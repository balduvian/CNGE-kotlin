package com.balduvian.cnge.core.util

import com.balduvian.cnge.graphics.Camera
import com.balduvian.cnge.graphics.TileTexture

abstract class Font {
	abstract fun preRender()

	abstract fun renderChar(camera: Camera, i: Int, char: Char, x: Float, y: Float, width: Float, height: Float)

	/**
	 * @param charWidth a percentage of width of a character vs the width of the bounding box
	 * @param scale how large the bounding boxes are
	 */
	fun renderString(
		camera: Camera,
		x: Float,
		y: Float,
		charWidth: Float,
		scale: Float,
		centered: Boolean,
		vararg strings: String,
	) {
		val boxSize = scale / charWidth
		val offX = if (centered) -(strings.fold(0) { last, string -> last + string.length } * scale / 2.0f) else 0f

		preRender()

		var goingX = x + offX

		for (i in strings.indices) {
			val string = strings[i]
			for (c in string) {
				renderChar(camera, i, c, goingX, y, boxSize, boxSize)
				goingX += scale
			}
		}
	}
}
