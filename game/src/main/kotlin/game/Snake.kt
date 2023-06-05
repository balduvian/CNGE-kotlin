package game

import com.balduvian.cnge.core.util.Color.Companion.uniformColor
import com.balduvian.cnge.core.util.Timer
import com.balduvian.cnge.core.util.Vector
import com.balduvian.cnge.graphics.Camera

class Snake(val desiredMoves: ArrayList<Move>) {
	val path = ArrayList<Move>()

	val movementTimer = Timer()
	var currentMove: Move? = null

	init {
		netxMove()
	}

	fun moveTimeFromCost(cost: Int): Double {
		return when (cost) {
			0 -> 0.25
			1 -> 0.5
			else -> 1.0
		}
	}

	private fun netxMove() {
		val lastMove = currentMove
		if (lastMove != null) {
			path.add(lastMove)
		}

		val nextIndex = path.size
		currentMove = if (nextIndex < desiredMoves.size) {
			val move = desiredMoves[nextIndex]
			movementTimer.start(moveTimeFromCost(move.cost))
			move

		} else {
			/* snake has completed all the desired moves */
			null
		}
	}

	fun getPathLength(): Int {
		return path.fold(0) { total, move -> total + move.cost }
	}

	fun update(time: Double) {
		if (currentMove != null && movementTimer.update(time)) {
			netxMove()
		}
	}

	/* in grid coordinate space, not rendered coordinates */
	private val snakeSize = 0.6f
	private val snakeOffset = (1.0f - snakeSize) / 2f

	fun render(camera: Camera, x: Float, y: Float, tileSize: Float) {
		/* render the origin */
		val (ox, oy) = desiredMoves.first()
		GameResources.colorShader.get().enable(camera.projectionView, Camera.transform(
			x + (ox + snakeOffset) * tileSize , y + (oy + snakeOffset) * tileSize,
			snakeSize * tileSize, snakeSize * tileSize
		))
		/* do not care about direction */
			.uniformColor(0, Colors.snakeStart)
		GameResources.rect.get().render()

		fun renderMove(
			i: Int,
			move: Move,
			moveAlong: Float,
		) {
			val origin = Vector(move.fromX + 0.5f, move.fromY + 0.5f).setAdd(Vector(snakeSize / 2.0f, 0.0f).setRotate(move.direction.rotation))

			val maxLength = snakeSize + 2.0f * snakeOffset

			val colorAlong0 = i.toFloat() / desiredMoves.size
			val colorAlong1 = (i.toFloat() + 1.0f) / desiredMoves.size

			GameResources.gradientShader.get().enable(camera.projectionView, Camera.transform(
				x + origin.x * tileSize, y + origin.y * tileSize,
				maxLength * moveAlong * tileSize, snakeSize * tileSize,
				move.direction.rotation
			))
				.uniformColor(0, Colors.getSnakeColor(colorAlong0))
				.uniformColor(1, Colors.getSnakeColor(colorAlong1))
			GameResources.snakeRect.get().render()
		}

		/* render all completed moves */
		for (i in 0 until path.size) {
			renderMove(i, path[i], 1.0f)
		}

		/* render ongoing move */
		val move = currentMove
		if (move != null) {
			renderMove(path.size, move, movementTimer.along())
		}
	}
}
