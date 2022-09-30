package ru.gorshenev.themesstyles.data.database.dao

import androidx.room.Embedded
import androidx.room.Relation
import ru.gorshenev.themesstyles.data.database.entities.MessageEntity
import ru.gorshenev.themesstyles.data.database.entities.ReactionEntity

class MessageWithReactions {
    @Embedded
    var message: MessageEntity? = null

    @Relation(parentColumn = "msgId", entityColumn = "message_id")
    var reactions : List<ReactionEntity> = ArrayList()
}
