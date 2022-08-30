package ru.gorshenev.themesstyles

object Errors {

    class MessageError(override val message: String?) : Throwable()

    class ReactionAlreadyExist : Throwable()
}