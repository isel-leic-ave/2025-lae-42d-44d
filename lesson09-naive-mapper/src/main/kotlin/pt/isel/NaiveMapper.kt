package pt.isel

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.memberProperties

class NaiveMapper<T : Any, R : Any>(val srcType: KClass<T>, val destType: KClass<R>) {

    /*
     * Select the first constructor with All arguments
     * with Any corresponding property in srcType
     * or the argument being optional
     */
    val constructor: KFunction<R> = destType
        .constructors
        .first {
            it.parameters.all { ctorParam ->
                srcType.memberProperties.any { srcProp ->
                    matchProps(srcProp, ctorParam)
                } || ctorParam.isOptional
            }
        }

    val props: Map<KProperty<*>, KParameter?> = srcType
        .memberProperties
        .associateWith { prop ->
            constructor
                .parameters
                .firstOrNull { matchProps(prop, it) }
        }
        .filter { (srcProp, ctorParam) -> ctorParam != null }

    fun mapFrom(src: T): R = props
        .map { (srcProp, ctorParam) ->
            require(ctorParam != null)
            ctorParam to srcProp.call(src)
        }
        .toMap()
        .let { propValues ->
            constructor.callBy(propValues)
        }

    private fun matchProps(prop: KProperty<*>, ctorParam: KParameter): Boolean {
        if (prop.returnType != ctorParam.type) {
            return false
        }
        if (prop.name == ctorParam.name) {
            return true
        }
        val annot = prop
            .findAnnotations(Match::class)
            .firstOrNull()
        if (annot == null) {
            return false
        }
        return annot.name == ctorParam.name
    }
}