package com.balduvian.cnge.core

import com.balduvian.cnge.graphics.GraphicsObject

abstract class Resource <T: GraphicsObject> {
	enum class State {
		UNLOADED,
		STAGED,
		WAITING,
		LOADED,
		ERROR,
	}

	var state: State = State.UNLOADED
	var referenceCount: Int = 0

	protected abstract fun internalAsyncLoad()
	protected abstract fun internalSyncLoad(): T
	protected abstract fun cleanup()

	fun increaseStake() {
		++referenceCount

		if (state === State.UNLOADED) {
			state = State.STAGED
		}
	}

	fun reduceStake(): Int {
		if (--referenceCount < 0) referenceCount = 0
		return referenceCount
	}

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

	fun error(thrownError: Exception) {
		resource?.destroy()
		resource = null
		state = State.ERROR
		this.thrownError = thrownError
	}

	var resource: T? = null
	var thrownError: Exception? = null

	fun get(): T {
		return resource!!
	}

	fun getError(): Exception? {
		val error = thrownError

		return if (thrownError == null) {
			null
		} else {
			thrownError = null
			state = State.UNLOADED
			error
		}
	}
}
