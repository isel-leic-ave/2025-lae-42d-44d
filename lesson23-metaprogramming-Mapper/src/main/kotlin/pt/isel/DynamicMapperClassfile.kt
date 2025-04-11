package pt.isel

import java.io.File
import java.lang.classfile.ClassBuilder
import java.lang.classfile.ClassFile
import java.lang.classfile.ClassFile.ACC_PUBLIC
import java.lang.classfile.CodeBuilder
import java.lang.classfile.Interfaces
import java.lang.classfile.MethodBuilder
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs.CD_Object
import java.lang.constant.ConstantDescs.CD_boolean
import java.lang.constant.ConstantDescs.CD_char
import java.lang.constant.ConstantDescs.CD_double
import java.lang.constant.ConstantDescs.CD_float
import java.lang.constant.ConstantDescs.CD_int
import java.lang.constant.ConstantDescs.CD_long
import java.lang.constant.ConstantDescs.CD_short
import java.lang.constant.ConstantDescs.CD_void
import java.lang.constant.ConstantDescs.INIT_NAME
import java.lang.constant.ConstantDescs.MTD_void
import java.lang.constant.MethodTypeDesc
import java.net.URLClassLoader
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.createInstance
import kotlin.reflect.jvm.javaGetter

private const val packageName = "pt.isel"
private val packageFolder = packageName.replace(".", "/")

private val root =
    Unit::class.java
        .getResource("/")
        ?.toURI()
        ?.path
        ?: "${System.getProperty("user.dir")}/"

/**
 * A new ClassLoader is required when the existing one loads classes from a JAR
 * and its resource path is null. In such cases, we create a ClassLoader that uses
 * the current working directory, as specified by the 'user.dir' system property.
 */
private val rootLoader = URLClassLoader(arrayOf(File(root).toURI().toURL()))

/**
 * Cache of dynamically generated mappers keyed by the domain class.
 * Prevents repeated code generation and loading.
 */
private val mappers = mutableMapOf<Pair<KClass<*>, KClass<*>>, Mapper<*, *>>()

/**
 * Loads a dynamic mapper instance for the given domain class using its Java `Class`
 * representation. Delegates to the Kotlin version of `loadDynamicMapper`.
 */
fun <T : Any, R : Any> loadDynamicMapper(
    srcType: Class<T>,
    destType: Class<R>,
) = loadDynamicMapper(srcType.kotlin, destType.kotlin)

/**
 * Loads or creates a dynamic mapper instance for the given domain class.
 * If not already cached, it generates the class using a builder, loads it, and instantiates it.
 */
fun <T : Any, R : Any> loadDynamicMapper(
    srcType: KClass<T>,
    destType: KClass<R>,
) = mappers.getOrPut(srcType to destType) {
    buildMapperClassfile(srcType, destType)
        .createInstance() as Mapper<*, *>
} as Mapper<T, R>

/**
 * Generates the class file for a mapper based on the structure of the given domain classes.
 * Uses code generation techniques (e.g., Class-File API) to build the repository implementation at runtime.
 *
 * @param src the Kotlin class of the source domain type.
 * @param dest the Kotlin class of the destination domain type.
 * @return the runtime-generated class implementing the repository logic.
 */
private fun <T : Any, R : Any> buildMapperClassfile(
    src: KClass<T>,
    dest: KClass<R>,
): KClass<out Any> {
    val className = "${src.simpleName}2${dest.simpleName}"
    buildMapperByteArray(className, src, dest)
    return rootLoader
        .loadClass("$packageName.$className")
        .kotlin
}

fun <T : Any, R : Any> buildMapperByteArray(
    className: String,
    src: KClass<T>,
    dest: KClass<R>,
) {
}

/**
 * Returns a ClassDesc of the type descriptor of the given KClass.
 */
fun KClass<*>.descriptor(): ClassDesc =
    if (this.java.isPrimitive) {
        when (this) {
            Char::class -> CD_char
            Short::class -> CD_short
            Int::class -> CD_int
            Long::class -> CD_long
            Float::class -> CD_float
            Double::class -> CD_double
            Boolean::class -> CD_boolean
            else -> {
                throw IllegalStateException("No primitive type for ${this.qualifiedName}!")
            }
        }
    } else {
        ClassDesc.of(this.java.name)
    }

/**
 * Returns a ClassDesc of the type descriptor of the given KType.
 */
fun KType.descriptor(): ClassDesc {
    val klass = this.classifier as KClass<*>
    return klass.descriptor()
}

/**
 * Returns the KClass of the type corresponding to the
 * generic argument of the List or to the classifier itself, otherwise.
 */
fun KType.toKClass() =
    if (classifier == List::class) {
        arguments[0]
            .type
            ?.classifier as KClass<*>
    } else {
        classifier as KClass<*>
    }
