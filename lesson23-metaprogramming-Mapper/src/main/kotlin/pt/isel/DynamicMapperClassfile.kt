package pt.isel

import java.io.File
import java.lang.classfile.ClassFile
import java.lang.classfile.ClassFile.ACC_PUBLIC
import java.lang.classfile.CodeBuilder
import java.lang.classfile.Interfaces
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
import kotlin.reflect.typeOf

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
    val mapperDesc = ClassDesc.of("$packageName.$className")
    val mapperInterfaceDesc = Interfaces.ofSymbols(ClassDesc.of(Mapper::class.qualifiedName)).interfaces()

    val constructor: KFunction<R> = findConstructor(src, dest)
    val props: Map<KProperty<*>, KParameter> =
        findMatchingProperties(src, constructor)
            .associate { (srcProp, destParam, _) -> srcProp to destParam }

    val bytes =
        ClassFile.of().build(mapperDesc) { clb ->
            clb
                .withInterfaces(mapperInterfaceDesc)
                .withMethod(INIT_NAME, MTD_void, ACC_PUBLIC) { mb ->
                    mb.withCode { cb ->
                        cb
                            .aload(0)
                            .invokespecial(CD_Object, INIT_NAME, MTD_void)
                            .return_()
                    }
                }.withMethod(
                    "mapFrom",
                    MethodTypeDesc.of(CD_Object, CD_Object),
                    ACC_PUBLIC,
                ) { mb ->
                    mb.withCode { cob -> cob.emitMapFrom(constructor, props, src, dest) }
                }
        }
    File(root, "$packageFolder/$className.class")
        .also { it.parentFile.mkdirs() }
        .writeBytes(bytes)
}

/**
 * new Person(src.getName(), src.getFrom());
 *
 *
 *     NEW pt/isel/Person
 *     DUP
 *     ALOAD 1
 *     INVOKEVIRTUAL pt/isel/PersonDto.getName ()Ljava/lang/String;
 *     ALOAD 1
 *     INVOKEVIRTUAL pt/isel/PersonDto.getFrom ()Ljava/lang/String;
 *     INVOKESPECIAL pt/isel/Person.<init> (Ljava/lang/String;Ljava/lang/String;)V
 *     ARETURN
 */
fun <T : Any, R : Any> CodeBuilder.emitMapFrom(
    constructor: KFunction<Any>,
    props: Map<KProperty<*>, KParameter>,
    src: KClass<T>,
    dest: KClass<R>,
) {
    aload(1)
    checkcast(src.descriptor())
    astore(2)
    new_(dest.descriptor())
    dup()
    props.forEach { srcProp, destParam -> emitLoadProperty(src, srcProp, destParam) }
    invokespecial(
        dest.descriptor(),
        INIT_NAME,
        MethodTypeDesc.of(
            CD_void,
            constructor.parameters.map { it.type.descriptor() },
        ),
    )
    areturn()
}

/**
 *  ALOAD 2
 *  INVOKEVIRTUAL pt/isel/PersonDto.getFrom ()Ljava/lang/String;
 */
fun <T : Any> CodeBuilder.emitLoadProperty(
    src: KClass<T>,
    srcProp: KProperty<*>,
    destParam: KParameter,
) {
    /**
     * For non-primitive types we need to load an auxiliary Mapper and
     * call the mapFrom()
     */
    if (!destParam.type
            .toKClass()
            .java.isPrimitive &&
        destParam.type != typeOf<String>()
    ) {
        // e.g. loadDynamicMapper(State::class.java, Country::class.java)
        // LDC Lpt/isel/State;.class
        // LDC Lpt/isel/Country;.class
        // INVOKESTATIC pt/isel/DynamicMapperClassfileKt.loadDynamicMapper (Ljava/lang/Class;Ljava/lang/Class;)Lpt/isel/Mapper;
        ldc(constantPool().classEntry(srcProp.returnType.descriptor()))
        ldc(constantPool().classEntry(destParam.type.descriptor()))
        invokestatic(
            ClassDesc.of("pt.isel.DynamicMapperClassfileKt"),
            "loadDynamicMapper",
            MethodTypeDesc.of(
                Mapper::class.descriptor(),
                Class::class.descriptor(),
                Class::class.descriptor(),
            ),
        )
        // Call mapFrom
    }

    // e.g. this.getFrom()
    aload(2)
    invokevirtual(
        src.descriptor(),
        srcProp.javaGetter?.name,
        MethodTypeDesc.of(srcProp.returnType.descriptor()),
    )
    /**
     * For non-primitive types we need to load an auxiliary Mapper and
     * call the mapFrom()
     */
    if (!destParam.type
            .toKClass()
            .java.isPrimitive &&
        destParam.type != typeOf<String>()
    ) {
        // <=> (Country) mapper.mapFrom(state)
        invokeinterface(
            Mapper::class.descriptor(),
            "mapFrom",
            MethodTypeDesc.of(CD_Object, CD_Object),
        )
        checkcast(destParam.type.descriptor())
    }
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
