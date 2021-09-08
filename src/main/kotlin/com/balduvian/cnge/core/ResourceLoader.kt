package com.balduvian.cnge.core

import com.balduvian.cnge.graphics.Bad
import com.balduvian.cnge.graphics.Good
import com.balduvian.cnge.graphics.Option

class ResourceLoader {
	private var thread: Thread? = null
	private var threadDone: Boolean = false

	private var unloads: List<Resource<*>> = ArrayList()
	var loads: Array<Resource<*>> = emptyArray()

	var result: Option<Boolean> = Good(false)
	var along: Float = 0.0f

	fun start(unloads: Array<Resource<*>>, loads: Array<Resource<*>>) {
		this.unloads = unloads.filter { unload -> loads.none { it === unload } }
		this.loads = loads

		threadDone = false
		result = Good(false)
		along = 0.0f

		var count = 0

		val thread = Thread {
			for (resource in loads) {
				if (resource.state === Resource.State.UNLOADED) {
					val error = resource.asyncLoad()

					if (error != null) {
						result = Bad(error)
						break
					}

					along = ++count / loads.size.toFloat()
				}
			}

			threadDone = true
		}

		thread.start()

		this.thread = thread
	}

	fun update() {
		var hasUnloaded = false

		unloads.forEach { resource ->
			if (resource.state === Resource.State.LOADED) {
				resource.destroy()
			}
		}

		for (resource in loads) {
			if (resource.state === Resource.State.WAITING) {
				val error = resource.syncLoad()

				if (error != null) {
					result = Bad(error)
					break
				}

			} else if (resource.state === Resource.State.UNLOADED) {
				hasUnloaded = true
			}
		}

		if (threadDone && result !is Bad && !hasUnloaded) {
			thread?.join()
			this.thread = null
			result = Good(true)
		}
	}

	companion object {
		fun blocking(loads: Array<Resource<*>>) {
			loads.forEach { resource ->
				if (resource.state === Resource.State.UNLOADED) {
					val error = resource.asyncLoad() ?: resource.syncLoad()
					if (error != null) throw Exception(error)
				}
			}
		}
	}
}
