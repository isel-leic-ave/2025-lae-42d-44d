package pt.isel

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.memberProperties

fun <T : Any> Any.mapTo(destType: KClass<T>): T {
    val target: T = destType.createInstance()
    this::class
        .memberProperties
        .forEach { prop ->
            val destProp = destType
                .memberProperties
                .firstOrNull { matchProps(prop, it) }
            // If we have a matched property (same name and type) and mutable
            if(destProp != null && destProp is KMutableProperty<*>) {
                val srcPropValue = prop.call(this)
                destProp.setter.call(target, srcPropValue)
            }
        }
    return target
}

fun matchProps(prop: KProperty<*>, destProp: KProperty<*>): Boolean {
    if(prop.returnType != destProp.returnType) {
        return false
    }
    if(prop.name == destProp.name) {
        return true
    }
    val annot = prop
        .findAnnotations(Match::class)
        .firstOrNull()
    if(annot == null) {
        return false
    }
    return annot.name == destProp.name
}
