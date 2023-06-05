package com.balduvian.cnge.graphics

import org.joml.Matrix2f
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.lwjgl.opengl.GL46.*

data class ShaderData(
	val program: Int,
	val locations: Array<Int>,
)

open class Shader(
	shaderData: ShaderData,
) : Disposable {
	private val program = shaderData.program
	val locations = shaderData.locations

	companion object {
		private const val VERSION = "460 core"

		val matrixValues = FloatArray(16)

		val pvm = Matrix4f()
		val normalMatrix = Matrix3f()

		private val includeCache = HashMap<String, String>()

		fun create(
			vertexSource: String,
			fragmentSource: String,
			vararg uniforms: String,
		): ShaderData {
			val program = glCreateProgram()

			val fragmentShader = createShader(fragmentSource, GL_FRAGMENT_SHADER)
			val vertexShader = createShader(vertexSource, GL_VERTEX_SHADER)

			glAttachShader(program, vertexShader)
			glAttachShader(program, fragmentShader)

			glLinkProgram(program)
			if (glGetProgrami(program, GL_LINK_STATUS) != 1) {
				throw Exception(glGetProgramInfoLog(program))
			}

			glDetachShader(program, vertexShader)
			glDetachShader(program, fragmentShader)

			glDeleteShader(vertexShader)
			glDeleteShader(fragmentShader)

			val locations = Array(uniforms.size) {
				glGetUniformLocation(program, uniforms[it])
			}
			for (i in locations.indices) {
				if (locations[i] == -1) throw Exception("Uniform ${uniforms[i]} was not found")
			}

			return ShaderData(program, locations)
		}

		fun processSource(source: String): String {
			if (Parsers.findLinesStartingWith(source, "#version ").isNotEmpty())
				throw Exception("Please do not specify shader version")

			val includes = Parsers.findLinesStartingWith(source, "#include ")

			val includeSources = includes.map { (startIndex, endIndex, restOfLine) ->
				val resourceURL = Parsers.parseInAngleBrackets(restOfLine)
					?: throw Exception("No < > brackets found in #include statement")

				includeCache[resourceURL] ?:
					this::class.java.getResource(resourceURL)?.readText()
					?: throw Exception("Could not find include for \"${resourceURL}\"")
			}

			return if (includes.isEmpty()) {
				"#version $VERSION\n$source"

			} else {
				val builder = StringBuilder()
				builder.append("#version $VERSION\n")

				for (i in includes.indices) {
					/* append the part before this include */
					builder.append(source.substring(
						(if (i == 0) 0 else includes[i - 1].endIndex) until includes[i].startIndex
					))

					/* append the include source */
					builder.append(includeSources[i])
				}

				/* append the part after the last include */
				builder.append(source.substring(includes.last().endIndex until source.length))
				builder.toString()
			}
		}

		fun createShader(source: String, type: Int): Int {
			val shader = glCreateShader(type)

			glShaderSource(shader, source)
			glCompileShader(shader)

			val shaderError = glGetShaderi(shader, GL_COMPILE_STATUS)

			if (shaderError != 1) throw Exception(glGetShaderInfoLog(shader))

			return shader
		}
	}

	/* uniforms */

	fun enable(): Shader {
		glUseProgram(program)
		return this
	}

	fun uniformPVM(index: Int, projectionView: Matrix4f, model: Matrix4f): Shader {
		glUniformMatrix4fv(index, false, projectionView.mul(model, pvm).get(matrixValues))
		return this
	}

	fun uniformModel(index: Int, model: Matrix4f): Shader {
		glUniformMatrix4fv(index, false, model.get(matrixValues))
		return this
	}

	fun uniformProjView(index: Int, projectionView: Matrix4f): Shader {
		glUniformMatrix4fv(index, false, projectionView.get(matrixValues))
		return this
	}

	fun uniformNormal(index: Int, model: Matrix4f): Shader {
		glUniformMatrix3fv(index, false, model.normal(normalMatrix).get(matrixValues))
		return this
	}

	fun uniformFloat(index: Int, value: Float): Shader {
		glUniform1f(locations[index], value)
		return this
	}
	fun uniformVector2(index: Int, x: Float, y: Float): Shader {
		glUniform2f(locations[index], x, y)
		return this
	}
	fun uniformVector3(index: Int, x: Float, y: Float, z: Float): Shader {
		glUniform3f(locations[index], x, y, z)
		return this
	}
	fun uniformVector4(index: Int, x: Float, y: Float, z: Float, w: Float): Shader {
		glUniform4f(locations[index], x, y, z, w)
		return this
	}
	fun uniformVector4(index: Int, values: Array<Float>): Shader {
		glUniform4f(locations[index], values[0], values[1], values[2], values[3])
		return this
	}
	fun uniformVector4(index: Int, values: FloatArray): Shader {
		glUniform4f(locations[index], values[0], values[1], values[2], values[3])
		return this
	}
	fun uniformFloatArray(index: Int, values: FloatArray): Shader {
		glUniform1fv(locations[index], values)
		return this
	}
	fun uniformVector2Array(index: Int, values: FloatArray): Shader {
		glUniform2fv(locations[index], values)
		return this
	}
	fun uniformVector3Array(index: Int, values: FloatArray): Shader {
		glUniform3fv(locations[index], values)
		return this
	}
	fun uniformVector4Array(index: Int, values: FloatArray): Shader {
		glUniform4fv(locations[index], values)
		return this
	}
	fun unfiformMatrix2(index: Int, values: Matrix2f): Shader {
		glUniformMatrix2fv(index, false, values.get(matrixValues))
		return this
	}
	fun unfiformMatrix3(index: Int, values: Matrix3f): Shader {
		glUniformMatrix3fv(index, false, values.get(matrixValues))
		return this
	}
	fun unfiformMatrix4(index: Int, values: Matrix4f): Shader {
		glUniformMatrix4fv(index, false, values.get(matrixValues))
		return this
	}

	override fun destroy() {
		glDeleteProgram(program)
	}
}
