package pt.isel

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.memberProperties


fun <T : Any> Any.mapTo(destType: KClass<T>): T {
    // 1. Create an instance of destType
    val target: T = destType.createInstance()

    // 2. Look for matching properties with same NAME and TYPE
    destType
        .memberProperties
        .filter { it is KMutableProperty<*> }
        .map { it as KMutableProperty<*> }
        .forEach { destProp ->
            this::class
                .memberProperties
                // .firstOrNull() { srcProp.returnType == destProp.returnType && srcProp.name == destProp.name } // 1st version
                .firstOrNull() { matchPropertye(it, destProp) } // 2nd version to match props with different name
                ?.let { srcProp ->
                    // 3. Copy the value of source property to destType instance
                    val srcPropValue = srcProp.call(this)
                    destProp.setter.call(target, srcPropValue)
                }
        }
    // 4. return the instance of destType
    return target
}

fun matchPropertye(srcProp: KProperty<*>, destProp: KMutableProperty<*>): Boolean {
    if(srcProp.returnType != destProp.returnType) {
        return false
    }
    else if(srcProp.name == destProp.name) {
        return true
    }
    return srcProp
        .findAnnotations(Match::class)
        .firstOrNull()
        ?.name == destProp.name
}






