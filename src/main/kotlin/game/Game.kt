package game

import com.balduvian.cnge.core.ResourceLoader
import com.balduvian.cnge.core.SceneHolder
import com.balduvian.cnge.core.util.frame.AspectFrame
import com.balduvian.cnge.graphics.Loop
import com.balduvian.cnge.graphics.Timing
import com.balduvian.cnge.graphics.Window
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.*

class Game() {
	val window: Window
	val aspect = AspectFrame(16.0f / 9.0f)
	val loader = ResourceLoader()

	init {
		if (!Window.init(::println)) throw Exception("GLFW failed to initialize")

		window = Window.create(4, 6, true, true, "CNGE Demo", false, true)
			?: throw Exception("Window failed to initialize")

		window.setIcon(
			Window::class.java.getResource("/textures/CNGE-logo.png")
			?: throw Exception("Icon missing"),
		)
	}

	val splashHolder = SceneHolder(arrayOf(
		GameResources.rightTriangle,
		GameResources.rect,
		GameResources.colorShader,
	), null) { Splash(window) }

	val gameSceneHolder = SceneHolder(arrayOf(
		GameResources.rect,
		GameResources.noiseTestShader,
		GameResources.colorShader,
	), null) { GameScene(window) }

	var stage = 0

	init {
		Loop(window) { timing -> doLoop(window, timing) }.loop()
	}

	fun doLoop(window: Window, timing: Timing) {
		window.poll()
		val input = window.input

		if (input.keyPressed(GLFW.GLFW_KEY_ESCAPE)) {
			window.setShouldClose()
		}

		if (input.keyPressed(GLFW.GLFW_KEY_F11)) {
			window.setFullScreen(!window.full, true)
		}

		if (input.didResize) {
			val (x, y, w, h) = aspect.getBounds(window.width, window.height)
			glViewport(x, y, w, h)
			splashHolder.scene?.onResize(x, y, w, h)
		}

		loader.update()

		/* update */
		when (stage) {
			0 -> {
				splashHolder.update(input, timing, loader)
				gameSceneHolder.update(input, timing, loader)

				if (splashHolder.scene?.splashDone() == true) {
					splashHolder.unload(loader)
					stage = 1
				}
			}
			1 -> {
				gameSceneHolder.update(input, timing, loader)
			}
			else -> {}
		}

		glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
		glClear(GL_COLOR_BUFFER_BIT)

		/* render */
		when (stage) {
			0 -> {
				gameSceneHolder.render()
				splashHolder.render()
			}
			1 -> {
				gameSceneHolder.render()
			}
			else -> {}
		}

		window.postFrame()
		window.swap()
	}

}

fun main() {
	Game()
}
