plugins {
    id("org.jetbrains.intellij") version "0.4.14"
    kotlin("jvm")
}

dependencies {
    api(project(":scripting"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("scripting-common"))
    testImplementation("junit", "junit", "4.12")
}

intellij {
    version = "2019.3"

    setPlugins(
        "gradle",
        "org.jetbrains.kotlin:1.3.61-release-IJ2019.3-1",
        "IdeaVIM:0.54"
    )
}

tasks {

    fun publicationOf(projectName: String) =
        ":$projectName:publishLibraryPublicationToMavenLocal"

    named("runIde") {
        dependsOn(
            publicationOf("graphics"),
            publicationOf("scripting")
        )
    }
}
