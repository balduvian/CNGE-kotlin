package game.tile

import com.balduvian.cnge.core.util.Color.Companion.uniformColor
import com.balduvian.cnge.graphics.Camera
import game.Colors
import game.GameResources
import game.GameUtil.Direction
import game.Tile

class Portal : Tile() {
	override fun passableFrom(direction: Direction): Boolean {
		return true
	}

	override fun passableTo(direction: Direction): Boolean {
		return true
	}

	override fun render(camera: Camera, x: Float, y: Float, width: Float, height: Float, ghost: Boolean) {
		Blank.renderBlank(camera, x, y, width, height)

		GameResources.colorShader.get().enable(
			camera.projectionView,
			Camera.transform(x, y, width, height)
		)
			.uniformColor(0, Colors.portalEdge, if (ghost) 0.5f else 1.0f)
		GameResources.circle.get().render()

		val border = 0.1f
		GameResources.colorShader.get().enable(
			camera.projectionView,
			Camera.transform(x + border * width, y + border * height, (1.0f - border * 2.0f) * width, (1.0f - border * 2.0f) * height)
		)
			.uniformColor(0, Colors.portalVoid, if (ghost) 0.5f else 1.0f)
		GameResources.circle.get().render()
	}

	override fun clone(): Tile {
		return Portal()
	}
}
