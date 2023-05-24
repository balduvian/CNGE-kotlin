package game.tile

import com.balduvian.cnge.core.util.Color
import com.balduvian.cnge.core.util.Color.Companion.uniformColor
import com.balduvian.cnge.graphics.Camera
import game.Colors
import game.GameResources
import game.GameUtil
import game.Tile
import kotlin.math.PI

abstract class AbstractGoal(
	direction: GameUtil.Direction,
	val borderColor: Color,
	val innerColor: Color,
): Tile() {
	override fun passableFrom(direction: GameUtil.Direction): Boolean {
		return true
	}

	override fun passableTo(direction: GameUtil.Direction): Boolean {
		return true
	}

	private fun getRotation(direction: GameUtil.Direction) = when(direction) {
		GameUtil.Direction.RIGHT -> 0.0f
		GameUtil.Direction.UP -> GameUtil.FPI / 2.0f
		GameUtil.Direction.LEFT -> GameUtil.FPI
		GameUtil.Direction.DOWN -> -GameUtil.FPI / 2.0f
	}

	private val rotation = getRotation(direction)

	override fun render(camera: Camera, x: Float, y: Float, width: Float, height: Float, ghost: Boolean) {
		GameResources.gridShader.get().enable(camera.projectionView, Camera.transform(x, y, width, height))
			.uniformColor(0, borderColor)
			.uniformColor(1, innerColor)
		GameResources.gridPiece.get().render()
	}

	fun renderGradient(camera: Camera, x: Float, y: Float, width: Float, height: Float) {
		GameResources.gradientShader().enable(camera.projectionView, Camera.transform(x + width / 2.0f, y + height / 2.0f, width, height, rotation))
			.uniformColor(0, Colors.background)
			.uniformColor(1, Colors.background, 0.0f)
		GameResources.centerRect().render()
	}

	companion object {
		fun getDirection(pos: Pair<Int, Int>, width: Int): GameUtil.Direction {
			val (ex, ey) = pos
			return when {
				ex == -1 -> GameUtil.Direction.RIGHT
				ex == width -> GameUtil.Direction.LEFT
				ey == -1 -> GameUtil.Direction.UP
				else -> GameUtil.Direction.DOWN
			}
		}
	}
}

class Entrance(
	val pos: Pair<Int, Int>, val width: Int
): AbstractGoal(
	getDirection(pos, width),
	Colors.entranceBorder,
	Colors.entrance,
) {
	override fun clone(): Tile {
		return Entrance(pos, width)
	}
}

class Exit(
	val pos: Pair<Int, Int>, val width: Int
): AbstractGoal(
	getDirection(pos, width),
	Colors.exitBorder,
	Colors.exit,
) {
	override fun clone(): Tile {
		return Entrance(pos, width)
	}
}
