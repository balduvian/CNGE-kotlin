package com.balduvian.cnge.sound

import com.balduvian.cnge.graphics.Disposable
import org.lwjgl.openal.AL10
import org.lwjgl.openal.AL10.AL_PLAYING
import org.lwjgl.openal.AL10.AL_SOURCE_STATE

class Sound(waveData: WaveData): Disposable {
	private val buffer = AL10.alGenBuffers()
	private val sourceId = AL10.alGenSources()

	init {
		AL10.alBufferData(buffer, waveData.format, waveData.data, waveData.samplerate)
		AL10.alSourcei(sourceId, AL10.AL_BUFFER, buffer)
		AL10.alSourcef(sourceId, AL10.AL_GAIN, 1.0f)
		AL10.alSourcef(sourceId, AL10.AL_PITCH, 1.0f)
		AL10.alSource3f(sourceId, AL10.AL_POSITION, 0.0f, 0.0f, 0.0f)
	}

	fun play() {
		AL10.alSourcei(sourceId, AL10.AL_LOOPING, 0)
		AL10.alSourcePlay(sourceId)
	}

	fun loop() {
		AL10.alSourcei(sourceId, AL10.AL_LOOPING, 1)
		AL10.alSourcePlay(sourceId)
	}

	fun stop() {
		AL10.alSourceStop(sourceId)
	}

	fun setVolume(volume: Float) {
		AL10.alSourcef(sourceId, AL10.AL_GAIN, volume)
	}

	fun setPitch(pitch: Float) {
		AL10.alSourcef(sourceId, AL10.AL_PITCH, pitch)
	}

	fun isPlaying(): Boolean {
		return AL10.alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PLAYING
	}

	override fun destroy() {
		AL10.alDeleteBuffers(buffer)
		AL10.alDeleteSources(sourceId)
	}
}