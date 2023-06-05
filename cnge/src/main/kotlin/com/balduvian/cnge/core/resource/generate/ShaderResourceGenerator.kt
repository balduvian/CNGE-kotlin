package com.balduvian.cnge.core.resource.generate

import com.balduvian.cnge.core.resource.AbstractShaderResource
import com.balduvian.cnge.core.util.Color
import com.balduvian.cnge.graphics.Shader
import com.balduvian.cnge.graphics.ShaderData
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.joml.Matrix4f
import java.io.File
import kotlin.io.path.Path

class ShaderResourceGenerator : ResourceGenerator {
	override fun getFolder() = Path("shaders")

	enum class UniformType {
		FLOAT,
		VEC2,
		VEC3,
		VEC4,
		MAT2,
		MAT3,
		MAT4,
	}

	data class Uniform(val type: UniformType, val name: String, val array: Boolean) {
		fun prettyTypeName(): String {
			return "${type.name.lowercase()}${if (array) "[]" else ""}"
		}

		fun expectType(type: UniformType, array: Boolean) {
			if (this.type != type || this.array != array) {
				throw Exception("uniform \"${name}\" must be a ${Uniform(type, "", array).prettyTypeName()} (is a ${prettyTypeName()})")
			}
		}
	}

	val typeNameToUniformType = hashMapOf(
		"float" to UniformType.FLOAT,
		"vec2" to UniformType.VEC2,
		"vec3" to UniformType.VEC3,
		"vec4" to UniformType.VEC4,
		"mat2" to UniformType.MAT2,
		"mat3" to UniformType.MAT3,
		"mat4" to UniformType.MAT4,
	)

	val uniformFunctions = hashMapOf<UniformType, (ClassName, Uniform, Int) -> Array<FunSpec>>(
		UniformType.FLOAT to { className, uniform, index -> arrayOf(
			FunSpec.builder(uniform.name)
				.addParameter("x", Float::class)
				.returns(className)
				.addCode("""
					uniformFloat(${index}, x)
					return this
				""".trimIndent()).build()
		) },
		UniformType.VEC4 to { className, uniform, index -> arrayOf(
			FunSpec.builder(uniform.name)
				.addParameter("x", Float::class)
				.addParameter("y", Float::class)
				.addParameter("z", Float::class)
				.addParameter("w", Float::class)
				.returns(className)
				.addCode("""
					uniformVector4(${index}, x, y, z, w)
					return this
				""".trimIndent())
				.build(),
			FunSpec.builder(uniform.name)
				.addParameter("values", FloatArray::class)
				.returns(className)
				.addCode("""
					uniformVector4(${index}, values)
					return this
				""".trimIndent())
				.build(),
			FunSpec.builder(uniform.name)
				.addParameter("color", Color::class)
				.returns(className)
				.addCode("""
					uniformColor(${index}, color)
					return this
				""".trimIndent())
				.build(),
			FunSpec.builder(uniform.name)
				.addParameter("color", Color::class)
				.addParameter("alpha", Float::class)
				.returns(className)
				.addCode("""
					uniformColor(${index}, color, alpha)
					return this
				""".trimIndent())
				.build(),
		) }
	)

	data class UniformBuilder(
		var type: UniformType? = null,
		var name: String? = null,
		var arrayStack: ArrayList<Token<GLSLTokenType>> = ArrayList()
	) {
		fun incompleteArray() = arrayStack.size != 0 && arrayStack.size != 3
		fun isArray() = arrayStack.size == 3
	}

	fun extractUniforms(tokens: ArrayList<Token<GLSLTokenType>>): ArrayList<Uniform> {
		val uniforms = ArrayList<Uniform>()
		var startOfStatement = true
		var uniformBuilder: UniformBuilder? = null

		val arrayFormation = arrayOf(
			GLSLTokenType.SQUARE_BRACKET_LEFT,
			GLSLTokenType.NUMBER,
			GLSLTokenType.SQUARE_BRACKET_RIGHT
		)

		fun processToken(token: Token<GLSLTokenType>, builder: UniformBuilder?): UniformBuilder? {
			if (token.type == GLSLTokenType.SEMICOLON) {
				if (builder != null) {
					builder.type?.let { type ->
						builder.name?.let { name ->
							if (builder.incompleteArray()) {
								null
							} else {
								uniforms.add(Uniform(type, name, builder.isArray()))
							}
						}
					} ?: run {
						throw Exception("Incomplete uniform statement for uniform ${builder.name}")
					}
				}

				startOfStatement = true
				return null
			}

			return if (builder == null) {
				if (startOfStatement && token.type == GLSLTokenType.IDENTIFIER && token.string == "uniform") {
					UniformBuilder()
				} else {
					null
				}
			} else if (builder.type == null) {
				if (token.type == GLSLTokenType.IDENTIFIER) {
					val type = typeNameToUniformType[token.string]
						?: throw Exception("Unsupported uniform type \"${token.string}\"")

					builder.type = type
					builder
				} else {
					throw Exception("Expected uniform type, got \"${token.string}\" instead")
				}
			} else if (builder.name == null) {
				if (token.type == GLSLTokenType.IDENTIFIER) {
					builder.name = token.string
					builder
				} else {
					throw Exception("Expected uniform name, got \"${token.string}\" instead")
				}
			} else {
				if (builder.arrayStack.size < 3 && token.type == arrayFormation[builder.arrayStack.size]) {
					builder.arrayStack.add(token)
					builder
				} else {
					throw Exception("Junk at the end of uniform declaration \"${builder.name}\"")
				}
			}
		}

		for (token in tokens) {
			uniformBuilder = processToken(token, uniformBuilder)
		}

		return uniforms
	}

