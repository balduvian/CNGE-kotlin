package game

import com.balduvian.cnge.core.Scene
import com.balduvian.cnge.core.frame.AspectFrame
import com.balduvian.cnge.graphics.*
import org.joml.Math
import org.joml.Vector2f
import org.lwjgl.glfw.GLFW.GLFW_KEY_Q
import org.lwjgl.glfw.GLFW.GLFW_KEY_V
import org.lwjgl.opengl.GL46.*
import kotlin.math.PI
import kotlin.random.Random

class GameScene(window: Window) : Scene(window) {
	val aspect = AspectFrame(16.0f / 9.0f)

	val frameBuffer: FrameBuffer
	init {
		val (_, _, w, h) = aspect.getBounds(window.width, window.height)
		frameBuffer = FrameBuffer.create(w, h, TextureParams().filter(GL_LINEAR))
	}

	val camera = Camera().setOrtho(16.0f, 9.0f)
	val frameCamera = Camera()

	val colorStartAngle = Random.nextFloat() * PI * 2f
	val angleAdvance = PI * 2f / 3f
	val deviance = 1f / (48f * 2)
	val redOffset = Pair(Math.cos(colorStartAngle).toFloat() * deviance, Math.sin(colorStartAngle).toFloat() * deviance)
	val greOffset = Pair(
		Math.cos(colorStartAngle + angleAdvance).toFloat() * deviance, Math.sin(colorStartAngle + angleAdvance).toFloat() * deviance)
	val bluOffset = Pair(
		Math.cos(colorStartAngle + angleAdvance * 2).toFloat() * deviance, Math.sin(colorStartAngle + angleAdvance * 2).toFloat() * deviance)
	val scanLines = 50f
	val scanOffset = 1f / 96f

	var vhsEnabled = false

	var player = Player(30f, 80f)

	val lineSet = LineSet(arrayOf(
		Vector(10f, 45f),
		Vector(150f, 45f),
		Vector(130f, 35f),
		Vector(100f, 30f),
		Vector(90f, 10f),
		Vector(80f, 20f),
		Vector(60f, 25f),
		Vector(40f, 30f),
		Vector(30f, 20f),
	))

	override fun update(input: Input, timing: Timing) {
		camera.update()

		if (input.keypressed(GLFW_KEY_Q)) {
			player.x = 20f
			player.y = 80f
		}

		player.update(input, timing.time.toFloat(), arrayOf(lineSet))

		val (cl, cr, cb, ct) = CameraPositioner.position(16.0f / 9.0f, 20f, -50f, 210f, -50f, 160f, arrayOf(Vector(player.x, player.y), Vector(80f, 45f)))

		frameCamera.setOrtho(cl, cr, cb, ct)
		frameCamera.update()

		if (input.keypressed(GLFW_KEY_V)) {
			vhsEnabled = !vhsEnabled
		}
	}

	override fun render() {
		glEnable(GL_BLEND)
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

		frameBuffer.enable()

		glClearColor(0.2f, 0.0f, 0.6f, 1.0f)
		glClear(GL_COLOR_BUFFER_BIT)

		lineSet.render(frameCamera, 1f)

		player.render(frameCamera)

		FrameBuffer.enableDefault()
		val (x, y, w, h) = aspect.getBounds(window.width, window.height)
		glViewport(x, y, w, h)

		glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
		glClear(GL_COLOR_BUFFER_BIT)

		frameBuffer.texture.bind()

		if (vhsEnabled) {
			GameResources.vhsShader.get().enable(camera.projectionView, Camera.transform(0f, 0f, 16.0f, 9.0f))
			GameResources.vhsShader.get().uniformFloat(0, frameCamera.width / frameCamera.height)
			GameResources.vhsShader.get().uniformVector2(1, redOffset.first, redOffset.second)
			GameResources.vhsShader.get().uniformVector2(2, bluOffset.first, bluOffset.second)
			GameResources.vhsShader.get().uniformVector2(3, greOffset.first, greOffset.second)
			GameResources.vhsShader.get().uniformFloat(4, scanLines)
			GameResources.vhsShader.get().uniformFloat(5, scanOffset)
			GameResources.vhsShader.get().uniformFloat(6, Random.nextFloat())
		} else {
			GameResources.textureShader.get().enable(camera.projectionView, Camera.transform(0f, 0f, 16.0f, 9.0f))
			GameResources.textureShader.get().uniformVector4(0, 1f, 1f, 1f, 1f)
		}

		GameResources.frameRect.get().render()
	}

	override fun onResize(width: Int, height: Int) {
		val (_, _, w, h) = aspect.getBounds(width, height)
		frameBuffer.resize(w, h)
	}

	override fun switchScene(): Int? {
		return null
	}
}
