package pt.isel

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.memberProperties

fun <T : Any, R : Any> findConstructor(
    srcType: KClass<T>,
    destType: KClass<R>,
): KFunction<R> =
    destType
        .constructors
        .firstOrNull {
            it.parameters.all { ctorParam ->
                srcType.memberProperties.any { srcProp ->
                    val match = matchProps(srcProp, ctorParam)
                    match
                }
            }
        } ?: throw Exception("No valid constructor found for ${destType.qualifiedName}")

fun <T : Any, R> findMatchingProperties(
    srcType: KClass<T>,
    constructor: KFunction<R>,
): List<PropInfo> =
    constructor
        .parameters
        .mapNotNull { ctorParam ->
            val srcProp =
                srcType
                    .memberProperties
                    .firstOrNull { matchProps(it, ctorParam) }
            if (srcProp == null) {
                null
            } else if (srcProp.returnType != ctorParam.type) {
                PropInfo(srcProp, ctorParam, buildMapperPropValue(srcProp, ctorParam))
            } else {
                PropInfo(srcProp, ctorParam) { it }
            }
        }

/**
 * Cache of NaiveMapper instances keyed by the domain class.
 * Prevents repeated instantiation.
 */
private val mappers = mutableMapOf<Pair<KClass<*>, KClass<*>>, NaiveMapper<*, *>>()

/**
 * Loads a dynamic mapper instance for the given domain class using its Java `Class`
 * representation. Delegates to the Kotlin version of `loadDynamicMapper`.
 */
fun <T : Any, R : Any> loadNaiveMapper(
    srcType: Class<T>,
    destType: Class<R>,
) = loadNaiveMapper(srcType.kotlin, destType.kotlin)

/**
 * Loads or creates a dynamic mapper instance for the given domain class.
 * If not already cached, it generates the class using a builder, loads it, and instantiates it.
 */
fun <T : Any, R : Any> loadNaiveMapper(
    srcType: KClass<T>,
    destType: KClass<R>,
) = mappers.getOrPut(srcType to destType) {
    NaiveMapper(srcType, destType)
} as NaiveMapper<T, R>

data class PropInfo(
    val srcProp: KProperty<*>,
    val ctorProp: KParameter,
    val mapPropValue: (Any?) -> Any?,
)

class NaiveMapper<T : Any, R : Any>(
    val srcType: KClass<T>,
    val destType: KClass<R>,
) {
    /*
     * Select the first constructor with All arguments
     * with Any corresponding property in srcType
     * or the argument being optional
     */
    private val constructor: KFunction<R> = findConstructor(srcType, destType)

    private val props: List<PropInfo> = findMatchingProperties(srcType, constructor)

    fun mapFrom(src: T): R =
        props
            .map { (srcProp, ctorParam, mapPropValue) ->
                require(srcProp != null)
                val propValue = srcProp.call(src)
                ctorParam to mapPropValue(propValue)
            }.toMap()
            .let { propValues ->
                constructor.callBy(propValues)
            }
}

private fun matchProps(
    prop: KProperty<*>,
    ctorParam: KParameter,
): Boolean {
    if (prop.returnType != ctorParam.type) {
        if (!areBothNonPrimitive(prop, ctorParam)) {
            return false
        }
    }
    if (prop.name == ctorParam.name) {
        return true
    }
    val annot =
        prop
            .findAnnotations(Match::class)
            .firstOrNull()
    if (annot == null) {
        return false
    }
    return annot.name == ctorParam.name
}

private fun areBothNonPrimitive(
    prop: KProperty<*>,
    ctorParam: KParameter,
): Boolean {
    val propClassifier = prop.returnType.classifier
    val paramClassifier = ctorParam.type.classifier
    if (propClassifier !is KClass<*> || paramClassifier !is KClass<*>) {
        return false
    }
    return !propClassifier.java.isPrimitive && !paramClassifier.java.isPrimitive
}

/**
 * Instead of transforming the property value,
 * returns a new function that transforms the property value.
 */
private fun buildMapperPropValue(
    prop: KProperty<*>,
    ctorParam: KParameter,
): (Any?) -> Any? {
    val propClassifier = prop.returnType.classifier
    val paramClassifier = ctorParam.type.classifier
    require(propClassifier is KClass<*>)
    require(paramClassifier is KClass<*>)
    // Avoid to instantiate redundant NaiveMapper objects.
    // e.g. Keep in Map<Pair<KClass, KClass>, NaiveMapper>
    val mapper = loadNaiveMapper(propClassifier as KClass<Any>, paramClassifier)
    return { propValue ->
        if (propValue == null) {
            null
        } else {
            mapper.mapFrom(propValue)
        }
    }
}
