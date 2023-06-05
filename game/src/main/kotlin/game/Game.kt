package game

import com.balduvian.cnge.core.ResourceLoader
import com.balduvian.cnge.core.SceneHolder
import com.balduvian.cnge.core.util.frame.AspectFrame
import com.balduvian.cnge.graphics.Loop
import com.balduvian.cnge.graphics.Timing
import com.balduvian.cnge.graphics.Window
import com.balduvian.cnge.sound.ALManagement
import game.title.TitleScene
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.*

class Game {
	val window: Window
	val alManagement: ALManagement
	val aspectFrame = AspectFrame(16.0f / 9.0f)
	val loader = ResourceLoader()

	init {
		if (!Window.init(::println)) throw Exception("GLFW failed to initialize")

		alManagement = ALManagement()

		window = Window.create(4, 6, true, true, "Snakeline", false, true, aspectFrame)
			?: throw Exception("Window failed to initialize")

		window.setIcon(
			Window::class.java.getResource("/textures/snakeline-icon.png")
			?: throw Exception("Icon missing"),
		)
	}

	val splashHolder = SceneHolder(arrayOf(
		GameResources.rightTriangle,
		GameResources.rect,
		GameResources.colorShader,
		GameResources.tileShader,
		GameResources.fontTiles,
	), null) { Splash() }

	val gameSceneHolder = SceneHolder(arrayOf(
		GameResources.rect,
		GameResources.colorShader,
		GameResources.gridX,
		GameResources.gridPiece,
		GameResources.gridShader,
		GameResources.gradientShader,
		GameResources.centerRect,
		GameResources.snakeRect,
		GameResources.fontTiles,
		GameResources.tileShader,
		GameResources.inventoryShader,
		GameResources.pipe,
		GameResources.circle,
		GameResources.apple,
		GameResources.goTriangle,
		GameResources.checkerShader,
		GameResources.gameTheme,
		GameResources.select,
		GameResources.win,
		GameResources.lose,
		GameResources.tutorialLevel,
		*GameResources.levels,
	), null) { GameScene(openTutorial) }

	val titleSceneHolder = SceneHolder(arrayOf(
		GameResources.rect,
		GameResources.snakeRect,
		GameResources.gradientShader,
		GameResources.colorShader,
		GameResources.gridPiece,
		GameResources.gridShader,
		GameResources.fontTiles,
		GameResources.tileShader,
		GameResources.introTheme,
		GameResources.select,
	), null) { TitleScene() }

	var stage = 0
	var openTutorial = false

	init {
		splashHolder.syncLoad(loader)

		glEnable(GL_BLEND)
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

		Loop(window) { timing -> doLoop(window, timing) }.loop()

		alManagement.destroy()
	}

	private fun doLoop(window: Window, timing: Timing) {
		val input = window.poll()

		if (input.keyPressed(GLFW.GLFW_KEY_ESCAPE)) {
			window.setShouldClose()
		}

		if (input.keyPressed(GLFW.GLFW_KEY_F11)) {
			window.setFullScreen(!window.full, true)
		}

		loader.update()

		var switchStage: Int? = null

		/* update */
		when (stage) {
			0 -> {
				splashHolder.update(input, timing, loader)
				if (splashHolder.scene?.actionable() != true) {
					titleSceneHolder.updateLoadOnly(loader)
				} else {
					titleSceneHolder.update(input, timing, loader)
				}

				if (splashHolder.scene?.splashDone() == true) {
					splashHolder.unload(loader)
					switchStage = 1
				}
			}
			1 -> {
				openTutorial = false
				titleSceneHolder.update(input, timing, loader)
				if (titleSceneHolder.scene?.shouldSwitchScenes == true) {
					openTutorial = titleSceneHolder.scene!!.shouldBeTutorial
					titleSceneHolder.unload(loader)
					switchStage = 2
				}
			}
			2 -> {
				gameSceneHolder.update(input, timing, loader)
				if (gameSceneHolder.scene?.shouldSwitchScenes == true) {
					gameSceneHolder.unload(loader)
					switchStage = 1
				}
			}
		}

		glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
		glClear(GL_COLOR_BUFFER_BIT)

		/* render */
		when (stage) {
			0 -> {
				titleSceneHolder.render()
				splashHolder.render()
			}
			1 -> {
				titleSceneHolder.render()
			}
			2 -> {
				gameSceneHolder.render()
			}
		}

		window.postFrame()
		window.swap()

		if (switchStage != null) stage = switchStage
	}
}

fun main() {
	Game()
}
