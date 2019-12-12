plugins {
  base
  idea
  id("com.github.node-gradle.node")
  // id("com.github.ben-manes.versions")
}

val projectNpmVersion: String by extra
val projectNodeVersion: String by extra
val projectGradleVersion: String by extra

node {
  download = true
  version = projectNodeVersion
  npmVersion = projectNpmVersion
  workDir = file("$buildDir/nodejs")
  npmWorkDir = file("$buildDir/npm")
  // distBaseUrl = "https://nodejs.org/dist"
  nodeModulesDir = file("$buildDir/gradle-browser-sync")
}

tasks {
  val tasksGroup = "Gradle Browser Sync"
  register("browserSyncInstall") {
    group = tasksGroup
    description = "Install in ${node.nodeModulesDir.absolutePath} folder NodeJS, npm package manager and browser-sync."
    dependsOn("npm_i_-ED_browser-sync")
    shouldRunAfter("npm_i_-ED_browser-sync")
  }
  val windows = org.apache.tools.ant.taskdefs.condition.Os.isFamily(org.apache.tools.ant.taskdefs.condition.Os.FAMILY_WINDOWS)
  val webappDir = java.nio.file.Paths.get(projectDir.absolutePath, "src", "main", "webapp").toFile().absolutePath
  register("browserSyncWebappDir", Exec::class) {
    group = tasksGroup
    description = "Create 'src/main/webapp' directory browser-sync will serve."
    if (windows) commandLine("mkdir", webappDir)
    else commandLine("mkdir", "-p", webappDir)
  }
  register(name = "browserSyncStart", type = Exec::class) {
    val binDir = java.nio.file.Paths.get(node.nodeModulesDir.absolutePath, "node_modules", ".bin").toFile()
    // we cna use either --single or --directory
    val arguments = "start --watch --no-open --directory --files *.* --cors --serveStatic $webappDir --server $webappDir"
    group = tasksGroup
    description = "Run platform specific 'browser-sync $arguments' command."
    dependsOn("browserSyncWebappDir")
    shouldRunAfter("browserSyncInstall", "browserSyncWebappDir")
    if (windows) commandLine("cmd", "/k", "$binDir\\browser-sync.exe $arguments")
    else commandLine("bash", "-c", "$binDir/browser-sync $arguments")
  }
  withType<Wrapper> {
    gradleVersion = projectGradleVersion
  }
}
