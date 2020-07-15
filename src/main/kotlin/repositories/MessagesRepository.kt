package repositories

import models.Reply
import models.Room
import models.Topic
import models.User
import org.jetbrains.exposed.sql.*
import java.time.LocalDateTime

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

class MessagesRepositoryImpl(private val userRepository: UserRepository) : MessagesRepository {
    private enum class TopicHydrationType {
        DEEP, ABBREVIATED, SHALLOW
    }

    override fun getActiveRoom(hydrateTopics: Boolean): Room? {
        return db.Room.select { db.Room.isActive eq true }.firstOrNull()?.let {
            val roomId = it[db.Room.id].value
            Room(if (hydrateTopics) getTopicsForRoom(roomId) else emptyList(), it[db.Room.created], roomId)
        }
    }

    override fun getAllRooms(hydrateTopics: Boolean): List<Room> {
        return db.Room.selectAll().map {
            val roomId = it[db.Room.id].value
            Room(if (hydrateTopics) getTopicsForRoom(roomId) else emptyList(), it[db.Room.created], roomId)
        }
    }

    override fun getTopicsForRoom(id: Int): List<Topic> {
        return db.Topic.innerJoin(db.Room).select { db.Room.id eq id }.andWhere { db.Room.id eq db.Topic.room }.map { topicResult ->
            hydrateTopic(topicResult, TopicHydrationType.ABBREVIATED)
        }
    }

    override fun getTopicById(id: TopicId): Topic? {
        return db.Topic.select { db.Topic.id eq id }.firstOrNull()?.let {
            hydrateTopic(it, TopicHydrationType.DEEP)
        }
    }

    override fun createRoom(): Room? {
        // Only one room can be active at a time, so we want to find the active one and deselect it, if it exists
        val active = getActiveRoom(false)
        if (active != null) {
            db.Room.update({ db.Room.id eq active.id }) {
                it[db.Room.isActive] = false
            }
        }
        val createdAt = LocalDateTime.now()
        val isActive = true
        val id = db.Room.insertAndGetId {
            it[db.Room.created] = createdAt
            it[db.Room.isActive] = isActive
        }.value
        return Room(emptyList(), createdAt, id)
    }

    override fun createTopic(title: String, body: String, author: User): TopicId? {
        val activeRoom = db.Room.select { db.Room.isActive eq true }.firstOrNull()
        return activeRoom?.let { roomRow ->
            val roomId = roomRow[db.Room.id]
            db.Topic.insertAndGetId {
                it[db.Topic.room] = roomId.value
                it[db.Topic.author] = author.id
                it[db.Topic.title] = title
                it[db.Topic.body] = body
                it[db.Topic.posted] = LocalDateTime.now()
            }.value
        }
    }

    override fun createReply(parentId: TopicId, body: String, author: User): TopicId? {
        // TODO: Check if topic exists + belongs to an active room before allowing the reply
        db.Reply.insertAndGetId {
            it[db.Reply.parent] = parentId
            it[db.Reply.author] = author.id
            it[db.Reply.body] = body
            it[db.Reply.posted] = LocalDateTime.now()
        }
        return parentId
    }

    override fun carryOverTopic(topicId: TopicId) {
        val activeRoom = getActiveRoom(false)
        if (activeRoom == null) {
            return
        }
        db.Topic.update({ db.Topic.id eq topicId } ){
            it[db.Topic.room] = activeRoom.id
        }
    }

    private fun hydrateTopic(row: ResultRow, type: TopicHydrationType): Topic {
        val topicId = row[db.Topic.id].value
        val topicQuery = when (type) {
            TopicHydrationType.DEEP -> {
                db.Reply.select { db.Reply.parent eq topicId}.orderBy(db.Reply.posted to SortOrder.DESC)
            }
            TopicHydrationType.ABBREVIATED -> {
                db.Reply.select { db.Reply.parent eq topicId}.orderBy(db.Reply.posted to SortOrder.DESC).limit(3)
            }
            TopicHydrationType.SHALLOW -> {
                null
            }
        }
        // Construct two topics: the base level topic, for the replies, and the one that contains the replies
        val topic = Topic(
            title = row[db.Topic.title],
            body = row[db.Topic.body],
            posted = row[db.Topic.posted],
            author = userRepository.findUserById(row[db.Topic.author]) ?: User.getEmptyUser(),
            // TODO: Clean up and optimize these queries!!!
            replies = emptyList())
        return topic.copy(replies = topicQuery?.let {
            it.map { replyRow -> hydrateReply(replyRow, topic) }
        } ?: emptyList())
    }

    private fun hydrateReply(row: ResultRow, topic: Topic): Reply {
        return Reply(
            author = userRepository.findUserById(row[db.Reply.author]) ?: User.getEmptyUser(),
            body = row[db.Reply.body],
            posted = row[db.Reply.posted],
            topic = topic
        )
    }
}
