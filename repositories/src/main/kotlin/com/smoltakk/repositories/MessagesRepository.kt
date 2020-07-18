package com.smoltakk.repositories

import com.smoltakk.models.Reply
import com.smoltakk.models.Room
import com.smoltakk.models.Topic
import com.smoltakk.models.User
import org.jetbrains.exposed.sql.*
import java.time.LocalDateTime
import com.smoltakk.db.Reply as DbReply
import com.smoltakk.db.Room as DbRoom
import com.smoltakk.db.Topic as DbTopic

private typealias TopicId = Int

interface MessagesRepository : Repository {
    fun getActiveRoom(hydrateTopics: Boolean = true): Room?
    fun getAllRooms(hydrateTopics: Boolean = false): List<Room>
    fun getTopicsForRoom(id: Int): List<Topic>
    fun getTopicById(id: Int): Topic?
    fun createRoom(): Room?
    fun createTopic(title: String, body: String, author: User): TopicId?
    fun createReply(parentId: Int, body: String, author: User): TopicId?
    fun carryOverTopic(topicId: TopicId)
}

class MessagesRepositoryImpl(override val database: Database, private val userRepository: UserRepository) :
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
        return DbTopic.innerJoin(DbRoom).select { DbRoom.id eq id }.andWhere { DbRoom.id eq DbTopic.room }.map { topicResult ->
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
        // TODO: Check if topic exists + belongs to an active room before allowing the reply
        DbReply.insertAndGetId {
            it[DbReply.parent] = parentId
            it[DbReply.author] = author.id
            it[DbReply.body] = body
            it[DbReply.posted] = LocalDateTime.now()
        }
        return parentId
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
                DbReply.select { DbReply.parent eq topicId}.orderBy(DbReply.posted to SortOrder.DESC)
            }
            TopicHydrationType.ABBREVIATED -> {
                DbReply.select { DbReply.parent eq topicId}.orderBy(DbReply.posted to SortOrder.DESC).limit(3)
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
            body = row[DbReply.body],
            posted = row[DbReply.posted],
            topic = topic
        )
    }
}
