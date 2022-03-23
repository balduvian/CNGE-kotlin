package com.balduvian.cnge.core.util

class Timer {
	var timer = 0.0
	var goal = 0.0
	var going = false

	fun start(goal: Double): Timer {
		this.goal = goal
		this.going = true
		this.timer = 0.0

		return this
	}

	fun restart() {
		if (this.going) {
			this.timer = 0.0
		}
	}

	fun stop() {
		this.going = false
		this.timer = 0.0
	}

	fun update(time: Double): Boolean {
		if (!going) return false

		timer += time

		return if (timer >= goal) {
			timer = goal
			going = false
			true
		} else {
			false
		}
	}

	fun updateContinual(time: Double): Int {
		if (!going) return 0

		timer += time

		val numPassed = (timer / goal).toInt()
		timer %= goal

		return numPassed
	}

	fun along(): Float {
		return (timer / goal).toFloat()
	}

	fun timeRemaining(): Double {
		return goal - timer
	}

	fun changeTime(change: Double) {
		timer += change
		if (timer < 0.0) timer = 0.0
		if (timer > goal) timer = goal
	}
}
