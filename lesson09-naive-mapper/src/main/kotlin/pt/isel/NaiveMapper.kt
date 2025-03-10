package pt.isel

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.memberProperties

/**
 *
 */
class NaiveMapper<T : Any, R : Any>(
    val srcType: KClass<T>,
    val destType: KClass<R>) {
    val props = srcType
        .memberProperties
        .associateWith { prop ->
            destType
                .memberProperties
                .filter { it is KMutableProperty<*> }
                .map { it as KMutableProperty<*> }
                .firstOrNull {
                    matchProps(prop, it) }
        }
        .filter { it.value != null }

    fun mapFrom(src: T) : R {
        val target: R = destType.createInstance()
        /*
         * Copy the value of each property from src object
         * to the target object.
         */
        props.forEach({ (srcProp, destProp) ->
            destProp?.setter?.call(target, srcProp.call(src))
        })
        return target
    }
}

fun matchProps(prop: KProperty<*>, destProp: KProperty<*>): Boolean {
    if (prop.returnType != destProp.returnType) {
        return false
    }
    if (prop.name == destProp.name) {
        return true
    }
    val annot = prop
        .findAnnotations(Match::class)
        .firstOrNull()
    if (annot == null) {
        return false
    }
    return annot.name == destProp.name
}
