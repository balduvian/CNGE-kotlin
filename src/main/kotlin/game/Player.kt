package game

import com.balduvian.cnge.core.util.Color
import com.balduvian.cnge.core.util.Color.Companion.uniformColor
import com.balduvian.cnge.core.util.Timer
import com.balduvian.cnge.graphics.Camera
import com.balduvian.cnge.graphics.Input
import org.lwjgl.glfw.GLFW.*
import kotlin.math.floor

class Player(var x: Float, var y: Float) {
	val gravity = 20.0f

	val jumpStrength = 7.27f
	val maxJumpTime = 0.3

	val walkAccel = 32.0f
	val maxWalkSpeed = 8.0f

	var jumpHoldTimer = Timer()

	var xVel = 0.0f
	var yVel = 0.0f

	val color = Color.hex(0x6f031f)

	enum class Axis {
		X, Y
	}

	enum class Direction(val axis: Axis) {
		RIGHT(Axis.X), UP(Axis.Y), LEFT(Axis.X), DOWN(Axis.Y)
	}

	fun update(input: Input, time: Double, level: Level) {
		jumpHoldTimer.update(time)

		if (jumpHoldTimer.going) {
			if (!input.keyHeld(GLFW_KEY_SPACE)) {
				jumpHoldTimer.stop()
			}
		} else {
			if (input.keyPressed(GLFW_KEY_SPACE)) {
				jumpHoldTimer.start(maxJumpTime)
			}
		}

		if (jumpHoldTimer.going) {
			yVel = jumpStrength
		}

		var leftHeld = input.keyHeld(GLFW_KEY_A)
		var rightHeld = input.keyHeld(GLFW_KEY_D)

		if (leftHeld && rightHeld) {
			leftHeld = false
			rightHeld = false
		}

		if (leftHeld) {
			xVel -= walkAccel * time.toFloat()
		}
		if (rightHeld) {
			xVel += walkAccel * time.toFloat()
		}

		if (xVel < 0.0f && !leftHeld) {
			xVel += walkAccel * time.toFloat()
			if (xVel > 0.0f) xVel = 0.0f
		} else if (xVel > 0.0f && !rightHeld) {
			xVel -= walkAccel * time.toFloat()
			if (xVel < 0.0f) xVel = 0.0f
		}

		xVel = xVel.coerceIn(-maxWalkSpeed, maxWalkSpeed)
		yVel -= gravity * time.toFloat()

		/* collision */
		var endX = x + xVel * time.toFloat()
		var endY = y + yVel * time.toFloat()

		val offsetDL = Pair(0.0f, 0.0f)
		val offsetDR = Pair(1.0f, 0.0f)
		val offsetUL = Pair(0.0f, 1.0f)
		val offsetUR = Pair(1.0f, 1.0f)

		fun intersectionX(
			startX: Float, startY: Float, endX: Float, endY: Float, yLevel: Float,
		): Float {
			return (endX - startX) * ((yLevel - startY) / (endY - startY)) + startX
		}
		fun intersectionY(
			startX: Float, startY: Float, endX: Float, endY: Float, xLevel: Float,
		): Float {
			return (endY - startY) * ((xLevel - startX) / (endX - startX)) + startY
		}

		fun blockPos(entry: Float): Int {
			return floor(entry).toInt()
			//return if (entry.mod(1.0f) == 0.0f) {
			//	floor(entry).toInt() - 1
			//} else {
			//	floor(entry).toInt()
			//}
		}



		/**
		 * offset is preapplied, reoffset the result yourself
		 */
		fun subCollide(
			startX: Float,
			startY: Float,
			endX: Float,
			endY: Float,
			line: Float,
			direction: Direction,
		): Float? {
			val block0X = blockPos(endX)
			val block0Y = blockPos(endY)
			val block1X = blockPos(endX + line * if (direction.axis == Axis.Y) 1.0f else 0.0f)
			val block1Y = blockPos(endY + line * if (direction.axis == Axis.X) 1.0f else 0.0f)

			fun collisionCheck(
				i: Int,
				j: Int,
				wallStart: Float,
				wallEnd: Float,
				lineStart: Float,
				lineEnd: Float,
				moveStart: Float,
				moveEnd: Float,
				wall: Float,
				faceCheckX: Int,
				faceCheckY: Int,
				positive: Boolean,
			): Float? {
				return if (
					/* *comments describe the RIGHT scenario */
					( /* player line overlaps the wall line vertically */
						wallStart == lineStart || (
							wallStart > lineStart && wallStart < lineEnd
							) ||
							wallEnd == lineEnd || (
							wallEnd > lineStart && wallEnd < lineEnd
							)
						) &&
					(if (positive) moveEnd > wall else moveEnd < wall) && /* player ends up past the left wall of the block */
					(if (positive) moveStart <= wall else moveStart >= wall) && /* player started on or to the left of the wall */
					level.blockAt(i + faceCheckX , j + faceCheckY) != 1 /* block has an open face to the left */
				) {
					wall
				} else {
					null
				}
			}

			for (j in block0Y..block1Y) {
				for (i in block0X..block1X) {
					if (level.blockAt(i, j) == 1) {
						val collision = when(direction) {
							Direction.RIGHT -> collisionCheck(
								i, j, j + 0.0f, j + 1.0f, endY, endY + line, startX, endX, i + 0.0f, -1, 0, true
							)
							Direction.UP -> collisionCheck(
								i, j, i + 0.0f, i + 1.0f, endX, endX + line, startY, endY, j + 0.0f, 0, -1, true
							)
							Direction.LEFT -> collisionCheck(
								i, j, j + 0.0f, j + 1.0f, endY, endY + line, startX, endX, i + 1.0f, 1, 0, false
							)
							Direction.DOWN -> collisionCheck(
								i, j, i + 0.0f, i + 1.0f, endX, endX + line, startY, endY, j + 1.0f, 0, 1, false
							)
						}

						if (collision != null) return collision
					}
				}
			}

			return null
		}

		fun collideY(): Float? {
			return if (endY < y /* move downward */) {
				val (ox, oy) = 0.0f to 0.0f
				subCollide(x + ox, y + oy, endX + ox, endY + oy, 1.0f, Direction.DOWN)?.minus(oy)

			} else if (endY > y /* move upward */) {
				val (ox, oy) = 0.0f to 1.0f
				subCollide(x + ox, y + oy, endX + ox, endY + oy, 1.0f, Direction.UP)?.minus(oy)

			} else {
				null
			}
		}

		fun collideX(): Float? {
			return if (endX < x /* move left */) {
				val (ox, oy) = 0.0f to 0.0f
				subCollide(x + ox, y + oy, endX + ox, endY + oy, 1.0f, Direction.LEFT)?.minus(ox)

			} else if (endX > x /* move right */) {
				val (ox, oy) = 1.0f to 0.0f
				subCollide(x + ox, y + oy, endX + ox, endY + oy, 1.0f, Direction.RIGHT)?.minus(ox)

			} else {
				null
			}
		}

		val colX = collideX()
		if (colX != null) {
			endX = colX
			xVel = 0.0f
		}
		val colY = collideY()
		if (colY != null) {
			endY = colY
			yVel = 0.0f
		}

		x = endX
		y = endY
	}

	fun render(camera: Camera) {
		GameResources.colorShader.get().enable(camera.projectionView, Camera.transform(x, y, 1.0f, 1.0f))
		GameResources.colorShader.get().uniformColor(0, color)
		GameResources.rect.get().render()
	}
}