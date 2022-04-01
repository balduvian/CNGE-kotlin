package com.balduvian.cnge.graphics

import org.joml.Matrix4f

class Camera {
	val projection = Matrix4f()
	val projectionView = Matrix4f()

	var width = 0.0f
		private set
	var height = 0.0f
		private set

	var x = 0.0f
	var y = 0.0f

	var scaleX = 1.0f
	var scaleY = 1.0f

	var rotation = 0.0f

	fun setOrtho(width: Float, height: Float): Camera {
		this.width = width
		this.height = height

		projection.setOrtho2D(0.0f, width, 0.0f, height)

		return this
	}

	fun setOrtho(left: Float, right: Float, bottom: Float, top: Float): Camera {
		this.width = right - left
		this.height = top - bottom

		projection.setOrtho2D(left, right, bottom, top)

		return this
	}

	fun setOrthoTex(width: Float, height: Float): Camera {
		this.width = width
		this.height = height

		projection.setOrtho2D(0.0f, width, height, 0.0f)

		return this
	}

	fun update() {
		projectionView.scaling(scaleX, scaleY, 1.0f)
			.rotateZ(-rotation)
			.translate(-x, -y, 0.0f)
		projection.mul(projectionView, projectionView)
	}

	fun updateCenter() {
		projectionView
			.translation(width / 2, height / 2, 0.0f)
			.scale(scaleX, scaleY, 1.0f)
			.rotateZ(-rotation)
			.translate(-x, -y, 0.0f)
		projection.mul(projectionView, projectionView)
	}

	companion object {
		var transform = Matrix4f()

		fun transform(x: Float, y: Float, width: Float, height: Float): Matrix4f {
			return transform
				.translation(x, y, 0.0f)
				.scale(width, height, 1.0f)
		}

		fun transform(x: Float, y: Float, width: Float, height: Float, rotation: Float): Matrix4f {
			return transform
				.translation(x, y, 0.0f)
				.rotate(rotation, 0.0f, 0.0f, 1.0f)
				.scale(width, height, 1.0f)
		}

		fun transformCenter(centerX: Float, centerY: Float, x: Float, y: Float, width: Float, height: Float): Matrix4f {
			return transform
				.translation(x, y, 0.0f)
				.scale(width, height, 1.0f)
				.translate(-centerX / width, -centerY / height, 0.0f)
		}

		fun transformCenter(centerX: Float, centerY: Float, x: Float, y: Float, width: Float, height: Float, rotation: Float): Matrix4f {
			return transform
				.translation(x + centerX * width, y + centerY * height, 0.0f)
				.rotate(rotation, 0.0f, 0.0f, 1.0f)
				.scale(width, height, 1.0f)
				.translate(-centerX, -centerY, 0.0f)
		}

		val defaultModel = Matrix4f()
		val defaultProjView = Matrix4f().setOrtho2D(0.0f, 1.0f, 0.0f, 1.0f)
	}
}
