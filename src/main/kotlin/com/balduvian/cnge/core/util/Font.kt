package com.balduvian.cnge.core.util

import com.balduvian.cnge.graphics.Camera
import com.balduvian.cnge.graphics.TileTexture

abstract class Font {
	abstract fun renderChar(camera: Camera, char: Char, x: Float, y: Float, width: Float, height: Float)

	fun renderString(
		camera: Camera,
		string: String,
		tileTexture: TileTexture,
		x: Float,
		y: Float,
		charWidth: Float,
		scale: Float,
		centered: Boolean,
	) {
		val boxSize = scale / charWidth
		val offX = if (centered) -(string.length * scale / 2.0f) else 0f

		tileTexture.bind()

		string.forEachIndexed { i, c ->
			renderChar(camera, c, x + (i * scale) + offX, y, boxSize, boxSize)
		}
	}
}
