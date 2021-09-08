package com.balduvian.cnge.core

import com.balduvian.cnge.graphics.Input
import com.balduvian.cnge.graphics.Timing
import com.balduvian.cnge.graphics.Window

abstract class Scene(val window: Window) {
	abstract fun update(input: Input, timing: Timing)

	abstract fun render()

	abstract fun onResize(width: Int, height: Int)

	abstract fun switchScene(): Int?
}
