package graphics

import com.intellij.AppTopics
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManagerListener
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.impl.PsiManagerEx
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.layout.panel
import com.intellij.util.messages.MessageBusConnection
import graphics.scripting.GraphicsScript
import graphics.scripting.fileExtension
import graphics.scripting.host.GraphicsScriptHost
import graphics.swing.withGraphics
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import java.awt.Canvas
import java.awt.Dimension
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicReference
import javax.swing.JLabel
import kotlin.coroutines.EmptyCoroutineContext


class GraphicsPreviewToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        GraphicsPreview(project).run {

            val contentFactory = ContentFactory.SERVICE.getInstance()
            val content = contentFactory.createContent(ui, "", false)

            toolWindow.contentManager.addContent(content)

            invokeLater {
                project.messageBus.connect(content).apply {
                    attach()
                }
            }
        }
    }
}


private class GraphicsPreview(val project: Project) {

    private val graphicsHost = GraphicsScriptHost()

    private val size = AtomicReference(Dimension(400, 400))

    private var selectedFile: VirtualFile? = selectedFile()

    private val activeEditorLabel = JLabel(computeLabel())

    private val canvas = Canvas()

    private val executor = Executor { ApplicationManager.getApplication().invokeLater(it) }

    private val coroutineScope = CoroutineScope(executor.asCoroutineDispatcher())

    val ui = panel {
        row {
            activeEditorLabel()
        }
        row {
            scrollPane(canvas).component.addComponentListener(
                object : ComponentAdapter() {
                    override fun componentResized(e: ComponentEvent) {
                        size.set(e.component.size)
                    }
                }
            )
        }
    }

    fun MessageBusConnection.attach() {
        subscribe(
            FileEditorManagerListener.FILE_EDITOR_MANAGER,
            object : FileEditorManagerListener {
                override fun selectionChanged(event: FileEditorManagerEvent) {
                    selectedFile = event.newEditor?.file
                        ?.takeIf { it.name.endsWith(".$fileExtension") }
                    selectedFileChanged()
                }
            }
        )
        subscribe(
            AppTopics.FILE_DOCUMENT_SYNC,
            object : FileDocumentManagerListener {
                override fun beforeDocumentSaving(document: Document) {
                    psiDocumentManager().getPsiFile(document)?.let { psiFile ->
                        if (psiFile.virtualFile == selectedFile) {
                            selectedFileContentsChanged(document.text)
                        }
                    }
                }
            }
        )
    }

    private fun selectedFileContentsChanged(text: String) {

        val dimension = size.get()

        ProgressManager.getInstance().executeNonCancelableSection {
            val newImage =
                withContextClassLoader(GraphicsScript::class.java.classLoader) {
                    withGraphics(dimension.width, dimension.height) { graphicsDsl ->
                        val result = graphicsHost.eval(text, graphicsDsl, coroutineScope)
                        println(result)
                    }
                }

            invokeLater {
                canvas.graphics.drawImage(newImage, 0, 0, null)
            }
        }
    }

    private fun selectedFileChanged() {
        val message = computeLabel()
        val selectedFileText = selectedFile?.let {
            PsiManagerEx.getInstanceEx(project).findFile(it)?.let { selectedPsiFile ->
                psiDocumentManager().getDocument(selectedPsiFile)?.text
            }
        }

        invokeLater {
            activeEditorLabel.text = message
        }
        selectedFileContentsChanged(selectedFileText ?: "")
    }

    private fun computeLabel() = selectedFile?.name ?: "no file"

    private fun selectedFile() = FileEditorManager.getInstance(project).selectedEditor?.file

    private fun psiDocumentManager() = PsiDocumentManager.getInstance(project)
}

private fun invokeLater(block: () -> Unit) =
    ApplicationManager.getApplication().invokeLater(block)

private inline fun <T> withContextClassLoader(classLoader: ClassLoader, block: () -> T): T {
    val previous = Thread.currentThread().contextClassLoader
    Thread.currentThread().contextClassLoader = classLoader
    try {
        return block()
    } finally {
        Thread.currentThread().contextClassLoader = previous
    }
}
