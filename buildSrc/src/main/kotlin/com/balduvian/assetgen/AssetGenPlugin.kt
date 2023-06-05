package com.balduvian.assetgen

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.net.URL
import java.net.URLClassLoader

lateinit var mainSourceSet: SourceSet
lateinit var genSourceSet: SourceSet

open class Extension(
	var unused: String = "______",
)

val Project.sourceSets: SourceSetContainer
	get() = project.extensions.getByType(JavaPluginExtension::class.java).sourceSets

abstract class GenerateTask : DefaultTask() {
	@get:Input
	abstract var mainSourceSet: SourceSet
	@get:Input
	abstract var genSourceSet: SourceSet
	@get:Input
	abstract var resourceGeneratorNames: Array<String>

	@TaskAction
	fun generate() {
		project.logger.error(genSourceSet.java.srcDirs.toList().joinToString { it.path })

		val outputDir = genSourceSet.java.srcDirs.find { file -> file.name == "kotlin" }
		if (outputDir == null) {
			project.logger.error("could not find kotlin directory")
			return
		}
		if (!outputDir.exists()) outputDir.mkdirs()

		val resourcesPath = mainSourceSet.resources.srcDirs.first().toPath()

		val classLoader = URLClassLoader(classpathToURLs(mainSourceSet.compileClasspath))

		resourceGeneratorNames.forEach { resourceGeneratorName ->
			val generatorClass = classLoader.loadClass(resourceGeneratorName).kotlin

			val puppetedGenerator = PuppetedGenerator.create(generatorClass)

			val folder = resourcesPath.resolve(puppetedGenerator.getFolder()).toFile()

			if (!folder.exists() || !folder.isDirectory) {
				project.logger.error("could not find resources folder \"${folder.path}\"")
				return@forEach
			}

			folder.listFiles()!!.forEach { file ->
				puppetedGenerator.generate(file, outputDir)
			}
		}
	}

	fun classpathToURLs(classpath: FileCollection): Array<out URL> {
		fun Iterable<File>.toUrls(): Sequence<URL> = asSequence().map { it.toURI().toURL() }

		return mutableListOf<URL>().apply {
			classpath.let { it.toUrls().forEach { add(it) } }
		}.toTypedArray().apply { project.logger.debug("Classpath for generator: ${this.joinToString("\n") { it.toString() }}") }
	}
}

class AssetGenPlugin : Plugin<Project> {
	override fun apply(project: Project) {
		project.extensions.create("assetgen", Extension::class.java)

		project.tasks.create("genassets", GenerateTask::class.java).apply {

		val copyClass =
			dependsOn()
		}

		//project.dependencies.add(genSourceSet.implementationConfigurationName, project.files(Callable { File("f") }))
	}


}
