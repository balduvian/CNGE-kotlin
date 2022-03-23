package com.balduvian.cnge.core.util

import kotlin.math.PI
import kotlin.math.sin

object Util {
	fun interp(start: Float, end: Float, along: Float): Float {
		return (end - start) * along + start
	}

	fun invInterp(start: Float, end: Float, value: Float): Float {
		return (value - start) / (end - start)
	}

	fun Float.sinAlong(): Float {
		return sin(PI.toFloat() * (this - 0.5f)) / 2.0f + 0.5f
	}

	enum class Direction(val offX: Int, val offZ: Int) {
		RIGHT( 1,  0),
		UP   ( 0, -1),
		LEFT (-1,  0),
		DOWN ( 0,  1),
		NONE ( 0,  0);
	}
}