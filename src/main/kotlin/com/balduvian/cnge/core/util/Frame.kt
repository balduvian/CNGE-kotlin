package com.balduvian.cnge.core.util

import org.lwjgl.opengl.GL11

abstract class Frame {
	data class Bounds(val x: Int, val y: Int, val width: Int, val height: Int) {
		fun setViewport() {
			GL11.glViewport(x, y, width, height)
		}
	}

	abstract fun getBounds(width: Int, height: Int): Bounds
}
