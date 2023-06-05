package game.tile

import com.balduvian.cnge.core.util.Color.Companion.uniformColor
import com.balduvian.cnge.graphics.Camera
import game.Colors
import game.GameResources
import game.GameUtil.Direction
import game.Tile

class Apple : Tile() {
	override fun passableFrom(direction: Direction): Boolean {
		return true
	}

	override fun passableTo(direction: Direction): Boolean {
		return true
	}

	override fun render(camera: Camera, x: Float, y: Float, width: Float, height: Float, ghost: Boolean) {
		Blank.renderBlank(camera, x, y, width, height)

		GameResources.gridShader.get().enable(
			camera.projectionView,
			Camera.transform(x, y, width, height)
		)
			.uniformColor(0, Colors.appleStem, if (ghost) 0.5f else 1.0f)
			.uniformColor(1, Colors.appleBody, if (ghost) 0.5f else 1.0f)
		GameResources.apple.get().render()
	}

	override fun clone(): Tile {
		return Apple()
	}
}
