package com.balduvian.cnge.graphics

import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL46.*

class VAO(
	val vao: Int,
	val drawMode: Int,
	val indexCount: Int,
	val buffers: Array<Int>,
	val indexBuffer: Int
): GraphicsObject() {
	sealed class Attribute(val perVertex: Int)
	class StaticAttribute(perVertex: Int, val data: FloatArray) : Attribute(perVertex)
	class DynamicAttribute(perVertex: Int, val elements: Int) : Attribute(perVertex)

	companion object {
		fun create(
			drawMode: Int,
			indices: IntArray,
			attributes: Array<Attribute>
		): VAO {
			val vao = glCreateVertexArrays()
			glBindVertexArray(vao)

			val buffers = Array(attributes.size) { 0 }

			/* other misc attributes */
			attributes.forEachIndexed { index, attribute ->
				buffers[index] = addAttribute(index, attribute)
			}

			/* create the index buffer */
			val ibo = glGenBuffers()
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo)
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)

			return VAO(vao, drawMode, indices.size, buffers, ibo)
		}

		/* to be called only in the create function */
		private fun addAttribute(location: Int, attribute: Attribute): Int {
			val buffer = glGenBuffers()
			glBindBuffer(GL_ARRAY_BUFFER, buffer)

			when (attribute) {
				is StaticAttribute -> glBufferData(GL_ARRAY_BUFFER, attribute.data, GL_STATIC_DRAW)
				is DynamicAttribute -> glBufferData(GL_ARRAY_BUFFER, attribute.elements * attribute.perVertex * 4L, GL_DYNAMIC_DRAW)
			}

			glVertexAttribPointer(location, attribute.perVertex, GL_FLOAT, false, 0, 0)
			glEnableVertexAttribArray(location)

			return buffer
		}
	}

	fun update(bufferIndex: Int, data: FloatArray) {
		glBindBuffer(GL_ARRAY_BUFFER, buffers[bufferIndex])
		GL15.glBufferSubData(GL_ARRAY_BUFFER, 0, data)
	}

	fun render() {
		glBindVertexArray(vao)
		glDrawElements(drawMode, indexCount, GL_UNSIGNED_INT, 0)
	}

	override fun destroy() {
		buffers.forEach { buffer ->
			glDeleteBuffers(buffer)
		}

		glDeleteBuffers(indexBuffer)

		glDeleteVertexArrays(vao)
	}
}
