package game

import com.balduvian.cnge.graphics.Camera
import com.balduvian.cnge.graphics.Input
import org.lwjgl.glfw.GLFW.*

class Player (var x: Float, var y: Float) {
	var velocity = Vector(0.0f, 0.0f)
	var walkingVelocity = Vector(0.0f, 0.0f)

	var touchingWall = null as Vector?

	data class Collision(val start: Vector, val end: Vector, val wall: Vector)

	companion object {
		val FLOAT_SKEK = 0.001f
		val WALK_ACCEL = 100.0f
		val WALK_SPEED = 25.0f
		val GRAVITY = 20.0f
	}

	fun update(input: Input, time: Float, lineSets: Array<LineSet>) {
		val accelWall = touchingWall ?: Vector(1.0f, 0.0f)

		val holdRight = input.keyHeld(GLFW_KEY_D)
		val holdLeft = input.keyHeld(GLFW_KEY_A)

		if (holdRight) {
			walkingVelocity += Vector(WALK_ACCEL * time, 0.0f).setProject(accelWall)
		}
		if (holdLeft) {
			walkingVelocity += Vector(-WALK_ACCEL * time, 0.0f).setProject(accelWall)
		}
		if (input.keypressed(GLFW_KEY_W)) {
			velocity.y = 20.0f
		}

		if (walkingVelocity.length() > WALK_SPEED) walkingVelocity.setAsLength(WALK_SPEED)

		if (walkingVelocity.x > 0 && !holdRight) {
			walkingVelocity += Vector(-WALK_ACCEL * time, 0.0f).setProject(accelWall)
			if (walkingVelocity.x < 0) walkingVelocity = Vector(0.0f, 0.0f)

		} else if (walkingVelocity.x < 0 && !holdLeft) {
			walkingVelocity += Vector(WALK_ACCEL * time, 0.0f).setProject(accelWall)
			if (walkingVelocity.x > 0) walkingVelocity = Vector(0.0f, 0.0f)
		}

		velocity.y -= GRAVITY * time

		if (velocity.x > 0) {
			velocity.x -= WALK_ACCEL * time
			if (velocity.x < 0) velocity.x = 0.0f
		} else if (velocity.x < 0 ) {
			velocity.x += WALK_ACCEL * time
			if (velocity.x > 0) velocity.x = 0.0f
		}

		val movement = Line(
			x, y,
			x + velocity.x * time + walkingVelocity.x * time,
			y + velocity.y * time + walkingVelocity.y * time
		)
		var didCollide = false

		while (true) {
			var nearestAlong = Float.POSITIVE_INFINITY
			var nearestLine = null as Line?

			for (lineSet in lineSets) {
				if (lineSet.inBox(movement.start()) || lineSet.inBox(movement.end())) {
					for (line in lineSet) {
						val moveAlong = movement.intersection(line)

						if (moveAlong in 0f..1f && moveAlong < nearestAlong) {
							val lineAlong = line.intersection(movement)

							if (lineAlong in 0f..1f) {
								nearestAlong = moveAlong
								nearestLine = line
							}
						}
					}
				}
			}

			if (nearestLine != null) {
				val wallVector = nearestLine.vector()

				/* where the movement intersects the wall */
				val newStart = movement.pointAlong(nearestAlong)

				/* where the movement would end if sliding along the wall */
				val newEnd = movement.end().setSub(nearestLine.start()).setProject(wallVector).setAdd(nearestLine.start())

				/* perpendicular to the wall, facing towards the movement's start */
				val push = wallVector.perpendicular()
				if (movement.vector().dot(push) >= 0) push.setInvert()
				push.setAsLength(FLOAT_SKEK)

				movement.setStart(newStart.setAdd(push))
				movement.setEnd(newEnd.setAdd(push))

				velocity.setProject(wallVector)
				walkingVelocity.setProject(wallVector)
				didCollide = true
				touchingWall = wallVector
			} else {
				break
			}
		}

		if (!didCollide) touchingWall = null

		x = movement.ex
		y = movement.ey
	}

	fun render(camera: Camera) {
		GameResources.hatKidTexture.get().bind()
		GameResources.textureShader.get().enable(camera.projectionView, Camera.transformCenter(5f, 0.0f, x, y, 10f, 10f))
		GameResources.textureShader.get().uniformVector4(0, 1f, 1f, 1f, 1f)
		GameResources.rect.get().render()
	}
}
