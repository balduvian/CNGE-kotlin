package com.balduvian.cnge.core

import java.util.*
import java.util.concurrent.locks.ReentrantLock

class ResourceLoader {
	private val thread: Thread

	private val unloadStack: Stack<Resource<*>> = Stack()
	private val asyncLoadStack: Stack<Resource<*>> = Stack()
	private val syncLoadStack: Stack<Resource<*>> = Stack()

	private val stackLock = ReentrantLock()

	fun stake(resources: Array<Resource<*>>) {
		synchronized(stackLock) {
			/* make sure to now not unload any of these if they were going to be */
			unloadStack.removeAll(resources.toSet())

			/* don't load anything already loaded , or already being loaded */
			asyncLoadStack.addAll(resources.filter { resource ->
				val wasNotAlreadyLoaded = resource.state === Resource.State.UNLOADED
				resource.increaseStake()
				wasNotAlreadyLoaded
			})
		}

		/* tell the thread it's time to start loading again */
		thread.interrupt()
	}

	fun unstake(resources: Array<Resource<*>>) {
		synchronized(stackLock) {
			/* decrease stake in all of these resources */
			unloadStack.addAll(resources.filter { resource ->
				resource.reduceStake() == 0 &&
				resource.state !== Resource.State.UNLOADED
			})
		}
	}

	init {
		val thread = Thread {
			while (true) {
				val resource = synchronized(stackLock) {
					 if (asyncLoadStack.isEmpty()) null else asyncLoadStack.pop()
				}

				if (resource != null) {
					try {
						resource.asyncLoad()

						synchronized(stackLock) {
							syncLoadStack.push(resource)
						}
					} catch (ex: Exception) {
						resource.error(infoError(resource, ex))
					}
				} else {
					try {
						Thread.sleep(1000)
					} catch (_: InterruptedException) {}
				}
			}
		}

		thread.start()

		this.thread = thread
	}

	fun update() {
		var hasUnloaded = false

		/* unload one resource per call */
		if (unloadStack.isNotEmpty()) {
			val unloadResource = unloadStack.pop()
			unloadResource.destroy()
		}

		stackLock.lock()
		while (syncLoadStack.isNotEmpty()) {
			val loadresource = syncLoadStack.pop()

			try {
				loadresource.syncLoad()
			} catch (ex: Exception) {
				loadresource.error(infoError(loadresource, ex))
			}
		}
		stackLock.unlock()
	}

	fun kill() {
		thread.join()
	}

	fun blocking(loads: Array<Resource<*>>) {
		for (resource in loads) {
			resource.increaseStake()
			if (resource.state === Resource.State.STAGED) {
				try {
					resource.asyncLoad()
					resource.syncLoad()
				} catch (ex: Exception) {
					throw infoError(resource, ex)
				}
			}
		}
	}

	companion object {
		fun infoError(resource: Resource<*>, ex: Exception): Exception {
			return Exception("While loading $resource | ${ex.message ?: "Unknown error"}")
		}

		fun getAlong(resources: Array<Resource<*>>): Float {
			var value = 0

			for (resource in resources) {
				value += when (resource.state) {
					Resource.State.UNLOADED -> 0
					Resource.State.STAGED -> 1
					Resource.State.WAITING -> 2
					Resource.State.LOADED -> 3
					Resource.State.ERROR -> 3
				}
			}

			return value.toFloat() / (resources.size * 3.0f)
		}

		fun getDone(resources: Array<Resource<*>>): Pair<Boolean, List<Exception>> {
			return resources.all { it.state === Resource.State.LOADED } to resources.mapNotNull { it.getError() }
		}
	}
}
