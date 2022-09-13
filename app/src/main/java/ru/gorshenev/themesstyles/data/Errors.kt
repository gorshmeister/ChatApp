package ru.gorshenev.themesstyles.data

object Errors {

    class MessageError(override val message: String?) : Throwable()

    class ReactionAlreadyExist : Throwable()
}