package game

import com.balduvian.cnge.core.Scene
import com.balduvian.cnge.core.util.Timer
import com.balduvian.cnge.core.util.frame.AspectFrame
import com.balduvian.cnge.graphics.*
import com.balduvian.cnge.core.util.Font
import com.balduvian.cnge.core.util.Util
import org.joml.Math.sin
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL46.*
import kotlin.math.PI
import kotlin.random.Random
import kotlin.system.exitProcess

class GameScene(window: Window) : Scene(window) {
	val aspect = AspectFrame(16.0f / 9.0f)

	val hudCamera = Camera().setOrtho(160f, 90f)

	var cameraX = 0.0f
	var cameraY = 0.0f
	var cameraZ = 0.0f

	override fun update(input: Input, timing: Timing) {
		hudCamera.update()

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
	}

	override fun onResize(width: Int, height: Int) {
		val (x, y, w, h) = aspect.getBounds(width, height)
		glViewport(x, y, w, h)
	}

	override fun switchScene(): Int? {
		return null
	}
}
