package com.balduvian.cnge.core.resource

import com.balduvian.cnge.core.Resource
import com.balduvian.cnge.graphics.Shader

class ShaderResource(
	val vertFilepath: String,
	val fragFilepath: String,
	vararg val uniforms: String
) : Resource <Shader> () {
	var vertData: String = ""
	var fragData: String = ""

	override fun internalAsyncLoad() {
		vertData = Shader.processSource(
			this::class.java.getResource(vertFilepath)?.readText()
			?: throw Exception("Resource $vertFilepath does not exist")
		)

		fragData = Shader.processSource(
			this::class.java.getResource(fragFilepath)?.readText()
			?: throw Exception("Resource $fragFilepath does not exist")
		)
	}

	override fun internalSyncLoad(): Shader {
		return Shader.create(vertData, fragData, *uniforms)
	}

	override fun cleanup() {
		vertData = ""
		fragData = ""
	}
}
