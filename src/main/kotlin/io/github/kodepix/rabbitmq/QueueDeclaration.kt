@file:Suppress("ktlint:standard:comment-wrapping", "InconsistentCommentForJavaParameter")

package io.github.kodepix.rabbitmq

import com.rabbitmq.client.*
import kotlin.reflect.*


/**
 * Returns an instance of [QueueDeclaration] for direct queues.
 *
 * @param queue queue name
 * @param scope custom description of microservice scope
 * @param arguments other properties (construction arguments) for the queue (empty by default)
 */
inline fun <reified T : Any> directQueueDeclaration(queue: String, scope: ConsumerScope, arguments: Map<String, Any> = emptyMap()) =
    QueueDeclaration(
        subject = T::class,
        queue = queue,
        consumerScope = scope,
        exchange = null,
        arguments = arguments,
    )


/**
 * Queue declaration configuration.
 *
 * @property subject class of data
 * @property queue queue name
 * @property consumerScope custom description of microservice scope
 * @property routingKey the routing key to use for the binding (equals to [queue] by default)
 * @property exchange exchange declaration
 * @property arguments other properties (construction arguments) for the queue (empty by default)
 * @property exclusive `true` if we are declaring an exclusive queue  (`false` by default)
 */
data class QueueDeclaration(
    val subject: KClass<out Any>,
    val queue: String,
    val consumerScope: ConsumerScope,
    val routingKey: String = queue,
    val exchange: ExchangeDeclaration?,
    val arguments: Map<String, Any> = emptyMap(),
    val exclusive: Boolean = false,
)


/**
 *  Microservice scope description.
 *
 * @property name scope name
 */
interface ConsumerScope {
    val name: String
}


internal fun Channel.declareQueues(declarations: List<QueueDeclaration>) {

    queueDeclarationByClass = declarations
        .onEach {

            queueDeclare(
                /* queue = */ it.queue,
                /* durable = */ !it.exclusive,
                /* exclusive = */ it.exclusive,
                /* autoDelete = */ false,
                /* arguments = */ it.arguments + if (!it.exclusive) mapOf("x-queue-type" to "quorum") else emptyMap()
            )

            if (it.exchange != null)
                queueBind(
                    /* queue = */ it.queue,
                    /* exchange = */ it.exchange.fullName,
                    /* routingKey = */ it.routingKey
                )
        }
        .associate {
            it.subject to InternalQueueDeclaration(
                queue = it.queue,
                consumerScope = it.consumerScope,
                routingKey = it.routingKey,
                exchange = it.exchange?.fullName ?: "",
            )
        }
}

internal fun <T : Any> declaration(clazz: KClass<T>) = queueDeclarationByClass[clazz] ?: error("Queue declaration for class $clazz is not found")
private lateinit var queueDeclarationByClass: Map<KClass<out Any>, InternalQueueDeclaration>

internal data class InternalQueueDeclaration(
    val queue: String,
    val consumerScope: ConsumerScope,
    val routingKey: String,
    val exchange: String,
)
