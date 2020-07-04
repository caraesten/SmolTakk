package repositories

import db.Room
import models.Reply
import models.Topic
import models.User
import org.jetbrains.exposed.sql.*
import java.time.LocalDateTime

private typealias TopicId = Int

interface MessagesRepository {
    fun getActiveTopics(): List<Topic>
    fun getTopicById(id: Int): Topic?
    fun createTopic(title: String, body: String, author: User): TopicId?
    fun createReply(parentId: Int, body: String, author: User): TopicId?
}

class MessagesRepositoryImpl(private val userRepository: UserRepository) : MessagesRepository {
    private enum class TopicHydrationType {
        DEEP, ABBREVIATED, SHALLOW
    }
    override fun getActiveTopics(): List<Topic> {
        return db.Topic.innerJoin(Room).select { db.Room.isActive eq true }.andWhere { db.Room.id eq db.Topic.room }.map { topicResult ->
            hydrateTopic(topicResult, TopicHydrationType.ABBREVIATED)
        }
    }

    override fun getTopicById(id: TopicId): Topic? {
        return db.Topic.select { db.Topic.id eq id }.firstOrNull()?.let {
            hydrateTopic(it, TopicHydrationType.DEEP)
        }
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
        return Topic(
            title = row[db.Topic.title],
            body = row[db.Topic.body],
            posted = row[db.Topic.posted],
            author = userRepository.findUserById(row[db.Topic.author]) ?: User.getEmptyUser(),
            // TODO: Clean up and optimize these queries!!!
            replies = topicQuery?.let {
                it.map(::hydrateReply)
            } ?: emptyList())
    }

    private fun hydrateReply(row: ResultRow): Reply {
        return Reply(
            author = userRepository.findUserById(row[db.Reply.author]) ?: User.getEmptyUser(),
            body = row[db.Reply.body],
            posted = row[db.Reply.posted]
        )
    }
}
