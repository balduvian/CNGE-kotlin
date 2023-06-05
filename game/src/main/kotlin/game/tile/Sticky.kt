package game.tile

import com.balduvian.cnge.core.util.Color.Companion.uniformColor
import com.balduvian.cnge.graphics.Camera
import game.Colors
import game.GameResources
import game.GameUtil
import game.Tile

class Sticky : Tile() {
	override fun passableFrom(direction: GameUtil.Direction): Boolean {
		return true
	}

	override fun passableTo(direction: GameUtil.Direction): Boolean {
		return true
	}

	override fun render(camera: Camera, x: Float, y: Float, width: Float, height: Float, ghost: Boolean) {
		GameResources.gridShader.get().enable(camera.projectionView, Camera.transform(x, y, width, height))
			.uniformColor(0, Colors.grid, if (ghost) 0.5f else 1.0f)
			.uniformColor(1, Colors.sticky, if (ghost) 0.5f else 1.0f)
		GameResources.gridPiece.get().render()
	}

	override fun clone(): Tile {
		return Sticky()
	}

	override fun travelCost(): Int {
		return 2
	}
}
