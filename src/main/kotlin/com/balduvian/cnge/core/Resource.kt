package com.balduvian.cnge.core

import com.balduvian.cnge.graphics.GraphicsObject

abstract class Resource <T: GraphicsObject> {
	enum class State {
		UNLOADED,
		WAITING,
		LOADED,
	}

	var state: State = State.UNLOADED

	protected abstract fun internalAsyncLoad()
	protected abstract fun internalSyncLoad(): T
	protected abstract fun cleanup()

	fun asyncLoad() {
		internalAsyncLoad()
		state = State.WAITING
	}

	fun syncLoad() {
		resource = internalSyncLoad()
		cleanup()
		state = State.LOADED
	}

	fun destroy() {
		resource?.destroy()
		resource = null
		state = State.UNLOADED
	}

	protected var resource: T? = null

	fun get(): T {
		return resource!!
	}
}
