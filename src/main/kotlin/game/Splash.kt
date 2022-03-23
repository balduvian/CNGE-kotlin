package game

import com.balduvian.cnge.core.Scene
import com.balduvian.cnge.core.util.Color
import com.balduvian.cnge.core.util.Color.Companion.uniformColor
import com.balduvian.cnge.core.util.Timer
import com.balduvian.cnge.core.util.Util
import com.balduvian.cnge.core.util.Util.sinAlong
import com.balduvian.cnge.graphics.Camera
import com.balduvian.cnge.graphics.Input
import com.balduvian.cnge.graphics.Timing
import com.balduvian.cnge.graphics.Window
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt

class Splash(val window: Window) : Scene() {
	val timer = Timer().start(8.0)

	val width = 160.0f
	val height = 90.0f
	val camera = Camera().setOrtho(width, height)

	val logoColor = Color.hex(0x000000)
	val backgroundColor = Color.hex(0xffffff)

	/*
	 * base form:
	 *
	 * |\
	 * | \
	 * +-->
     *
	 * discrete number of rotations counterclockwise,
	 * in image coordinates (must flip y)
	 */
	val cngeLogoRotations = arrayOf(
		1, 3, 1, 0,
		2, 0, 3, 2,
		1, 3, 1, 0,
		2, 1, 2, 0,
	)

	val initialRotations = arrayOf(
		2, 0, 3, 1,
		0, 3, 1, 0,
		2, 0, 0, 3,
		0, 0, 3, 2
	)

	override fun update(input: Input, timing: Timing) {
		camera.update()
		timer.update(timing.time)
	}

	override fun render() {
		val elapsed = timer.timer.toFloat()

		val rotateAlong = Util.invInterp(1.0f, 3.0f, elapsed).coerceIn(0.0f..1.0f).sinAlong()
		val fadeAlong = Util.invInterp(6.0f, 8.0f, elapsed).coerceIn(0.0f..1.0f).sinAlong()

		GameResources.colorShader.get().enable(Camera.defaultProjView, Camera.defaultModel)
			.uniformColor(0, backgroundColor, 1.0f - fadeAlong)
		GameResources.rect.get().render()

		val triangleSize = 12.5f
		val size = Util.interp(0.0f, triangleSize, rotateAlong)

		val yOffset = 10.0f

		val startX = width / 2 - (triangleSize * 2)
		val startY = height / 2  - (triangleSize * 2)

		for (y in 0..3) {
			for (x in 0..3) {
				val initialRotation = initialRotations[(3 - y) * 4 + x] * PI.toFloat() / 2.0f
				val rotation = cngeLogoRotations[(3 - y) * 4 + x] * PI.toFloat() / 2.0f

				GameResources.colorShader.get().enable(
					camera.projectionView,
					Camera.transform(
						startX + x * triangleSize + triangleSize * 0.5f,
						startY + y * triangleSize + triangleSize * 0.5f + yOffset,
						size,
						size,
						Util.interp(initialRotation, rotation, rotateAlong)
					)
				).uniformColor(0, logoColor, 1.0f - fadeAlong)
				GameResources.rightTriangle.get().render()
			}
		}
	}

	override fun onResize(x: Int, y: Int, width: Int, height: Int) {

	}

	fun splashDone(): Boolean {
		return timer.along() >= 1.0f
	}
}
