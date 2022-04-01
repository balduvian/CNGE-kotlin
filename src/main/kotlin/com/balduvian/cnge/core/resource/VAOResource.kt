package com.balduvian.cnge.core.resource

import com.balduvian.cnge.core.Resource
import com.balduvian.cnge.graphics.VAO

class VAOResource(
	val drawMode: Int,
	val indices: IntArray,
	val attributes: Array<VAO.Attribute>
) : Resource<VAO>() {
	override fun internalAsyncLoad() {}

	override fun internalSyncLoad(): VAO {
		return VAO.create(
			drawMode,
			indices,
			attributes
		)
	}

	override fun cleanup() {}
}