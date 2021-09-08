package com.balduvian.cnge.graphics

data class Timing(val fps: Int, val delta: Long, val time: Double) {

	companion object {
		const val BILLION = 1000000000
	}
}
