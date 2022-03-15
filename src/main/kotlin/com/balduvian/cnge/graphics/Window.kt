package com.balduvian.cnge.graphics

import game.main
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWImage
import org.lwjgl.glfw.GLFWVidMode
import org.lwjgl.opengl.GL.createCapabilities
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.net.URI
import java.net.URL
import java.nio.ByteBuffer
import javax.imageio.ImageIO

class Window(
	var videoMode: GLFWVidMode,
	val window: Long,
	var vsync: Boolean,
	var width: Int,
	var height: Int,
	var full: Boolean,
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
			vsync: Boolean
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

			return Window(videoMode, window, vsync, width, height, full)
		}
	}

	val input = Input()

	private fun onSizeChanged(window: Long, newWidth: Int, newHeight: Int) {
		this.width = newWidth
		this.height = newHeight
		input.didResize = true
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
			input.mouseX = x.toFloat()
			input.mouseY = y.toFloat()
		}
	}

	/* internal CNGE interface, do not call these */

	fun poll() {
		glfwPollEvents()
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

	fun setIconSingle(resource: URL) {
		val icon = GLFWImage.create(1)

		val image = ImageIO.read(resource)

		val byteBuffer = ByteBuffer.allocate(image.width * image.height * 4)

		val pixels = IntArray(image.width * image.height)
		image.getRGB(0, 0, image.width, image.height, pixels, 0, image.width)

		for (b in 0 until image.width * image.height) {
			byteBuffer.put(pixels[b].shr(16).toByte() /* r */)
			byteBuffer.put(pixels[b].shr( 8).toByte() /* g */)
			byteBuffer.put(pixels[b]        .toByte() /* b */)
			byteBuffer.put(pixels[b].shr(24).toByte() /* a */)
		}

		byteBuffer.flip()
		val glfwImage = GLFWImage.create().set(image.width, image.height, byteBuffer)

		icon.put(0, glfwImage)
		icon.flip()

		glfwSetWindowIcon(window, icon)
	}

	fun setIcon(resource: URL, vararg sizes: Int) {
		val mainImage = ImageIO.read(resource)

		val iconSet = GLFWImage.create(sizes.size)
		val byteBuffers = Array(sizes.size) { i ->
			ByteBuffer.allocate(sizes[i] * sizes[i] * 4)
		}
		val glfwImages = Array(sizes.size) { i ->
			GLFWImage.create()
		}

		for (i in sizes.indices) {
			val size = sizes[i]
			val byteBuffer = byteBuffers[i]

			val scaledImage = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
			val graphics = scaledImage.createGraphics()
			graphics.drawImage(mainImage, 0, 0, size, size, 0, 0, mainImage.width, mainImage.height, null)

			val pixels = IntArray(size * size)
			scaledImage.getRGB(0, 0, size, size, pixels, 0, size)

			for (b in 0 until size * size) {
				byteBuffer.put(pixels[b].shr(16).toByte() /* r */)
				byteBuffer.put(pixels[b].shr( 8).toByte() /* g */)
				byteBuffer.put(pixels[b]        .toByte() /* b */)
				byteBuffer.put(pixels[b].shr(24).toByte() /* a */)
			}

			byteBuffer.flip()
			iconSet.put(glfwImages[i].set(size, size, byteBuffer))
		}

		iconSet.flip()
		glfwSetWindowIcon(window, iconSet)
	}

	fun shouldClose(): Boolean {
		return glfwWindowShouldClose(window)
	}

	fun refreshRate(): Int {
		return videoMode.refreshRate()
	}
}
