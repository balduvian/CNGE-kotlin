package com.balduvian.cnge.sound;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

class WaveData(stream: AudioInputStream) {
	val format: Int
	val samplerate: Int
	val totalBytes: Int
	val bytesPerFrame: Int
	val data: ByteBuffer

	val dataArray: ByteArray
 
	init {
		val audioFormat = stream.format
		format = openAlFormat(audioFormat.channels, audioFormat.sampleSizeInBits)
		this.samplerate = audioFormat.sampleRate.toInt()
		this.bytesPerFrame = audioFormat.frameSize
		this.totalBytes = (stream.frameLength * bytesPerFrame).toInt()
		this.data = BufferUtils.createByteBuffer(totalBytes)
		this.dataArray = ByteArray(totalBytes)

		data.clear()
		data.put(dataArray, 0, stream.read(dataArray, 0, totalBytes))
		data.flip()

		stream.close()
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