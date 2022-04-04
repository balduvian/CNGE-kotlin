package game

import com.balduvian.cnge.core.Resource
import com.balduvian.cnge.graphics.Window
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

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
		fun discoverLevels(levelsPath: String): Array<LevelResource> {
			val directoryURL = Window::class.java.getResource(levelsPath)
			val directory = File(directoryURL.toURI())

			val levelNumbers = directory.listFiles().mapNotNull { file ->
				val number = file.nameWithoutExtension.filter { it in '0'..'9' }.toIntOrNull()
				if (number == null) null else "$levelsPath${file.name}" to number
			} as ArrayList<Pair<String, Int>>

			levelNumbers.sortBy { it.second }

			return Array(levelNumbers.size) { i ->
				LevelResource(levelNumbers[i].first)
			}
		}
	}
}
