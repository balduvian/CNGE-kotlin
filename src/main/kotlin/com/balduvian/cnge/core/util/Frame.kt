package com.balduvian.cnge.core.util

abstract class Frame {
	data class Bounds(val x: Int, val y: Int, val width: Int, val height: Int)

	abstract fun getBounds(width: Int, height: Int): Bounds
}
