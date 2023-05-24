package com.balduvian.cnge.core.resource.generate

import com.balduvian.cnge.graphics.Shader
import com.squareup.kotlinpoet.TypeSpec
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path

interface ResourceGenerator {
	val folder: Path

	fun generate(file: File): TypeSpec?
}

class ShaderResourceGenerator : ResourceGenerator {
	override val folder = Path("shaders")

	enum class UniformType {
		FLOAT,
		VEC2,
		VEC3,
		VEC4,
		MAT2,
		MAT3,
		MAT4,
	}

	data class Uniform(val type: UniformType, val name: String, val array: Boolean)

	val typeNameToUniformType = hashMapOf(
		"float" to UniformType.FLOAT,
		"vec2" to UniformType.VEC2,
		"vec3" to UniformType.VEC3,
		"vec4" to UniformType.VEC4,
		"mat2" to UniformType.MAT2,
		"mat3" to UniformType.MAT3,
		"mat4" to UniformType.MAT4,
	)

	enum class TokenType {
		STRING,
		NUMBER,
		BRACKET_LEFT,
		BRACKET_RIGHT,
		SEMICOLON,
		PREPROCESSOR,
		COMMENT,
		MULTI_COMMENT,
		UNKNOWN,
	}

	data class Token(val type: TokenType, val value: String)

	fun tokenize(line: String): ArrayList<Token> {
		val tokens = ArrayList<Token>()

		var currentTokenType: TokenType? = null
		val builder = StringBuilder()

		fun isWord(char: Char) = (char in 'a'..'z') || (char in 'A'..'Z') || char == '_'
		fun isNumber(char: Char) = char in '0'..'9'

		fun processInitial(char: Char) {
			if (char.isWhitespace()) return

			else if (isWord(char)) {
				builder.append(char)
				currentTokenType = TokenType.STRING
			} else if (isNumber(char)) {
				builder.append(char)
				currentTokenType = TokenType.NUMBER
			} else if (char == '[') {
				tokens.add(Token(TokenType.BRACKET_LEFT, "["))
				currentTokenType = null
			} else if (char == ']') {
				tokens.add(Token(TokenType.BRACKET_RIGHT, "]"))
				currentTokenType = null
			} else if (char == ';') {
				tokens.add(Token(TokenType.SEMICOLON, ";"))
				currentTokenType = null
			} else if (char == '/') {
				builder.append(char)
				currentTokenType = TokenType.COMMENT
			} else {
				builder.append(char)
				currentTokenType = TokenType.UNKNOWN
			}
		}

		fun finalize(char: Char, type: TokenType) {
			tokens.add(Token(type, builder.toString()))
			builder.clear()
			currentTokenType = null
			processInitial(char)
		}

		fun processIntermediate(char: Char) {
			val tokenType = currentTokenType ?: return

			if (tokenType == TokenType.UNKNOWN) {
				if (!char.isWhitespace() && !isWord(char) && !isNumber(char) && char != '[' && char != ']' && char != ';') {
					builder.append(char)
				} else {
					finalize(char, tokenType)
				}
			} else if (tokenType == TokenType.STRING || tokenType == TokenType.NUMBER) {
				if (isWord(char) || isNumber(char)) {
					builder.append(char)
				} else {
					finalize(char, tokenType)
				}
			} else if (tokenType == TokenType.COMMENT) {
				if (builder.length == 1 && ) {

				} else {

				}

				} else if (char == '\n') {

				}
			}
		}

		for (char in line) {
			if (builder.isEmpty()) {
				processInitial(char)
			} else {
				processIntermediate(char)
			}
		}

		processIntermediate('\n')

		return tokens
	}

	data class UniformBuilder(
		var type: UniformType? = null,
		var name: String? = null,
		var arrayStack: ArrayList<Token> = ArrayList()
	) {
		fun incompleteArray() = arrayStack.size != 0 && arrayStack.size != 3
		fun isArray() = arrayStack.size == 3
	}

	fun extractUniforms(tokens: ArrayList<Token>): ArrayList<Uniform> {
		val uniforms = ArrayList<Uniform>()
		var startOfStatement = true
		var uniformBuilder: UniformBuilder? = null

		val arrayFormation = arrayOf(
			TokenType.BRACKET_LEFT,
			TokenType.NUMBER,
			TokenType.BRACKET_RIGHT
		)

		fun processToken(token: Token, builder: UniformBuilder?): UniformBuilder? {
			if (token.type == TokenType.SEMICOLON) {
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
				if (startOfStatement && token.type == TokenType.STRING && token.value == "uniform") {
					UniformBuilder()
				} else {
					null
				}
			} else if (builder.type == null) {
				if (token.type == TokenType.STRING) {
					val type = typeNameToUniformType[token.value]
						?: throw Exception("Unsupported uniform type \"${token.value}\"")

					builder.type = type
					builder
				} else {
					throw Exception("Expected uniform type, got \"${token.value}\" instead")
				}
			} else if (builder.name == null) {
				if (token.type == TokenType.STRING) {
					builder.name = token.value
					builder
				} else {
					throw Exception("Expected uniform name, got \"${token.value}\" instead")
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

	override fun generate(file: File): TypeSpec? {
		if (!file.isDirectory) return null

		val shaderName = file.name.replaceFirstChar { it.uppercase() }

		val files = file.listFiles() ?: throw Exception("bad folder for shader $shaderName")

		val vertexShader = files.find { it.isFile && it.name == "vert.glsl" }
			?: throw Exception("No vertex shader (vert.glsl) found for shader $shaderName")

		val fragmentShader = files.find { it.isFile && it.name == "frag.glsl" }
			?: throw Exception("No fragment shader (frag.glsl) found for shader $shaderName")

		val uniforms = ArrayList<Uniform>()

		println(tokenize(vertexShader.readText()).joinToString { "\"${it.value}\"" })
		println(tokenize(fragmentShader.readText()).joinToString { "\"${it.value}\"" })

		uniforms.addAll(extractUniforms(tokenize(vertexShader.readText())))
		uniforms.addAll(extractUniforms(tokenize(fragmentShader.readText())))

		println(uniforms.joinToString { "uniform ${it.name}: ${it.type}${if (it.array) "[]" else ""}" })

		return TypeSpec.classBuilder("${shaderName}Shader").superclass(Shader::class).build()

		//val resourceField = TypeSpec.
		//val o = TypeSpec.objectBuilder("${shaderName}Shader").superclass(ShaderResource::class).build()
	}
}

fun main() {
	val shaderResourceGenerator = ShaderResourceGenerator()

	shaderResourceGenerator.generate(File("src/main/resources/shaders/checker"))
}
