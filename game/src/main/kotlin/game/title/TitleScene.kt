package game.title

import com.balduvian.cnge.core.Scene
import com.balduvian.cnge.core.util.Color.Companion.uniformColor
import com.balduvian.cnge.core.util.Frame
import com.balduvian.cnge.graphics.Camera
import com.balduvian.cnge.graphics.Input
import com.balduvian.cnge.graphics.Timing
import game.*
import javax.imageio.ImageIO

class TitleScene: Scene() {
	companion object {
		val titlePaths: ArrayList<ArrayList<Move>>
		init {
			val image = ImageIO.read(TitleScene::class.java.getResource("/textures/title.png"))
			titlePaths = TitleSnaker.fromImage(image)
		}
	}

	val camera = Camera().setOrtho(GameScene.VIEW_WIDTH, GameScene.VIEW_HEIGHT)
	val snakes = ArrayList<Snake>()

	var shouldSwitchScenes = false
	var shouldBeTutorial = false

	val startButton = Button(GameScene.VIEW_WIDTH / 4.0f - 20f, 5f, 40.0f, 8.0f, "play")
	val tutorialButton = Button(3.0f * (GameScene.VIEW_WIDTH / 4.0f) - 20f, 5f, 40.0f, 8.0f, "tutorial")

	init {
		for (path in titlePaths) {
			snakes.add(Snake(path))
		}

		GameResources.introTheme.get().play()
	}

	override fun update(input: Input, timing: Timing) {
		camera.update()

		for (snake in snakes) {
			snake.update(timing.time)
		}

		if (tutorialButton.update(input)) {
			GameResources.introTheme.get().stop()
			shouldBeTutorial = true
			shouldSwitchScenes = true
		} else if (startButton.update(input)) {
			GameResources.introTheme.get().stop()
			shouldBeTutorial = false
			shouldSwitchScenes = true
		}
	}

	override fun render() {
		GameResources.colorShader.get().enable(Camera.defaultProjView, Camera.defaultModel)
			.uniformColor(0, Colors.uiDark)
		GameResources.rect.get().render()

		val levelWidth = 23
		val levelHeight = 12

		val tileSize = GameScene.VIEW_WIDTH / levelWidth
		val yOffset = GameScene.VIEW_HEIGHT - (levelHeight - 0.5f) * tileSize

		for (snake in snakes) {
			snake.render(camera, 0.0f, yOffset, tileSize)
		}

		startButton.render(camera)
		tutorialButton.render(camera)
	}

	override fun onResize(bounds: Frame.Bounds) {}
}
