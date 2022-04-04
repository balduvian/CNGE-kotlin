package game.tile

import com.balduvian.cnge.core.util.Color.Companion.uniformColor
import com.balduvian.cnge.graphics.Camera
import game.Colors
import game.GameResources
import game.GameUtil
import game.GameUtil.Direction
import game.Tile

class Pipe : Tile() {
	var orientation = Direction.RIGHT

	override fun passableFrom(direction: Direction): Boolean {
		return direction == orientation || direction == orientation.opposite()
	}

	override fun passableTo(direction: Direction): Boolean {
		return direction == orientation || direction == orientation.opposite()
	}

	override fun render(camera: Camera, x: Float, y: Float, width: Float, height: Float, ghost: Boolean) {
		Blank.renderBlank(camera, x, y, width, height)

		GameResources.gridShader.get().enable(
			camera.projectionView,
			Camera.transform(x + width / 2.0f, y + height / 2.0f, width, height, orientation.rotation)
		)
			.uniformColor(0, Colors.wallBorder, if (ghost) 0.5f else 1.0f)
		GameResources.pipe.get().render()
	}

	override fun clone(): Tile {
		return Pipe()
	}

	override fun rightClick() {
		orientation = Direction.values()[(orientation.ordinal + 1) % Direction.values().size]
	}
}
