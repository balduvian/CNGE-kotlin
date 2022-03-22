package com.balduvian.cnge.sound;

import org.lwjgl.openal.AL10
import org.lwjgl.system.MemoryUtil
import java.io.BufferedInputStream
import java.nio.ByteBuffer
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem

class WaveData(stream: AudioInputStream) {
	val format: Int
	val samplerate: Int
	val totalBytes: Int
	val bytesPerFrame: Int
	val data: ByteBuffer

	init {
		val audioFormat = stream.format
		format = openAlFormat(audioFormat.channels, audioFormat.sampleSizeInBits)
		samplerate = audioFormat.sampleRate.toInt()
		bytesPerFrame = audioFormat.frameSize
		totalBytes = (stream.frameLength * bytesPerFrame).toInt()

		val byteArray = ByteArray(totalBytes)
		stream.read(byteArray, 0, totalBytes)
		stream.close()

		data = ByteBuffer.wrap(byteArray)
	}

	companion object {
		fun create(path: String): WaveData {
			val stream = WaveData::class.java.getResource(path)!!.openStream()
			val bufferedInput = BufferedInputStream(stream)
			val audioStream = AudioSystem.getAudioInputStream(bufferedInput)
			return WaveData(audioStream)
		}

		fun openAlFormat(channels: Int, bitsPerSample: Int): Int {
			return if (channels == 1) {
				if (bitsPerSample == 8) AL10.AL_FORMAT_MONO8 else AL10.AL_FORMAT_MONO16
			} else {
				if (bitsPerSample == 8) AL10.AL_FORMAT_STEREO8 else AL10.AL_FORMAT_STEREO16
			}
		}
	}
}