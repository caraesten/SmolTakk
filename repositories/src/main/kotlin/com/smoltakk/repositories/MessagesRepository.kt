package com.smoltakk.repositories

import com.smoltakk.models.*
import com.smoltakk.repositories.di.RepositorySingleton
import org.jetbrains.exposed.sql.*
import java.time.LocalDateTime
import javax.inject.Inject
import com.smoltakk.db.Reply as DbReply
import com.smoltakk.db.Room as DbRoom
import com.smoltakk.db.Topic as DbTopic

private typealias TopicId = Int

interface MessagesRepository : Repository {
    fun getActiveRoom(hydrateTopics: Boolean = true): Room?
    fun getAllRooms(hydrateTopics: Boolean = false): List<Room>
    fun getTopicsForRoom(id: Int): List<Topic>
    fun getTopicById(id: Int): Topic?
    fun getReplyById(id: Int): Reply?
    fun createRoom(): Room?
    fun createTopic(title: String, body: String, author: User): TopicId?
    fun createReply(parentId: Int, body: String, author: User): TopicId?
    fun deleteMessage(message: Message): Boolean
    fun carryOverTopic(topicId: TopicId)
}

@RepositorySingleton
class MessagesRepositoryImpl @Inject constructor(override val database: Database, private val userRepository: UserRepository) :
    MessagesRepository {
    private enum class TopicHydrationType {
        DEEP, ABBREVIATED, SHALLOW
    }

    override fun getActiveRoom(hydrateTopics: Boolean): Room? {
        return DbRoom.select { DbRoom.isActive eq true }.firstOrNull()?.let {
            val roomId = it[DbRoom.id].value
            Room(if (hydrateTopics) getTopicsForRoom(roomId) else emptyList(), it[DbRoom.created], roomId)
        }
    }

    override fun getAllRooms(hydrateTopics: Boolean): List<Room> {
        return DbRoom.selectAll().map {
            val roomId = it[DbRoom.id].value
            Room(if (hydrateTopics) getTopicsForRoom(roomId) else emptyList(), it[DbRoom.created], roomId)
        }
    }

    override fun getTopicsForRoom(id: Int): List<Topic> {
        return DbTopic.innerJoin(DbRoom).select { DbRoom.id eq id }
            .andWhere { DbRoom.id eq DbTopic.room }
            .orderBy( DbTopic.posted, SortOrder.DESC)
            .map { topicResult ->
                hydrateTopic(topicResult,
                    TopicHydrationType.ABBREVIATED
                )
            }
    }

    override fun getTopicById(id: TopicId): Topic? {
        return DbTopic.select { DbTopic.id eq id }.firstOrNull()?.let {
            hydrateTopic(it,
                TopicHydrationType.DEEP
            )
        }
    }

    override fun getReplyById(id: Int): Reply? {
        return DbReply.select { DbReply.id eq id }.firstOrNull()?.let {
            Reply(
                topic = getTopicById(it[DbReply.parent])!!,
                id = it[DbReply.id].value,
                author = userRepository.findUserById(it[DbReply.author])!!,
                body = it[DbReply.body],
                posted = it[DbReply.posted]
            )
        }
    }

    override fun createRoom(): Room? {
        // Only one room can be active at a time, so we want to find the active one and deselect it, if it exists
        val active = getActiveRoom(false)
        if (active != null) {
            DbRoom.update({ DbRoom.id eq active.id }) {
                it[DbRoom.isActive] = false
            }
        }
        val createdAt = LocalDateTime.now()
        val isActive = true
        val id = DbRoom.insertAndGetId {
            it[DbRoom.created] = createdAt
            it[DbRoom.isActive] = isActive
        }.value
        return Room(emptyList(), createdAt, id)
    }

    override fun createTopic(title: String, body: String, author: User): TopicId? {
        val activeRoom = DbRoom.select { DbRoom.isActive eq true }.firstOrNull()
        return activeRoom?.let { roomRow ->
            val roomId = roomRow[DbRoom.id]
            DbTopic.insertAndGetId {
                it[DbTopic.room] = roomId.value
                it[DbTopic.author] = author.id
                it[DbTopic.title] = title
                it[DbTopic.body] = body
                it[DbTopic.posted] = LocalDateTime.now()
            }.value
        }
    }

    override fun createReply(parentId: TopicId, body: String, author: User): TopicId? {
        val topic = getTopicById(parentId) ?: return null
        DbReply.insertAndGetId {
            it[DbReply.parent] = parentId
            it[DbReply.author] = author.id
            it[DbReply.body] = body
            it[DbReply.posted] = LocalDateTime.now()
        }
        DbTopic.update({ DbTopic.id eq topic.id }) {
            it[DbTopic.posted] = LocalDateTime.now()
        }
        return parentId
    }

    override fun deleteMessage(message: Message): Boolean {
        return when (message) {
            is Topic -> {
                // as far as I can tell, the ORM doesn't support cascades over FKeys
                DbReply.deleteWhere { DbReply.parent eq message.id }
                DbTopic.deleteWhere { DbTopic.id eq message.id } > 0
            }
            is Reply -> {
                DbReply.deleteWhere { DbReply.id eq message.id } > 0
            }
            else -> {
                false
            }
        }
    }

    override fun carryOverTopic(topicId: TopicId) {
        val activeRoom = getActiveRoom(false)
        if (activeRoom == null) {
            return
        }
        DbTopic.update({ DbTopic.id eq topicId } ){
            it[DbTopic.room] = activeRoom.id
        }
    }

    private fun hydrateTopic(row: ResultRow, type: TopicHydrationType): Topic {
        val topicId = row[DbTopic.id].value
        val topicQuery = when (type) {
            TopicHydrationType.DEEP -> {
                DbReply.select { DbReply.parent eq topicId}.orderBy(DbReply.posted to SortOrder.ASC)
            }
            TopicHydrationType.ABBREVIATED -> {
                DbReply.select { DbReply.parent eq topicId}.orderBy(DbReply.posted to SortOrder.ASC).limit(3)
            }
            TopicHydrationType.SHALLOW -> {
                null
            }
        }
        val replyCount = DbReply.select { DbReply.parent eq topicId }.count().toInt()
        // Construct two topics: the base level topic, for the replies, and the one that contains the replies
        val topic = Topic(
            title = row[DbTopic.title],
            id = row[DbTopic.id].value,
            body = row[DbTopic.body],
            posted = row[DbTopic.posted],
            author = userRepository.findUserById(row[DbTopic.author]) ?: User.getEmptyUser(),
            // TODO: Clean up and optimize these queries!!!
            replies = emptyList(),
            replyCount = replyCount)
        return topic.copy(replies = topicQuery?.let {
            it.map { replyRow -> hydrateReply(replyRow, topic) }
        } ?: emptyList())
    }

    private fun hydrateReply(row: ResultRow, topic: Topic): Reply {
        return Reply(
            author = userRepository.findUserById(row[DbReply.author]) ?: User.getEmptyUser(),
            id = row[DbReply.id].value,
            body = row[DbReply.body],
            posted = row[DbReply.posted],
            topic = topic
        )
    }
}
