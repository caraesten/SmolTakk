package views.viewmodels

import com.smoltakk.models.Message
import com.smoltakk.models.Room
import com.smoltakk.models.Urls.ROOM_URL
import com.smoltakk.models.Urls.TOPIC_URL
import com.smoltakk.models.User
import java.time.LocalDateTime
import java.time.Period
import kotlin.time.ExperimentalTime
import kotlin.time.toKotlinDuration

data class Messages<T : Message>(val messages: List<T>,
                                 val rootId: Int,
                                 val activeUser: User,
                                 val activeRoom: Room,
                                 val header: Message? = null) {

    // I like the explicitness of this, it makes the template easier to read :)
    val isReplies = header != null
    val isTopics = header == null

    // For when we want to show the input
    val firstTwoMessages = messages.take(2)
    val remainingMessages = if (messages.size > 2) messages.takeLast(messages.size - 2) else emptyList()

    val postTopic = "$ROOM_URL$rootId"
    val postReply = "$TOPIC_URL$rootId"

    @ExperimentalTime
    private val roomTtl = java.time.Duration.between(LocalDateTime.now(), activeRoom.created + Period.ofDays(ROOM_TTL_DAYS)).toKotlinDuration()
    @ExperimentalTime
    private val duration = roomTtl.toComponents { days: Int, hours: Int, minutes: Int, _: Int, _: Int ->
        DurationComponents(days, hours, minutes)
    }

    @ExperimentalTime
    val roomTtlDays = duration.days
    @ExperimentalTime
    val roomTtlHours = duration.hours
    @ExperimentalTime
    val roomTtlMinutes = duration.minutes

    val topicUrlBase = TOPIC_URL

    companion object {
        private const val ROOM_TTL_DAYS = 3
    }
    private data class DurationComponents(val days: Int, val hours: Int, val minutes: Int)

}