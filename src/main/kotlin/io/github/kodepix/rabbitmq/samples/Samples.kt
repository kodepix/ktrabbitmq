@file:Suppress("UNUSED_VARIABLE")

package io.github.kodepix.rabbitmq.samples

import com.rabbitmq.client.BuiltinExchangeType.*
import io.github.kodepix.rabbitmq.*


internal fun publishSample1() {
    val channel = createChannel()
    channel.publish(Any())
}


internal fun publishSample2() {
    val channel = createChannel()
    channel.publish(Any(), routingKey = "")
}


internal fun consumeSample1() {
    consume(Any::class) {
        // Work with received data.
    }
}


internal fun consumeSample2() {
    consume<Any> {
        // Work with received data.
    }
}


internal fun shutdownRabbitMQSample() {
    shutdownRabbitMQ()
}


internal fun createChannelSample() {
    val channel = createChannel()
}


internal fun configureRabbitMQSample() {

    val fanout = object : ExchangeDeclaration {
        override val subject = Any::class
        override val name = "subject-name"
        override val type = FANOUT
    }

    val queue = directQueueDeclaration<Any>(
        queue = "context-video",
        scope = object : ConsumerScope {
            override val name = "consumer1"
        }
    )

    configureRabbitMQ {
        exchangeDeclarations = listOf(fanout)
        queueDeclarations = listOf(queue)
    }
}
