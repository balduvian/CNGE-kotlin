package com.balduvian.cnge.core

import com.balduvian.cnge.graphics.Bad
import com.balduvian.cnge.graphics.Good
import com.balduvian.cnge.graphics.GraphicsObject
import com.balduvian.cnge.graphics.Option

abstract class Resource <T: GraphicsObject> {
	enum class State {
		UNLOADED,
		WAITING,
		LOADED,
	}

	var state: State = State.UNLOADED

	protected abstract fun internalAsyncLoad(): String?
	protected abstract fun internalSyncLoad(): Option<T>
	protected abstract fun cleanup()

	fun asyncLoad(): String? {
		val error = internalAsyncLoad()
		if (error != null) return error

		state = State.WAITING
		return null
	}

	fun syncLoad(): String? {
		return when (val option = internalSyncLoad()) {
			is Good -> {
				resource = option.value
				cleanup()
				state = State.LOADED
				null
			}
			is Bad -> option.value
		}
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
