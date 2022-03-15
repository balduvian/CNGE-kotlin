package com.balduvian.cnge.core.resource

import com.balduvian.cnge.core.Resource
import com.balduvian.cnge.graphics.Option
import com.balduvian.cnge.graphics.Shader

class ShaderResource(
	val vertFilepath: String,
	val fragFilepath: String,
	vararg val uniforms: String
) : Resource <Shader> () {
	var vertData: String = ""
	var fragData: String = ""

	override fun internalAsyncLoad(): String? {
		vertData = this::class.java.getResource(vertFilepath)?.readText()
			?: return "Resource $vertFilepath does not exist"

		fragData = this::class.java.getResource(fragFilepath)?.readText()
			?: return "Resource $fragFilepath does not exist"

		return null
	}

	override fun internalSyncLoad(): Option<Shader> {
		return Shader.create(vertData, fragData, *uniforms)
	}

	override fun cleanup() {
		vertData = ""
		fragData = ""
	}
}
