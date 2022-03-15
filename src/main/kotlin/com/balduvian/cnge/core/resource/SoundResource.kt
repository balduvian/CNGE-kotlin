package com.balduvian.cnge.core.resource

import com.balduvian.cnge.core.Resource
import com.balduvian.cnge.graphics.Good
import com.balduvian.cnge.graphics.Option
import com.balduvian.cnge.sound.Sound
import com.balduvian.cnge.sound.WaveData

class SoundResource(
	val path: String
) : Resource<Sound>() {
	private var waveData: WaveData? = null

	override fun internalAsyncLoad(): String? {
		return try {
			waveData = WaveData.create(path)
			null
		} catch (ex: Exception) {
			ex.message
		}
	}

	override fun internalSyncLoad(): Option<Sound> {
		return Good(Sound(waveData!!))
	}

	override fun cleanup() {
		waveData = null
	}
}