package graphics.scripting

import graphics.scripting.host.SomeTestClass
import kotlinx.coroutines.CoroutineScope
import java.awt.Graphics
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.*
import kotlin.script.experimental.jvm.dependenciesFromClassContext
import kotlin.script.experimental.jvm.jvm

const val fileExtension = "graphics.kts"

@KotlinScript(
    displayName = "Graphics script",
    fileExtension = fileExtension,
    compilationConfiguration = GraphicsScriptCompilationConfiguration::class
)
abstract class GraphicsScript(val graphics: Graphics, val mainScope: CoroutineScope) : CoroutineScope by mainScope

internal object GraphicsScriptCompilationConfiguration : ScriptCompilationConfiguration({
    implicitReceivers(Graphics::class)

    jvm {
        dependenciesFromClassContext(
            GraphicsScript::class,
            "scripting",
            "graphics",
            "kotlin-stdlib",
            "kotlinx-coroutines-core"
        )
    }

    ide {
        acceptedLocations(ScriptAcceptedLocation.Everywhere)
    }

    defaultImports(
        "java.awt.Color",
        "java.awt.Color.*"
    )
})