	fun removeDuplicateUniforms(uniforms: ArrayList<Uniform>): ArrayList<Uniform> {
		val filteredUniforms = ArrayList<Uniform>(uniforms.size)

		for (i in uniforms.indices) {
			val baseName = uniforms[i].name

			var unique = (i + 1 until uniforms.size).none { j ->
				uniforms[j].name == baseName
			}

			if (unique) filteredUniforms.add(uniforms[i])
		}

		return filteredUniforms
	}

	fun preprocess(text: String): String? {
		var hasSplit = false

		val processedLines = text.lines().filter { line ->
			val trimmed = line.trimStart()
			if (trimmed.startsWith('#')) {
				if (trimmed.substring(1).trim() == "split") {
					hasSplit = true
				}

				false
			} else {
				true
			}
		}

		return if (hasSplit) processedLines.joinToString("\n") { it } else null
	}

	override fun generate(file: File, output: File) {
		if (file.isDirectory) return
		if (file.extension != "glsl") return

		val baseName = file.nameWithoutExtension

		val fieldName = "${baseName.replaceFirstChar { it.lowercase() }}Shader"
		val className = "${baseName.replaceFirstChar { it.uppercase() }}Shader"

		val fileText = preprocess(file.readText()) ?: return

		val tokens = tokenize(fileText, GLSLTokenType.UNKNOWN, tokenMapping)

		val uniforms = removeDuplicateUniforms(extractUniforms(tokens))

		val pvmIndex = uniforms.indexOfFirst { it.name == "pvm" }
		if (pvmIndex != -1) uniforms[pvmIndex].expectType(UniformType.MAT4, false)

		val modelIndex = uniforms.indexOfFirst { it.name == "model" }
		if (modelIndex != -1) uniforms[modelIndex].expectType(UniformType.MAT4, false)

		val projViewIndex = uniforms.indexOfFirst { it.name == "projView" }
		if (projViewIndex != -1) uniforms[projViewIndex].expectType(UniformType.MAT4, false)

		val normalIndex = uniforms.indexOfFirst { it.name == "normal" }
		if (normalIndex != -1) uniforms[normalIndex].expectType(UniformType.MAT3, false)

		val typeName = ClassName("", className)

		val shaderClassBuilder = TypeSpec.classBuilder(className)
			.primaryConstructor(
				FunSpec.constructorBuilder()
					.addParameter("shaderData", ShaderData::class)
					.build()
			)
			.superclass(Shader::class)
			.addSuperclassConstructorParameter("shaderData")
			.addFunction(
				FunSpec.builder("matrices")
					.addParameter("projectionView", Matrix4f::class)
					.addParameter("model", Matrix4f::class)
					.returns(typeName)
					.addCode("""
						${listOfNotNull(
							if (pvmIndex == -1) null else "uniformPVM(${pvmIndex}, projectionView, model)",
							if (modelIndex == -1) null else "uniformModel(${modelIndex}, model)",
							if (projViewIndex == -1) null else "uniformProjView(${projViewIndex}, projectionView)",
							if (normalIndex == -1) null else "uniformNormal(${normalIndex}, model)",
						).joinToString("\n")}
						return this
					""".trimIndent())
					.build()
			)

		uniforms.forEachIndexed { index, uniform ->
			if (index == pvmIndex || index == modelIndex || index == projViewIndex || index == normalIndex) return@forEachIndexed
			val generator = uniformFunctions[uniform.type] ?: return@forEachIndexed

			shaderClassBuilder.addFunctions(generator(typeName, uniform, index).asIterable())
		}

		val fieldType = AbstractShaderResource::class.asClassName().parameterizedBy(typeName)
		FileSpec.builder(typeName)
			.addType(shaderClassBuilder.build())
			.addImport("com.balduvian.cnge.core.resource", "createShaderResource")
			.addImport("com.balduvian.cnge.core.util.Color.Companion", "uniformColor")
			.addProperty(PropertySpec.builder(fieldName, fieldType).initializer(
				"""
				createShaderResource(
					${(0 until uniforms.size + 1).joinToString("\n") { "%S," }}
				) { shaderData -> ${className}(shaderData) }
				""".trimIndent(),
				*(listOf(file.path) + uniforms.map { it.name }).toTypedArray()
			).build())
			.build()
			.writeTo(output)
	}
}

fun main() {
	//val shaderResourceGenerator = ShaderResourceGenerator()
//
	//val generated = shaderResourceGenerator.generate(File("src/main/resources/shaders/checker.glsl")) ?: return
//
	//val fileSpec = FileSpec.builder(ClassName.bestGuess("Resources"))

	//generated.forEach { spec ->
	//	when (spec) {
	//		is TypeSpec -> {
	//			fileSpec.addType(spec)
	//		}
	//		is PropertySpec -> {
	//			fileSpec.addProperty(spec)
	//		}
	//		else -> {
	//			throw Exception("$spec is not a proper spec")
	//		}
	//	}
	//}

	//fileSpec.build().writeTo(System.out)
}
