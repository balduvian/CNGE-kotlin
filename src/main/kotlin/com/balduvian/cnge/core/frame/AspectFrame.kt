package com.balduvian.cnge.core.frame

import com.balduvian.cnge.core.Frame
import kotlin.math.roundToInt

class AspectFrame(val aspect: Float) : Frame() {
	override fun getBounds(width: Int, height: Int): Bounds {
		val windowAspect = width.toFloat() / height

		/* window is too wide */
		return if (windowAspect > aspect) {
			val h = height
			val w = (aspect * height).roundToInt()
			val y = 0
			val x = (width - w) / 2

			Bounds(x, y, w, h)

		/* window is too tall */
		} else {
			val w = width
			val h = ((1 / aspect) * width).roundToInt()
			val x = 0
			val y = (height - h) / 2

			return Bounds(x, y, w, h)
		}
	}
}