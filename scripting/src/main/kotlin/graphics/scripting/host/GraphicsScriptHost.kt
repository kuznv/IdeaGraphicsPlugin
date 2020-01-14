package graphics.scripting.host

import graphics.scripting.GraphicsScript
import graphics.scripting.SomeClass
import kotlinx.coroutines.CoroutineScope
import java.awt.Graphics
import kotlin.script.experimental.api.EvaluationResult
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.SourceCode
import kotlin.script.experimental.api.constructorArgs
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost

class SomeTestClass

class GraphicsScriptHost {

    private val scriptingHost = BasicJvmScriptingHost()

    /**
     * Evaluates the given Graphics script [sourceCode] against the given [graphics].
     */
    fun eval(
        sourceCode: SourceCode,
        graphics: Graphics,
        mainScope: CoroutineScope
    ): ResultWithDiagnostics<EvaluationResult> =
        scriptingHost.evalWithTemplate<GraphicsScript>(
            sourceCode,
            evaluation = {
                constructorArgs(graphics, SomeTestClass())
                //implicitReceivers(graphics)
            }
        )

    /**
     * Evaluates the given Graphics script [sourceCode] against the
     * given [graphics].
     */
    fun eval(
        sourceCode: String,
        graphics: Graphics,
        mainScope: CoroutineScope
    ) = eval(sourceCode.toScriptSource(), graphics, mainScope)
}