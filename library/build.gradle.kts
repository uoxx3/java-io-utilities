import generator.TaskBuildInfoGenerator
import project.tasking.fromSourceSets
import project.tasking.fromTasks
import publish.createFromSpec
import publish.getProjectInfo

plugins {
  `java-library`
  alias(libs.plugins.org.javamodularity.module)
}

/* -----------------------------------------------------
 * Project configuration
 * ----------------------------------------------------- */

val projectSpec = getProjectInfo()

group = projectSpec.group
version = projectSpec.version

/* -----------------------------------------------------
 * Java configuration
 * ----------------------------------------------------- */

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = sourceCompatibility
}

/* -----------------------------------------------------
 * Publishing configuration
 * ----------------------------------------------------- */

afterEvaluate {
  publishing.publications {
    // Generate maven publications
    createFromSpec(projectSpec, project)
  }
}

/* -----------------------------------------------------
 * Task configurations
 * ----------------------------------------------------- */

val buildInfoGen by tasks.registering(TaskBuildInfoGenerator::class) {
  projectName = projectSpec.name
  groupName = projectSpec.group
  // Build info configuration
  projectSpec.buildInfo?.let {
    moduleName = it.outPackage
    filename = it.filename
  }
}

val javadocJar by tasks.registering(Jar::class) {
  group = "publishing"
  archiveClassifier.set("javadoc")
  
  fromTasks("javadoc")
}

val sourceJar by tasks.registering(Jar::class) {
  group = "publishing"
  archiveClassifier.set("sources")
  
  fromSourceSets("main") {
    return@fromSourceSets it.java.srcDirs
  }
}

tasks.named("processResources") {
  dependsOn(buildInfoGen)
}

tasks.test {
  useJUnitPlatform()
}

/* -----------------------------------------------------
 * Dependencies
 * ----------------------------------------------------- */

dependencies {
  compileOnly(libs.java.org.jetbrains.annotations)
  
  // Testing libraries
  testImplementation(platform(libs.java.org.junit.jupiter.bom))
  testImplementation(libs.java.org.junit.jupiter.jupiter)
  testImplementation(libs.java.org.junit.jupiter.engine)
  
  // Testing Runtime/Compile
  testCompileOnly(libs.java.org.jetbrains.annotations)
  testCompileOnly(libs.java.org.junit.jupiter.runtime)
}