package game.tile

import com.balduvian.cnge.graphics.Camera
import game.GameUtil
import game.Tile

class Hole: Tile() {
	override fun passableFrom(direction: GameUtil.Direction): Boolean {
		return false
	}

	override fun passableTo(direction: GameUtil.Direction): Boolean {
		return false
	}

	override fun render(camera: Camera, x: Float, y: Float, width: Float, height: Float, ghost: Boolean) {
		/* do not */
	}

	override fun clone(): Tile {
		return Hole()
	}
}
