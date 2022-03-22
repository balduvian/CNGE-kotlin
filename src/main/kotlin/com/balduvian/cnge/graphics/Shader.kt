package com.balduvian.cnge.graphics

import org.joml.Matrix3f
import org.joml.Matrix4f
import org.lwjgl.opengl.GL46.*

class Shader(
	val program: Int,
	val projViewLocation: Int,
	val modelLocation: Int,
	val normalLocation: Int,
	val pvmLocation: Int,
	val locations: Array<Int>
) : GraphicsObject() {
	companion object {
		const val VERSION = "460 core"

		const val MODEL_NAME = "model"
		const val PROJVIEW_NAME = "projView"
		const val PVM_NAME = "pvm"
		const val NORMALMATRIX_NAME = "normalMatrix"

		val matrixValues = FloatArray(16)
		val matrixValues2 = FloatArray(16)
		val pvm = Matrix4f()
		val normalMatrix = Matrix3f()

		val includeCache = HashMap<String, String>()

		fun create(
			vertexSource: String,
			fragmentSource: String,
			vararg uniforms: String,
		): Shader {
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

			val modelLocation = glGetUniformLocation(program, MODEL_NAME)
			val projViewLocation = glGetUniformLocation(program, PROJVIEW_NAME)
			val normalMatrixLocation = glGetUniformLocation(program, NORMALMATRIX_NAME)

			val pvmLocation = glGetUniformLocation(program, PVM_NAME)
			if (pvmLocation == -1) throw Exception("No uniform for \"${PVM_NAME}\" was found")

			val locations = Array(uniforms.size) {
				glGetUniformLocation(program, uniforms[it])
			}
			for (i in locations.indices) {
				if (locations[i] == -1) throw Exception("Uniform ${uniforms[i]} was not found")
			}

			return Shader(program, projViewLocation, modelLocation, normalMatrixLocation, pvmLocation, locations)
		}

		fun processSource(source: String): String {
			val newlinesSource = source.filter { it != '\r' }

			if (Parsers.findLinesStartingWith(newlinesSource, "#version ").isNotEmpty())
				throw Exception("Please do not specify shader version")

			val includes = Parsers.findLinesStartingWith(newlinesSource, "#include ")

			val includeSources = includes.map { (startIndex, endIndex, resourceURL) ->
				 includeCache[resourceURL] ?:
					this::class.java.getResource(resourceURL)?.readText()
					?: throw Exception("Could not find include for \"${resourceURL}\"")
			}

			return if (includes.isEmpty()) {
				"#version $VERSION\n$newlinesSource"

			} else {
				val builder = StringBuilder()
				builder.append("#version $VERSION\n")

				for (i in includes.indices) {
					/* append the part before this include */
					builder.append(newlinesSource.substring(
						(if (i == 0) 0 else includes[i - 1].endIndex)
							until
							includes[i].startIndex
					))

					/* append the include source */
					builder.append(includeSources[i])
				}

				/* append the part after the last include */
				builder.append(newlinesSource.substring(includes.last().endIndex until newlinesSource.length))
				builder.toString()
			}
		}

		private fun createShader(source: String, type: Int): Int {
			val shader = glCreateShader(type)

			glShaderSource(shader, source)
			glCompileShader(shader)

			val shaderError = glGetShaderi(shader, GL_COMPILE_STATUS)

			if (shaderError != 1) throw Exception(glGetShaderInfoLog(shader))

			return shader
		}
	}

	private fun mulMvp(projectionView: Matrix4f, model: Matrix4f) {
		glUniformMatrix4fv(pvmLocation, false, projectionView.mul(model, pvm).get(matrixValues))
	}

	fun enable(projectionView: Matrix4f, model: Matrix4f): Shader {
		glUseProgram(program)
		mulMvp(projectionView, model)
		return this
	}
	fun enableModel(projectionView: Matrix4f, model: Matrix4f): Shader {
		glUseProgram(program)
		glUniformMatrix4fv(modelLocation, false, model.get(matrixValues2))
		mulMvp(projectionView, model)
		return this
	}
	fun enableProjView(projectionView: Matrix4f, model: Matrix4f): Shader {
		glUseProgram(program)
		glUniformMatrix4fv(projViewLocation, false, projectionView.get(matrixValues))
		mulMvp(projectionView, model)
		return this
	}
	fun enableProjViewModel(projectionView: Matrix4f, model: Matrix4f): Shader {
		glUseProgram(program)
		glUniformMatrix4fv(projViewLocation, false, projectionView.get(matrixValues))
		glUniformMatrix4fv(modelLocation, false, model.get(matrixValues))
		mulMvp(projectionView, model)
		return this
	}
	fun enableNormal(projectionView: Matrix4f, model: Matrix4f): Shader {
		glUseProgram(program)
		glUniformMatrix3fv(normalLocation, false, model.normal(normalMatrix).get(matrixValues))
		mulMvp(projectionView, model)
		return this
	}
	fun enableModelNormal(projectionView: Matrix4f, model: Matrix4f): Shader {
		glUseProgram(program)
		glUniformMatrix4fv(modelLocation, false, model.get(matrixValues))
		glUniformMatrix3fv(normalLocation, false, model.normal(normalMatrix).get(matrixValues))
		mulMvp(projectionView, model)
		return this
	}
	fun enableProjViewNormal(projectionView: Matrix4f, model: Matrix4f): Shader {
		glUseProgram(program)
		glUniformMatrix4fv(projViewLocation, false, projectionView.get(matrixValues))
		glUniformMatrix3fv(normalLocation, false, model.normal(normalMatrix).get(matrixValues))
		mulMvp(projectionView, model)
		return this
	}
	fun enableProjViewModelNormal(projectionView: Matrix4f, model: Matrix4f): Shader {
		glUseProgram(program)
		glUniformMatrix4fv(projViewLocation, false, projectionView.get(matrixValues))
		glUniformMatrix4fv(modelLocation, false, model.get(matrixValues))
		glUniformMatrix3fv(normalLocation, false, model.normal(normalMatrix).get(matrixValues))
		mulMvp(projectionView, model)
		return this
	}

	override fun destroy() {
		glDeleteProgram(program)
	}

	/* uniform helpers */

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
	fun uniformVector2Array(index: Int, values: FloatArray): Shader {
		glUniform2fv(locations[index], values)
		return this
	}
	fun unfiformMatrix3(index: Int, values: Matrix3f): Shader {
		glUniformMatrix3fv(projViewLocation, false, values.get(matrixValues))
		return this
	}
	fun unfiformMatrix4(index: Int, values: Matrix4f): Shader {
		glUniformMatrix4fv(projViewLocation, false, values.get(matrixValues))
		return this
	}
}
