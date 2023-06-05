package com.balduvian.cnge.graphics

import com.balduvian.cnge.core.util.Frame
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWImage
import org.lwjgl.glfw.GLFWVidMode
import org.lwjgl.opengl.GL.createCapabilities
import org.lwjgl.system.MemoryUtil
import java.net.URL
import javax.imageio.ImageIO

class Window(
	var videoMode: GLFWVidMode,
	val window: Long,
	var vsync: Boolean,
	var width: Int,
	var height: Int,
	var full: Boolean,
	var frame: Frame,
) {
	companion object {
		fun init(onError: (String) -> Unit): Boolean {
			glfwSetErrorCallback { code, description ->
				onError("GLFW ERROR CODE $code | ${GLFWErrorCallback.getDescription(description)}")
			}

			return glfwInit()
		}

		fun create(
			majorVersion: Int,
			minorVersion: Int,
			resizable: Boolean,
			decorated: Boolean,
			title: String,
			full: Boolean,
			vsync: Boolean,
			frame: Frame
		): Window? {
			glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, majorVersion)
			glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, minorVersion)
			glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
			glfwWindowHint(GLFW_RESIZABLE, if (resizable) 1 else 0)
			glfwWindowHint(GLFW_DECORATED, if (decorated) 1 else 0)

			val monitor = glfwGetPrimaryMonitor()
			if (monitor == 0L) return null

			val videoMode = glfwGetVideoMode(monitor) ?: return null

			val width = videoMode.width() / 2
			val height = videoMode.height() / 2

			val window = glfwCreateWindow(
				width,
				height,
				title,
				if (full) monitor else 0L,
				0L
			)
			if (window == 0L) return null

			glfwFocusWindow(window)

			glfwMakeContextCurrent(window)
			createCapabilities()

			glfwSwapInterval(if (vsync) 1 else 0)

			return Window(videoMode, window, vsync, width, height, full, frame)
		}
	}

	val input = Input(frame)

	private fun onSizeChanged(window: Long, newWidth: Int, newHeight: Int) {
		this.width = newWidth
		this.height = newHeight
		input.didResize = true
		input.bounds = input.frame.getBounds(width, height)
		input.bounds.setViewport()
	}

	init {
		glfwSetWindowSizeCallback(window, ::onSizeChanged)

		glfwSetKeyCallback(window) { _, key, scanCode, action, _ ->
			if (action == GLFW_PRESS) {
				input.keys[key] = Input.PRESSED
			} else if (action == GLFW_RELEASE) {
				input.keys[key] = Input.RELEASED
			}
		}

		glfwSetMouseButtonCallback(window) { _, button, action, _ ->
			if (action == GLFW_PRESS) {
				input.buttons[button] = Input.PRESSED
			} else if (action == GLFW_RELEASE) {
				input.buttons[button] = Input.RELEASED
			}
		}

		glfwSetCursorPosCallback(window) { _, x, y ->
			input.mouseX = x
			input.mouseY = y
		}

		onSizeChanged(window, width, height)
	}

	/* internal CNGE interface, do not call these */

	fun poll(): Input {
		glfwPollEvents()
		return input
	}

	fun postFrame() {
		input.keys.indices.forEach { i ->
			if (input.keys[i] == Input.PRESSED) input.keys[i] = Input.HELD
		}
		input.buttons.indices.forEach { i ->
			if (input.buttons[i] == Input.PRESSED) input.buttons[i] = Input.HELD
		}
		input.didResize = false
	}

	fun swap() {
		glfwSwapBuffers(window)
	}

	fun terminate() {
		glfwTerminate()
	}

	/* interface */

	fun setFullScreen(full: Boolean, vsync: Boolean) {
		this.full = full
		this.vsync = vsync

		val newMonitor = glfwGetPrimaryMonitor()
		val newVideoMode = glfwGetVideoMode(newMonitor) ?: return

		val newWidth = if (full) newVideoMode.width() else newVideoMode.width() / 2
		val newHeight = if (full) newVideoMode.height() else newVideoMode.height() / 2

		if (full) {
			glfwSetWindowMonitor(window, newMonitor, 0, 0, newWidth, newHeight, GLFW_DONT_CARE)
		} else {
			glfwSetWindowMonitor(window, 0L, newVideoMode.width() / 4, newVideoMode.height() / 4, newWidth, newHeight, GLFW_DONT_CARE)
		}

		glfwSwapInterval(if (vsync) 1 else 0)

		this.videoMode = newVideoMode
		onSizeChanged(window, newWidth, newHeight)
	}

	fun setShouldClose() {
		glfwSetWindowShouldClose(window, true)
	}

	fun clearIcon() {
		glfwSetWindowIcon(window, null)
	}

	fun setIcon(resource: URL) {
		val bufferedImage = ImageIO.read(resource)
		val buffer = Images.imageToByteBuffer(bufferedImage)

		glfwSetWindowIcon(
			window,
			GLFWImage.create(1).put(0, GLFWImage.create().set(bufferedImage.width, bufferedImage.height, buffer))
		)

		MemoryUtil.memFree(buffer)
	}

	fun setIconMulti(vararg resources: URL) {
		val iconSet = GLFWImage.create(resources.size)

		val byteBuffers = resources.map { resource ->
			val bufferImage = ImageIO.read(resource)
			val byteBuffer = Images.imageToByteBuffer(bufferImage)

			iconSet.put(GLFWImage.create().set(bufferImage.width, bufferImage.height, byteBuffer))

			byteBuffer
		}

		iconSet.flip()
		glfwSetWindowIcon(window, iconSet)

		byteBuffers.forEach{ byteBuffer ->
			MemoryUtil.memFree(byteBuffer)
		}
	}

	fun shouldClose(): Boolean {
		return glfwWindowShouldClose(window)
	}

	fun refreshRate(): Int {
		return videoMode.refreshRate()
	}
}
