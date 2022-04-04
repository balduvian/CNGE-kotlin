package com.balduvian.cnge.sound

import org.lwjgl.openal.AL
import org.lwjgl.openal.AL10
import org.lwjgl.openal.ALC
import org.lwjgl.openal.ALC10.*
import org.lwjgl.openal.ALCCapabilities

import java.nio.ByteBuffer
import java.nio.IntBuffer

class ALManagement {
	val device: Long
	val context: Long
	val deviceCaps: ALCCapabilities

	init {
		device = alcOpenDevice(null as ByteBuffer?)
		if (device == 0L) {
			throw Exception("Failed to open the default device.")
		}

		deviceCaps = ALC.createCapabilities(device)

		context = alcCreateContext(device, null as IntBuffer?)
		if (context == 0L) {
			throw Exception("Failed to create an OpenAL context.")
		}

		alcMakeContextCurrent(context)
		AL.createCapabilities(deviceCaps)
	}
	
	fun destroy() {
		alcDestroyContext(context)
		alcCloseDevice(device)
	}
}