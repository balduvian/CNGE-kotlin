package game

import com.balduvian.cnge.core.util.Color.Companion.uniformColor
import com.balduvian.cnge.graphics.Camera
import com.balduvian.cnge.graphics.Input
import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1

class Button(
	var x: Float,
	var y: Float,
	var width: Float,
	var height: Float,
	var text: String,
) {
	companion object {
		private val font = GameFont()
		init { font.colors.add(Colors.buttonText) }
	}

	var highlighted = false

	/**
	 * @return if it was interacted with
	 */
	fun update(input: Input): Boolean {
		val (cursorX, cursorY) = GameUtil.screenToGameCoords(input)

		if (cursorX in x..x + width && cursorY in y..y + height) {
			highlighted = true
			if (input.buttonPressed(GLFW_MOUSE_BUTTON_1)) {
				GameResources.select.get().play()
				return true
			}

		} else {
			highlighted = false
		}

		return false
	}

	fun render(camera: Camera) {
		GameResources.gridShader.get().enable(camera.projectionView, Camera.transform(x, y, width, height))
			.uniformColor(0, Colors.wallBorder)
			.uniformColor(1, if (highlighted) Colors.buttonHighlight else Colors.button)
		GameResources.gridPiece.get().render()

		font.colors[0] = if (highlighted) Colors.buttonHighlightText else Colors.buttonText
		font.renderString(camera, x + width / 2.0f, y + (height - 3f) / 2.0f, 0.5f, 3f, true, text)
	}
}
