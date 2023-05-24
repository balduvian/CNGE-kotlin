
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.concurrent.Callable
import org.gradle.api.tasks.Input
import kotlin.io.path.Path
import java.io.FileWriter

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

	@TaskAction
	fun generate() {
		val kotlinDir = genSourceSet.java.srcDirs.find { file -> file.name == "kotlin" }
		if (kotlinDir == null || !kotlinDir.isDirectory) {
			project.logger.error("")
			return;
		}

		val newFile = kotlinDir.toPath().resolve("d.kt").toFile()
		val writer = FileWriter(newFile)

		writer.write("""
			fun skek(a: Int): Int {
				return a + 1
			}
		""".trimIndent())
		writer.close()
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
