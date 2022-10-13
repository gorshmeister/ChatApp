package ru.gorshenev.themesstyles.utils.reflection

object Defaults {

    private val DOUBLE_DEFAULT = java.lang.Double.valueOf(0.0)
    private val FLOAT_DEFAULT = java.lang.Float.valueOf(0f)

    @JvmStatic
    fun <T> defaultValue(type: Class<T>): T? {
        return when (type) {
            Boolean::class.javaPrimitiveType -> java.lang.Boolean.FALSE as T
            Char::class.javaPrimitiveType -> Character.valueOf('\u0000') as T
            Byte::class.javaPrimitiveType -> java.lang.Byte.valueOf(0.toByte()) as T
            Short::class.javaPrimitiveType -> 0.toShort() as T
            Int::class.javaPrimitiveType -> Integer.valueOf(0) as T
            Long::class.javaPrimitiveType -> java.lang.Long.valueOf(0L) as T
            Float::class.javaPrimitiveType -> FLOAT_DEFAULT as T
            Double::class.javaPrimitiveType -> DOUBLE_DEFAULT as T
            else -> null
        }
    }
}
