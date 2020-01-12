package turtle.scripting

import turtle.Turtle
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.*
import kotlin.script.experimental.jvm.dependenciesFromClassContext
import kotlin.script.experimental.jvm.jvm

const val fileExtension = "turtle.kts"

@KotlinScript(
    displayName = "Turtle script",
    fileExtension = fileExtension,
    compilationConfiguration = TurtleScriptCompilationConfiguration::class
)
abstract class TurtleScript(turtle: Turtle) : Turtle by turtle

internal object TurtleScriptCompilationConfiguration : ScriptCompilationConfiguration({

    jvm {
        dependenciesFromClassContext(
            TurtleScript::class,
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