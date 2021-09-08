package com.balduvian.cnge.graphics

import java.awt.SystemColor.window


abstract class Loop {
	data class Props(val exit: Boolean, val skipWait: Boolean, val fps: Int)

	abstract fun getProps(): Props

	abstract fun doFrame(timing: Timing)

	private fun loop() {
		var last = System.nanoTime()
		var next = last

		while (true) {
			val (exit, skipWait, fps) = getProps()
			if (exit) return

			//if (!skipWait) {
			//	Thread.sleep(
			//		(((next - System.nanoTime()) / 1000000) - 1).coerceAtLeast(0)
			//	)
			//	while (System.nanoTime() < next) {}
			//}

			val now = System.nanoTime()
			val delta = now - last
			last = now

			//if (!skipWait) {
			//	next = if (delta > Timing.BILLION) {
			//		now + Timing.BILLION / fps
			//	} else {
			//		last + Timing.BILLION / fps
			//	}
			//}

			doFrame(Timing(0, delta, 1.0 / fps))
		}
	}

	private fun slabLoop() {
		var frames = 0
		var curTime: Long
		var pastTime: Long
		var pastSec: Long = 0
		pastTime = System.nanoTime()

		do {
			val (exit, skipWait, fps) = getProps()
			val nspf: Long = 1000000000L / fps
			var dtime: Double
			curTime = System.nanoTime()
			if (curTime - pastTime > nspf) {
				var adtime = nspf / 1000000000.0

				doFrame(Timing(fps, curTime - pastTime, adtime))

				pastTime += nspf
				++frames
			}
			if (curTime - pastSec > 1000000000) {
				frames = 0
				pastSec += 1000000000
			}
			if (nspf - curTime + pastTime > 10000000) try {
				Thread.sleep(1)
			} catch (e: Exception) {
				e.printStackTrace()
			}
		} while (!exit)
	}

	init { loop() }
}
