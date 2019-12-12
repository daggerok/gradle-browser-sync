pluginManagement {
    plugins {
        repositories {
            gradlePluginPortal()
        }
        val nodePluginVersion: String by extra
        id("com.github.node-gradle.node") version nodePluginVersion
        // val versionsPluginVersion: String by extra
        // id("com.github.ben-manes.versions") version versionsPluginVersion
    }
}

val name: String by extra
rootProject.name = name
