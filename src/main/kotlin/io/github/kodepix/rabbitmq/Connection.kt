package io.github.kodepix.rabbitmq

import com.rabbitmq.client.*
import com.rabbitmq.client.AMQP.*
import com.rabbitmq.client.Connection
import io.github.kodepix.*


/**
 * Creates a new broker connection with configuring of exchanges and queues.
 *
 * Usage:
 *
 * ```kotlin
 * val fanout = object : ExchangeDeclaration {
 *     override val subject = Any::class
 *     override val name = "subject-name"
 *     override val type = FANOUT
 * }
 *
 * val queue = directQueueDeclaration<Any>(
 *     queue = "context-video",
 *     scope = object : ConsumerScope {
 *         override val name = "consumer1"
 *     }
 * )
 *
 * configureRabbitMQ {
 *     exchangeDeclarations = listOf(fanout)
 *     queueDeclarations = listOf(queue)
 * }
 * ```
 *
 * @param configure configuration block
 *
 * @sample io.github.kodepix.rabbitmq.samples.configureRabbitMQSample
 */
fun configureRabbitMQ(configure: RabbitMQConfig.() -> Unit) {

    val config = RabbitMQConfig().apply { configure(this) }

    runUntilSuccess {
        connection = factory.newConnection().apply {
            (this as Recoverable).addRecoveryListener(object : RecoveryListener {
                override fun handleTopologyRecoveryStarted(recoverable: Recoverable?) = createChannel().use { it.declareQueues(config.queueDeclarations) }
                override fun handleRecovery(recoverable: Recoverable?) = Unit
                override fun handleRecoveryStarted(recoverable: Recoverable?) = Unit
            })
        }
    }

    createChannel().use {
        it.declareExchanges(config.exchangeDeclarations)
        it.declareQueues(config.queueDeclarations)
    }
}

private lateinit var connection: Connection

private val factory by lazy {
    ConnectionFactory().apply {
        setUri(config.rabbitmq.uri)
        isAutomaticRecoveryEnabled = true
    }
}


private val config by extractConfig<Config>()

private data class Config(val rabbitmq: UriConfig) {
    data class UriConfig(val uri: String)
}


/**
 * RabbitMQ configuration data.
 *
 * @property exchangeDeclarations exchange declarations
 * @property queueDeclarations queue declarations
 */
class RabbitMQConfig {
    var exchangeDeclarations: List<ExchangeDeclaration> = emptyList()
    var queueDeclarations: List<QueueDeclaration> = emptyList()
}


/**
 * Abort RabbitMQ connection and all its channels with the [REPLY_SUCCESS] close code and message `OK`.
 * Forces the connection to close. Any encountered exceptions in the close operations are silently discarded.
 *
 * Usage:
 *
 * ```kotlin
 * shutdownRabbitMQ()
 * ```
 *
 * @sample io.github.kodepix.rabbitmq.samples.shutdownRabbitMQSample
 */
fun shutdownRabbitMQ() {
    log.info { "Shutdown RabbitMQ connection" }
    connection.abort()
}


/**
 * Create a new channel, using an internally allocated channel number.
 * If [automatic connection recovery](https://www.rabbitmq.com/api-guide.html#recovery)
 * is enabled, the channel returned by this method will be [Recoverable].
 *
 * Usage:
 *
 * ```kotlin
 * val channel = createChannel()
 * ```
 *
 * @sample io.github.kodepix.rabbitmq.samples.createChannelSample
 */
fun createChannel() = connection.createChannel()!!


private val log by logger()
