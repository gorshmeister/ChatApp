package ru.gorshenev.themesstyles.data.network

import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.*
import ru.gorshenev.themesstyles.data.network.model.*

interface ZulipApi {
    @GET("users/{id}")
    fun getUser(@Path("id") id: Int): Single<GetOneUserResponse>

    @GET("users")
    fun getUsers(): Single<GetUserResponse>

    @GET("users/{user_id_or_email}/presence")
    fun getUserPresence(@Path("user_id_or_email") id: Int): Single<GetUserPresenceResponse>


    @GET("messages/{message_id}")
    fun getMessage(
        @Path("message_id") id: Int,
        @Query("apply_markdown") applyMarkdown: Boolean = false,
    ): Single<GetOneMessageResponse>

    @GET("messages")
    fun getMessages(
        @Query("anchor") anchor: Long,
        @Query("num_before") numBefore: Int,
        @Query("num_after") numAfter: Int = 0,
        @Query("narrow") narrow: String,
        @Query("client_gravatar") clientGravatar: Boolean,
        @Query("apply_markdown") applyMarkdown: Boolean
    ): Single<GetMessageResponse>

    @POST("messages")
    fun sendMessage(
        @Query("type") type: String = "stream",
        @Query("to") to: String,
        @Query("topic") topic: String,
        @Query("content") content: String
    ): Single<CreateMessageResponse>


    @POST("messages/{message_id}/reactions")
    fun addEmoji(
        @Path("message_id") msgId: Int,
        @Query("emoji_name") name: String,
    ): Single<CreateReactionResponse>

    @DELETE("messages/{message_id}/reactions")
    fun deleteEmoji(
        @Path("message_id") msgId: Int,
        @Query("emoji_name") name: String,
    ): Single<CreateReactionResponse>


    @GET("streams")
    fun getStreamsAll(): Single<GetStreamResponse>

    @GET("users/me/subscriptions")
    fun getStreamsSubs(): Single<GetStreamResponse>

    @GET("users/me/{stream_id}/topics")
    fun getTopics(@Path("stream_id") id: Int): Single<GetTopicResponse>


    @POST("register")
    fun getQueue(
        @Query("event_types") types: String,
        @QueryMap narrow: Map<String, String>,
        @Query("slim_presence") slimPresence: Boolean = true,
    ): Single<CreateQueueResponse>

    @GET("events")
    fun getEventsFromQueue(
        @Query("queue_id") queueId: String,
        @Query("last_event_id") lastId: Int
    ): Single<GetMessageEventsResponse>

    @GET("events")
    fun getEmojiEventsFromQueue(
        @Query("queue_id") queueId: String,
        @Query("last_event_id") lastId: Int
    ): Single<GetEmojiEventsResponse>
}

