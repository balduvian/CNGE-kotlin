package com.balduvian.cnge.core.resource

import com.balduvian.cnge.core.Resource
import com.balduvian.cnge.graphics.Good
import com.balduvian.cnge.graphics.Option
import com.balduvian.cnge.graphics.VAO

class VAOResource(
	val drawMode: Int,
	val indices: IntArray,
	val attributes: Array<VAO.Attribute>
) : Resource<VAO>() {
	override fun internalAsyncLoad(): String? {
		return null
	}

	override fun internalSyncLoad(): Option<VAO> {
		return Good(VAO.create(
			drawMode,
			indices,
			attributes
		))
	}

	override fun cleanup() {}
}