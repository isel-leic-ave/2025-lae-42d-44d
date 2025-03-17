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

    private val props: Map<KProperty<*>?, KParameter> = constructor
        .parameters
        .associateBy { ctorParam ->
            srcType
                .memberProperties
                .firstOrNull { matchProps(it, ctorParam) }
        }
        .filter { (srcProp, _) -> srcProp != null }


    fun mapFrom(src: T): R = props
        .map { (srcProp, ctorParam) ->
            require(srcProp != null)
            val propValue = srcProp.call(src)
            if (srcProp.returnType != ctorParam.type) {
                ctorParam to mapPropValue(propValue, srcProp, ctorParam)
            } else ctorParam to propValue
        }
        .toMap()
        .let { propValues ->
            constructor.callBy(propValues)
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

    private fun mapPropValue(propValue: Any?, prop: KProperty<*>, ctorParam: KParameter): Any? {
        if (propValue == null) {
            return null
        }
        val propClassifier = prop.returnType.classifier
        val paramClassifier = ctorParam.type.classifier
        require(propClassifier is KClass<*>)
        require(paramClassifier is KClass<*>)
        return NaiveMapper(propClassifier as KClass<Any>, paramClassifier).mapFrom(propValue)
    }
}
