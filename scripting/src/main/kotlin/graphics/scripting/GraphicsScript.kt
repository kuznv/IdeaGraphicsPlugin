package graphics.scripting

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
abstract class GraphicsScript(val graphics: Graphics)

internal object GraphicsScriptCompilationConfiguration : ScriptCompilationConfiguration({

    jvm {
        dependenciesFromClassContext(
            GraphicsScript::class,
            "scripting",
            "graphics",
            "kotlin-stdlib"
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