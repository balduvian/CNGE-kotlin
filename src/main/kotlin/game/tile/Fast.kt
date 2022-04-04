package game.tile

import com.balduvian.cnge.core.util.Color.Companion.uniformColor
import com.balduvian.cnge.graphics.Camera
import game.Colors
import game.GameResources
import game.GameUtil
import game.Tile

class Fast : Tile() {
	override fun passableFrom(direction: GameUtil.Direction): Boolean {
		return true
	}

	override fun passableTo(direction: GameUtil.Direction): Boolean {
		return true
	}

	override fun render(camera: Camera, x: Float, y: Float, width: Float, height: Float, ghost: Boolean) {
		Blank.renderBlank(camera, x, y, width, height)

		GameResources.checkerShader.get().enable(camera.projectionView, Camera.transform(
			x + 0.1f * width, y + 0.1f * height, width * 0.8f, height * 0.8f
		))
			.uniformColor(0, Colors.checker0)
			.uniformColor(1, Colors.checker1)
		GameResources.rect.get().render()
	}

	override fun clone(): Tile {
		return Fast()
	}

	override fun travelCost(): Int {
		return 0
	}
}
