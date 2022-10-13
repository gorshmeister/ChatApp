package ru.gorshenev.themesstyles.utils.reflection

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

object ReflectionUtils {
    private val DEFAULT_VALUE: InvocationHandler = DefaultValueInvocationHandler()

    fun <T> createStub(interfaceType: Class<T>): T {
        return Reflection.newProxy(interfaceType, DEFAULT_VALUE)
    }

    private class DefaultValueInvocationHandler : InvocationHandler {
        override fun invoke(
            proxy: Any,
            method: Method,
            args: Array<Any>
        ): Any? {
            return Defaults.defaultValue(method.returnType)
        }
    }
}