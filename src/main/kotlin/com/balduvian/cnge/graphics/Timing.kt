package com.balduvian.cnge.graphics

import kotlin.math.roundToInt

data class Timing(val delta: Long) {
	val time = delta.toDouble() / BILLION.toDouble()
	val fps = (1.0 / time).roundToInt()

	companion object {
		const val BILLION = 1000000000L
	}
}
