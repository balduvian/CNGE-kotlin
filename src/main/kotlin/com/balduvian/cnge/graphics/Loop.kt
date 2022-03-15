package com.balduvian.cnge.graphics

class Loop(private val window: Window, val doFrame: (timing: Timing) -> Unit) {
	fun loop() {
		var last = System.nanoTime()

		/* only used when waiting a specified amount of time (no vsync) */
		var next = last

		while (!window.shouldClose()) {
			val skipWait = window.vsync

			/* if waiting, wait until the next time */
			if (!skipWait) {
				Thread.sleep(
					(((next - System.nanoTime()) / 1000000L) - 1L).coerceAtLeast(0L)
				)
				while (System.nanoTime() < next) { /* useless computation */ 32+34+2+23+2+342+324+342 }
			}

			/* reference point for the current frame */
			val now = System.nanoTime()

			val (delta, newLast) = if (skipWait) {
				/* exactly how much time has passed since last frame, determined by vsync */
				(now - last) to now
			} else {
				val fps = window.refreshRate()

				/* if real time has passed a full second ahead of the internal timer, */
				next = if (now - last > Timing.BILLION) {
					now + Timing.BILLION / fps.toLong()
				} else {
					last + (Timing.BILLION * 2) / fps.toLong()
				}

				/* determined by the fixed framerate */
				(Timing.BILLION / fps) to (last + Timing.BILLION)
			}

			last = newLast

			doFrame(Timing(delta))
		}

		window.terminate()
	}
}
