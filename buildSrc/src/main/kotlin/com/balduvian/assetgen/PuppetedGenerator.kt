package com.balduvian.assetgen

import java.io.File
import java.nio.file.Path
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.typeOf

class PuppetedGenerator(
	val instance: Any,
	val getFolderFunction: KFunction<*>,
	val generateFunction: KFunction<*>,
) {
	fun getFolder(): Path {
		return getFolderFunction.call(instance) as Path
	}

	fun generate(file: File, output: File) {
		generateFunction.call(instance, file, output)
	}

	companion object {
		fun create(clazz: KClass<*>): PuppetedGenerator {
			val instance = clazz.primaryConstructor?.call()
				?: throw Exception("Class ${clazz.simpleName} does not have a default constructor")

			val getFolderFunction = clazz.declaredFunctions.find { it.name == "getFolder" }
				?: throw Exception("Class ${clazz.simpleName} does not have a getFolder() function")

			if (getFolderFunction.parameters.size != 1 || getFolderFunction.parameters.first().kind != KParameter.Kind.INSTANCE) {
				throw Exception("${clazz.simpleName}.getFolder() should take no parameters")
			}

			if (getFolderFunction.returnType != typeOf<Path>()) {
				throw Exception("${clazz.simpleName}.getFolder() should return a Path")
			}

			val generateFunction = clazz.declaredFunctions.find { it.name == "generate" }
				?: throw Exception("Class ${clazz.simpleName} does not have a generate() function")

			if (
				generateFunction.parameters.size != 3 ||
				generateFunction.parameters[0].kind != KParameter.Kind.INSTANCE ||
				generateFunction.parameters[1].type != typeOf<File>() ||
				generateFunction.parameters[2].type != typeOf<File>()
			) {
				throw Exception("${clazz.simpleName}.generate() should take a File parameter and a File parameter")
			}

			if (generateFunction.returnType != typeOf<Unit>()) {
				throw Exception("${clazz.simpleName}.generate() should return Unit")
			}

			return PuppetedGenerator(instance, getFolderFunction, generateFunction)
		}
	}
}
