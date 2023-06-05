package com.balduvian.cnge.core.resource

import com.balduvian.cnge.core.Resource
import com.balduvian.cnge.graphics.Shader
import com.balduvian.cnge.graphics.ShaderData

abstract class AbstractShaderResource<S : Shader>(
	val vertFilepath: String,
	val fragFilepath: String,
	vararg val uniforms: String
) : Resource <S> () {
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

	abstract fun createShader(shaderData: ShaderData): S

	override fun internalSyncLoad(): S {
		return createShader(Shader.create(vertData, fragData, *uniforms))
	}

	override fun cleanup() {
		vertData = ""
		fragData = ""
	}
}

class ShaderResource(
	vertFilepath: String,
	fragFilepath: String,
	vararg uniforms: String
) : AbstractShaderResource<Shader>(vertFilepath, fragFilepath, *uniforms) {
	override fun createShader(shaderData: ShaderData) = Shader(shaderData)
}

inline fun <reified S : Shader>createShaderResource(
	vertFilepath: String,
	fragFilepath: String,
	vararg uniforms: String,
	crossinline createShader: (shaderData: ShaderData) -> S
) = object : AbstractShaderResource<S>(vertFilepath, fragFilepath, *uniforms) {
	override fun createShader(shaderData: ShaderData) = createShader(shaderData)
}
