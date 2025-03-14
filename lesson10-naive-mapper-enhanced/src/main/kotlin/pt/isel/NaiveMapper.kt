package pt.isel

import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.memberProperties

/**
 *
 */
class NaiveMapper<T : Any, R : Any>(
    val srcType: KClass<T>,
    val destType: KClass<R>) {
    /*
     * Look for the first Constructor with all parameters
     * with any matching property in srcType or parameter being optional
     */
    val destCtor = destType
        .constructors
        .first {
            it.parameters.all { ctorParam ->
                // Parameter matches a property in srcType or isOptional
                srcType.memberProperties.any { prop ->
                    matchProps(prop, ctorParam)
                } || ctorParam.isOptional
            }
        }
    private val props: Map<KProperty<*>, KParameter> = srcType
        .memberProperties
        .associateWith { prop ->
            destCtor
                .parameters
                .first { matchProps(prop, it) }
        }

    fun mapFrom(src: T) = props
        .map({ (srcProp, paramCtor) ->
            val propValue = srcProp.call(src)
            val value = if(srcProp.returnType != paramCtor.type) {
                mapPropValue(propValue, srcProp, paramCtor)
            } else propValue
            paramCtor to value
        })
        .toMap()
        .let { ctorArgs: Map<KParameter, Any?> ->
            destCtor.callBy(ctorArgs)
        }

    private fun mapPropValue(src: Any?, srcProp: KProperty<*>, paramCtor: KParameter): Any?    {
        if(src == null) return null
        val propClassifier = srcProp.returnType.classifier
        val paramClassifier = paramCtor.type.classifier
        check(propClassifier is KClass<*>)
        check(paramClassifier is KClass<*>)
        return NaiveMapper(propClassifier as KClass<Any>, paramClassifier).mapFrom(src)
    }
}

fun matchProps(prop: KProperty<*>, ctorParam: KParameter): Boolean {
    if (prop.returnType != ctorParam.type) {
        return areBothNonPrimitive(prop, ctorParam)
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

fun areBothNonPrimitive(prop: KProperty<*>, ctorParam: KParameter): Boolean {
    val propClassifier = prop.returnType.classifier
    val paramClassifier = ctorParam.type.classifier
    if(propClassifier !is KClass<*> || paramClassifier !is KClass<*>) {
        return false
    }
    return !propClassifier.java.isPrimitive && !paramClassifier.java.isPrimitive
}
