package com.balduvian.cnge.graphics

sealed class Option<T>

class Good<T>(val value: T): Option<T>()

class Bad<T>(val value: String): Option<T>() {
	fun <X> forward() = Bad<X>(value)
}
