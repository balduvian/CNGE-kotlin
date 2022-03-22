package com.balduvian.cnge.core

class ResourceLoader {
	private var thread: Thread? = null
	private var threadDone: Boolean = false

	private var unloads: List<Resource<*>> = ArrayList()
	var loads: Array<Resource<*>> = emptyArray()

	var error: Exception? = null
	var along: Float = 0.0f

	fun start(unloads: Array<Resource<*>>, loads: Array<Resource<*>>) {
		this.unloads = unloads.filter { unload -> loads.none { it === unload } }
		this.loads = loads

		threadDone = false
		error = null
		along = 0.0f

		var count = 0

		val thread = Thread {
			for (resource in loads) {
				if (resource.state === Resource.State.UNLOADED) {
					try {
						resource.asyncLoad()
						along = ++count / loads.size.toFloat()

					} catch (ex: Exception) {
						error = infoError(resource, ex)
						break
					}
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
				try {
					resource.syncLoad()
				} catch (ex: Exception) {
					error = infoError(resource, ex)
					break
				}
			} else if (resource.state === Resource.State.UNLOADED) {
				hasUnloaded = true
			}
		}

		if (error != null || (threadDone && !hasUnloaded)) {
			thread?.join()
			this.thread = null
		}
	}

	fun done(): Boolean {
		return thread == null
	}

	companion object {
		fun infoError(resource: Resource<*>, ex: Exception): Exception {
			return Exception("While loading $resource | ${ex.message ?: "Unknown error"}")
		}

		fun blocking(loads: Array<Resource<*>>) {
			for (resource in loads) {
				if (resource.state === Resource.State.UNLOADED) {
					try {
						resource.asyncLoad()
						resource.syncLoad()
					} catch (ex: Exception) {
						throw infoError(resource, ex)
					}
				}
			}
		}
	}
}
