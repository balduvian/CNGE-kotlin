package com.balduvian.cnge.core.util

object Util {
	fun interp(start: Float, end: Float, along: Float): Float {
		return (end - start) * along + start
	}

	enum class Direction(val offX: Int, val offZ: Int) {
		RIGHT( 1,  0),
		UP   ( 0, -1),
		LEFT (-1,  0),
		DOWN ( 0,  1),
		NONE ( 0,  0);
	}
}