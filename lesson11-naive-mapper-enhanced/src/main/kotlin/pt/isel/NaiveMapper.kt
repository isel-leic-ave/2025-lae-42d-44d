package pt.isel

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.memberProperties

data class PropInfo(
    val srcProp: KProperty<*>,
    val ctorProp: KParameter,
    val mapPropValue: (Any?) -> Any?
)

class NaiveMapper<T : Any, R : Any>(val srcType: KClass<T>, val destType: KClass<R>) {

    /*
     * Select the first constructor with All arguments
     * with Any corresponding property in srcType
     * or the argument being optional
     */
    private val constructor: KFunction<R> = destType
        .constructors
        .firstOrNull {
            it.parameters.all { ctorParam ->
                srcType.memberProperties.any { srcProp ->
                    val match = matchProps(srcProp, ctorParam)
                    match
                }
            }
        } ?: throw Exception("No valid constructor found for ${destType.qualifiedName}")

    private val props: List<PropInfo> = constructor
        .parameters.mapNotNull { ctorParam ->
            val srcProp = srcType
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

    fun mapFrom(src: T): R = props
        .associate { (srcProp, ctorParam, mapPropValue) ->
            val propValue = srcProp.call(src)
            ctorParam to mapPropValue(propValue)
        }
        .let { params: Map<KParameter, Any?> ->
            constructor.callBy(params)
        }

    private fun matchProps(prop: KProperty<*>, ctorParam: KParameter): Boolean {
        if (prop.returnType != ctorParam.type) {
            if (!areBothNonPrimitive(prop, ctorParam))
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

    private fun areBothNonPrimitive(prop: KProperty<*>, ctorParam: KParameter): Boolean {
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
    private fun buildMapperPropValue(prop: KProperty<*>, ctorParam: KParameter): (Any?) -> Any? {
        val propClassifier = prop.returnType.classifier
        val paramClassifier = ctorParam.type.classifier
        require(propClassifier is KClass<*>)
        require(paramClassifier is KClass<*>)
        // !!!!!! Note we should keep the auxiliary mapper in a companion object
        // and avoid to instantiate redundant NaiveMapper objects.
        // e.g. Map<Pair<KClass, KClass>, NaiveMapper>
        val mapper = NaiveMapper(propClassifier as KClass<Any>, paramClassifier)
        return { propValue ->
            if (propValue == null) {
                null
            } else {
                mapper.mapFrom(propValue)
            }
        }
    }
}
