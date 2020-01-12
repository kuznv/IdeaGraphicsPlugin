package graphics.scripting.host

import graphics.scripting.GraphicsScript
import java.awt.Graphics
import java.lang.StringBuilder
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost

class GraphicsScriptHost {

    private val scriptingHost = BasicJvmScriptingHost()

    /**
     * Evaluates the given Graphics script [sourceCode] against the given [graphics].
     */
    fun eval(
        sourceCode: SourceCode,
        graphics: Graphics
    ): ResultWithDiagnostics<EvaluationResult> =
        scriptingHost.evalWithTemplate<GraphicsScript>(
            sourceCode,
            compilation = {
                implicitReceivers(Graphics::class)
            },
            evaluation = {
                constructorArgs(graphics)
                implicitReceivers(graphics)
            }
        )

    /**
     * Evaluates the given Graphics script [sourceCode] against the
     * given [graphics].
     */
    fun eval(
        sourceCode: String,
        graphics: Graphics
    ) = eval(sourceCode.toScriptSource(), graphics)
}