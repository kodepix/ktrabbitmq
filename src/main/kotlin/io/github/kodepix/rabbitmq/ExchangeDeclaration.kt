@file:Suppress("ktlint:standard:comment-wrapping", "InconsistentCommentForJavaParameter")

package io.github.kodepix.rabbitmq

import com.rabbitmq.client.*
import kotlin.reflect.*


/**
 * Exchange declaration configuration.
 *
 * @property subject class of data
 * @property name exchange name
 * @property type exchange type
 */
interface ExchangeDeclaration {
    val subject: KClass<out Any>
    val name: String
    val type: BuiltinExchangeType
}


internal fun Channel.declareExchanges(declarations: List<ExchangeDeclaration>) {

    exchangeByClass += declarations
        .onEach { exchangeDeclare(it.fullName, it.type, /* durable = */ true) }
        .associate { it.subject to it.fullName }
}

internal val ExchangeDeclaration.fullName get() = "$name.${type.type}"

internal fun exchange(clazz: KClass<out Any>) = exchangeByClass[clazz] ?: error("Exchange declaration for class $clazz is not found")
private var exchangeByClass: Map<KClass<out Any>, String> = emptyMap()
