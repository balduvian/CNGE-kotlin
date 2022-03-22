package game

import com.balduvian.cnge.core.Scene
import com.balduvian.cnge.core.util.Color
import com.balduvian.cnge.core.util.Color.Companion.uniformColor
import com.balduvian.cnge.core.util.frame.AspectFrame
import com.balduvian.cnge.graphics.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL46.*

class GameScene(window: Window) : Scene(window) {
	val aspect = AspectFrame(16.0f / 9.0f)
	val camera = Camera().setOrtho(160f, 90f)

	val color = Color.hex(0xeb4310)

	init {
		glEnable(GL_BLEND)
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
	}

	override fun update(input: Input, timing: Timing) {
		camera.update()

		if (input.keyPressed(GLFW_KEY_ESCAPE)) {
			window.setShouldClose()
		}

		if (input.keyPressed(GLFW_KEY_F11)) {
			window.setFullScreen(!window.full, true)
		}
	}

	override fun render() {
		glClearColor(0f, 0f, 1f, 1f)
		glClear(GL_COLOR_BUFFER_BIT.or(GL_DEPTH_BUFFER_BIT))

		GameResources.noiseTestShader.get()
			.enableModel(
				camera.projectionView,
				Camera.transform(5.0f, 5.0f, 50.0f, 50.0f)
			)
			.uniformColor(0, color)

		GameResources.rect.get().render()
	}

	override fun onResize(width: Int, height: Int) {
		val (x, y, w, h) = aspect.getBounds(width, height)
		glViewport(x, y, w, h)
	}

	override fun switchScene(): Int? {
		return null
	}
}
