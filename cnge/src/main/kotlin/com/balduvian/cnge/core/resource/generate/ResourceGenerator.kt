package com.balduvian.cnge.core.resource.generate

import java.io.File
import java.nio.file.Path

interface ResourceGenerator {
	fun getFolder(): Path

	fun generate(file: File, output: File)
}
