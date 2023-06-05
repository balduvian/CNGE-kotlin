package game

import com.balduvian.cnge.core.Resource
import com.balduvian.cnge.graphics.Window
import java.awt.image.BufferedImage
import java.io.File
import java.net.URISyntaxException
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile
import javax.imageio.ImageIO
import kotlin.collections.ArrayList


class LevelResource(val imagePath: String) : Resource<Level>() {
	var image: BufferedImage? = null

	override fun internalAsyncLoad() {
		image = ImageIO.read(Window::class.java.getResource(imagePath))
	}

	override fun internalSyncLoad(): Level {
		return Level.loadFromImage(image!!)
	}

	override fun cleanup() {
		image = null
	}

	companion object {
		fun getJarFolder(path: String): ArrayList<String> {
			val ret = ArrayList<String>()
			val jarFile = File(this::class.java.protectionDomain.codeSource.location.path)

			if (jarFile.isFile) {  // Run with JAR file
				println("jarfile")
				val jar = JarFile(jarFile)
				val entries: Enumeration<JarEntry> = jar.entries() //gives ALL entries in jar
				while (entries.hasMoreElements()) {
					val name: String = "/" + entries.nextElement().name
					//println("name: ${name}")
					if (name.startsWith(path)) { //filter according to the path
						ret.add(name.substringAfter(path))
					}
				}
				jar.close()
			} else { // Run with IDE
				val url = this::class.java.getResource(path)
				if (url != null) {
					try {
						val apps: File = File(url.toURI())
						for (app in apps.listFiles()) {
							ret.add(app.name)
						}
					} catch (ex: URISyntaxException) {
						// never happens
					}
				}
			}

			return ret
		}

		fun discoverLevels(levelsPath: String): Array<LevelResource> {
			//val directoryURL = Window::class.java.getResource(levelsPath)
			//val directory = File(directoryURL.toURI())

			val paths = getJarFolder(levelsPath)

			println(paths.size)
			for (path in paths) {
				println(path)
			}

			val levelNumbers = paths.mapNotNull { name ->
				val number = name.filter { it in '0'..'9' }.toIntOrNull()
				if (number == null) null else "$levelsPath${name}" to number
			} as ArrayList<Pair<String, Int>>

			levelNumbers.sortBy { it.second }

			return Array(levelNumbers.size) { i ->
				LevelResource(levelNumbers[i].first)
			}
		}
	}
}
