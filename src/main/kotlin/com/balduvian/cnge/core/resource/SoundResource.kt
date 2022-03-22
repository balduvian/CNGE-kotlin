package com.balduvian.cnge.core.resource

import com.balduvian.cnge.core.Resource
import com.balduvian.cnge.sound.Sound
import com.balduvian.cnge.sound.WaveData

class SoundResource(
	val path: String
) : Resource<Sound>() {
	private var waveData: WaveData? = null

	override fun internalAsyncLoad() {
		waveData = WaveData.create(path)
	}

	override fun internalSyncLoad(): Sound {
		return Sound(waveData!!)
	}

	override fun cleanup() {
		waveData = null
	}
}