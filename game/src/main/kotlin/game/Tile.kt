package game

import com.balduvian.cnge.graphics.Camera

abstract class Tile {
	abstract fun passableFrom(direction: GameUtil.Direction): Boolean
	abstract fun passableTo(direction: GameUtil.Direction): Boolean

	abstract fun render(camera: Camera, x: Float, y: Float, width: Float, height: Float, ghost: Boolean)

	abstract fun clone(): Tile

	open fun travelCost(): Int {
		return 1
	}

	open fun rightClick() {}
}
