package com.balduvian.cnge.graphics

import org.joml.Matrix4f

class Camera3D {
	val projection = Matrix4f()
	val view = Matrix4f()
	val projView = Matrix4f()

	var fov = 0.0f
		private set
	var aspect = 0.0f
		private set

	var x = 0.0f
	var y = 0.0f
	var z = 0.0f

	var rotationX = 0.0f
	var rotationY = 0.0f
	var rotationZ = 0.0f

	fun setPerspective(fov: Float, aspect: Float): Camera3D {
		this.fov = fov
		this.aspect = aspect

		projection.setPerspective(fov, aspect, 0.1f, 50.0f)

		return this
	}

	fun update() {
		projection.mul(view.rotationXYZ(-rotationX, -rotationY, -rotationZ).translate(-x, -y, -z), projView)
	}

	fun updateLookAt(eyeX: Float, eyeY: Float, eyeZ: Float, centerX: Float, centerY: Float, centerZ: Float, upX: Float, upY: Float, upZ: Float) {
		projection.mul(view.setLookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ), projView)
	}

	companion object {
		fun transform(x: Float, y: Float, z: Float, width: Float, height: Float, depth: Float): Matrix4f {
			return Camera.transform
				.translation(x, y, z)
				.scale(width, height, depth)
		}

		fun transform(x: Float, y: Float, z: Float, width: Float, height: Float, depth: Float, rotationX: Float, rotationY: Float, rotationZ: Float): Matrix4f {
			return Camera.transform
				.translation(x, y, z)
				.scale(width, height, depth)
				.rotateX(rotationX).rotateY(rotationY).rotateZ(rotationZ)
		}

		fun transform(
			x: Float, y: Float, z: Float,
			width: Float, height: Float, depth: Float,
			rotationX: Float, rotationY: Float, rotationZ: Float,
			centerX: Float, centerY: Float, centerZ: Float
		): Matrix4f {
			return Camera.transform
				.translation(x, y, z)
				.rotateX(rotationX).rotateY(rotationY).rotateZ(rotationZ)
				.scale(width, height, depth)
				.translate(-centerX / width, -centerY / height, -centerZ / depth)
		}
	}
}
