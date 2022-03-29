package com.balduvian.cnge.core.util

import com.balduvian.cnge.graphics.Camera
import com.balduvian.cnge.graphics.TileTexture

abstract class Font {
	abstract fun preRender()

	abstract fun renderChar(camera: Camera, char: Char, x: Float, y: Float, width: Float, height: Float)

	/**
	 * @param charWidth a percentage of width of a character vs the width of the bounding box
	 * @param scale how large the bounding boxes are
	 */
	fun renderString(
		camera: Camera,
		string: String,
		x: Float,
		y: Float,
		charWidth: Float,
		scale: Float,
		centered: Boolean,
	) {
		val boxSize = scale / charWidth
		val offX = if (centered) -(string.length * scale / 2.0f) else 0f

		preRender()

		string.forEachIndexed { i, c ->
			renderChar(camera, c, x + (i * scale) + offX, y, boxSize, boxSize)
		}
	}
}
