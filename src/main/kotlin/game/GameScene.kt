package game

import com.balduvian.cnge.core.Scene
import com.balduvian.cnge.core.util.Color
import com.balduvian.cnge.core.util.Color.Companion.uniformColor
import com.balduvian.cnge.core.util.Frame
import com.balduvian.cnge.core.util.frame.AspectFrame
import com.balduvian.cnge.graphics.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL46.*

class GameScene(val window: Window) : Scene() {
	val aspect = AspectFrame(16.0f / 9.0f)
	val camera = Camera().setOrtho(160f, 90f)

	val backgroundColor = Color.hex(0xdb4237)
	val color = Color.hex(0xeb4310)

	val level = Level()
	val player = Player(2.0f, 1.0f)

	override fun update(input: Input, timing: Timing) {
		camera.scaleX = 8f
		camera.scaleY = 8f
		camera.update()

		player.update(input, timing.time, level)
	}

	override fun render() {
		GameResources.colorShader.get().enable(Camera.defaultProjView, Camera.defaultModel)
			.uniformColor(0, backgroundColor)
		GameResources.rect.get().render()

		GameResources.noiseTestShader.get()
			.enableModel(
				camera.projectionView,
				Camera.transform(5.0f, 5.0f, 50.0f, 50.0f)
			)
			.uniformColor(0, color)

		GameResources.rect.get().render()

		level.render(camera)
		player.render(camera)
	}

	override fun onResize(bounds: Frame.Bounds) {

	}
}
