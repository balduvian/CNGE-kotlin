package game

import com.balduvian.cnge.graphics.Input
import kotlin.math.PI

object GameUtil {
	enum class Direction(val x: Int, val y: Int, val rotation: Float) {
		RIGHT(1, 0, 0.0f),
		UP(0, 1, FPI / 2.0f),
		LEFT(-1, 0, FPI),
		DOWN(0, -1, -FPI / 2.0f);

		fun opposite(): Direction {
			return when(this) {
				RIGHT -> LEFT
				UP -> DOWN
				LEFT -> RIGHT
				DOWN -> UP
			}
		}

		companion object {
			fun fromOffset(offsetX: Int, offsetY: Int): Direction? {
				return when {
					offsetX ==  1 && offsetY ==  0 -> RIGHT
					offsetX == -1 && offsetY ==  0 -> LEFT
					offsetX ==  0 && offsetY ==  1 -> UP
					offsetX ==  0 && offsetY == -1 -> DOWN
					else -> null
				}
			}
		}
	}

	val FPI = PI.toFloat()

	fun screenToGameCoords(input: Input): Pair<Float, Float> {
		return Pair(
			(input.mouseX - input.bounds.x).toFloat() * (GameScene.VIEW_WIDTH / input.bounds.width),
			GameScene.VIEW_HEIGHT - (input.mouseY - input.bounds.y).toFloat() * (GameScene.VIEW_HEIGHT / input.bounds.height)
		)
	}
}
