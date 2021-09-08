package com.balduvian.cnge.graphics;

import org.lwjgl.glfw.GLFW.*

class Input {
	companion object {
		const val RELEASED = 0.toByte()
		const val PRESSED = 1.toByte()
		const val HELD = 2.toByte()
	}

	var didResize = false
	val keys = ByteArray(GLFW_KEY_LAST)
	val buttons = ByteArray(GLFW_MOUSE_BUTTON_LAST)
	var mouseX = 0.0f
	var mouseY = 0.0f
	var mouseScroll = 0

	fun keyHeld(key: Int): Boolean {
		return keys[key] > RELEASED
	}

	fun keypressed(key: Int): Boolean {
		return keys[key] == PRESSED
	}

	fun buttonHeld(button: Int): Boolean {
		return buttons[button] > RELEASED
	}

	fun buttonPressed(button: Int): Boolean {
		return buttons[button] == PRESSED
	}
}
