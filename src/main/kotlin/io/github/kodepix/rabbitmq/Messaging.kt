package io.github.kodepix.rabbitmq

import com.rabbitmq.client.*
import io.github.kodepix.*
import java.sql.*
import kotlin.reflect.*
import kotlin.text.Charsets.UTF_8


/**
 * Publish a message.
 *
 * Queue declaration and routing key for class of value will be found.
 *
 * Publishing to a non-existent exchange will result in a channel-level
 * protocol exception, which closes the channel.
 *
 * Invocations of [publish] will eventually block if a
 * [resource-driven alarm](https://www.rabbitmq.com/alarms.html) is in effect.
 *
 * Usage:
 *
 * ```kotlin
 * val channel = createChannel()
 * channel.publish(Any())
 * ```
 *
 * @param value object for message body
 * @param props other properties for the message: routing headers, etc
 *
 * @sample io.github.kodepix.rabbitmq.samples.publishSample1
 */
@RabbitMQDsl
fun Channel.publish(value: Any, props: AMQP.BasicProperties? = null) = run {
    val (_, _, routingKey, exchange) = declaration(value::class)
    publish(exchange, routingKey, value, props)
}


/**
 * Publish a message.
 *
 * Exchange declaration for class of value will be found.
 *
 * Publishing to a non-existent exchange will result in a channel-level
 * protocol exception, which closes the channel.
 *
 * Invocations of [publish] will eventually block if a
 * [resource-driven alarm](https://www.rabbitmq.com/alarms.html) is in effect.
 *
 * Usage:
 *
 * ```kotlin
 * val channel = createChannel()
 * channel.publish(Any(), routingKey = "")
 * ```
 *
 * @param value object for message body
 * @param routingKey routing key
 * @param props other properties for the message: routing headers, etc
 *
 * @sample io.github.kodepix.rabbitmq.samples.publishSample2
 */
@RabbitMQDsl
fun Channel.publish(value: Any, routingKey: String, props: AMQP.BasicProperties? = null) = publish(exchange(value::class), routingKey, value, props)

@RabbitMQDsl
private fun Channel.publish(exchange: String, routingKey: String, value: Any, props: AMQP.BasicProperties? = null) =
    try {
        basicPublish(exchange, routingKey, props, value.toJson().toByteArray())
    } catch (e: Exception) {
        log.error(e) { "Error publishing message" }
    }


/**
 * Start a non-nolocal, non-exclusive consumer.
 *
 * Queue and consumer scope for class of consuming data will be found.
 *
 * Usage:
 *
 * ```kotlin
 * consume<Any> {
 *     // Work with received data.
 * }
 * ```
 *
 * @param number consumer number, used when running multiple concurrent consumers
 * @param fn consumed data processing
 *
 * @sample io.github.kodepix.rabbitmq.samples.consumeSample2
 */
@RabbitMQDsl
inline fun <reified T : Any> consume(number: Int? = null, noinline fn: (T) -> Unit) = consume(T::class, number, fn)


/**
 * Start a non-nolocal, non-exclusive consumer.
 *
 * Queue and consumer scope for class of consuming data will be found.
 *
 * Usage:
 *
 * ```kotlin
 * consume(Any::class) {
 *     // Work with received data.
 * }
 * ```
 *
 * @param clazz class of consuming data
 * @param number consumer number, used when running multiple concurrent consumers
 * @param fn consumed data processing
 *
 * @sample io.github.kodepix.rabbitmq.samples.consumeSample1
 */
@RabbitMQDsl
fun <T : Any> consume(clazz: KClass<T>, number: Int? = null, fn: (T) -> Unit) {

    log.info { "Consumer launched: for ${clazz.simpleName}" }

    val (queue, consumerScope) = declaration(clazz)

    createChannel().run {

        basicQos(2)
        basicConsume(
            queue,
            false,
            makeConsumerTag(consumerScope, queue, number),
            object : DefaultConsumer(this) {

                override fun handleDelivery(consumerTag: String?, envelope: Envelope, properties: AMQP.BasicProperties, body: ByteArray?) {

                    if (body != null) {
                        val json = body.toString(UTF_8)
                        try {
                            fn(objectMapper.readValue(json, clazz.java))
                            basicAck(envelope.deliveryTag, false)
                        } catch (e: SQLException) {
                            if (e.message?.contains("duplicate key value violates unique constraint") == true)
                                basicAck(envelope.deliveryTag, false)
                            else
                                logError(e, json)
                        } catch (e: Throwable) {
                            logError(e, json)
                        }
                    }
                }
            }
        )
    }
}

private val objectMapper = objectMapper()

private fun logError(e: Throwable, json: String) {
    log.error(e) { "Faulty JSON: $json" }
}

private fun makeConsumerTag(scope: ConsumerScope, queue: String, number: Int?) = "${scope.name.lowercase()}-${queue.replace(".", "-")}-consumer${if (number != null) "-$number" else ""}"


@DslMarker
annotation class RabbitMQDsl


private val log by logger()
