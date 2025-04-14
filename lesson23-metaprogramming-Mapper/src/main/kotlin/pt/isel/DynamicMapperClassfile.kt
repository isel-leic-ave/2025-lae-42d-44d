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
    val className = "$packageName.${src.simpleName}2${dest.simpleName}"
    buildMapperByteArray(className, src, dest)
    return rootLoader
        .loadClass("$className")
        .kotlin
}

fun <T : Any, R : Any> buildMapperByteArray(
    className: String,
    src: KClass<T>,
    dest: KClass<R>,
) {
    /*
     * Select the first constructor with All arguments
     * with Any corresponding property in srcType
     * or the argument being optional
     */
    val constructor: KFunction<R> = findConstructor(src, dest)
    val props: List<PropInfo> = findMatchingProperties(src, constructor)
    val bytes: ByteArray =
        ClassFile
            .of()
            .build(ClassDesc.of(className)) { clb ->
                clb.withInterfaces(Interfaces.ofSymbols(ClassDesc.of(Mapper::class.qualifiedName)).interfaces())
                clb.withMethod(INIT_NAME, MethodTypeDesc.of(CD_void), ACC_PUBLIC) { mb ->
                    mb.withCode { cb ->
                        cb
                            .aload(0)
                            .invokespecial(CD_Object, INIT_NAME, MTD_void)
                            .return_()
                    }
                }
                clb.withMethod("mapFrom", MethodTypeDesc.of(CD_Object, CD_Object), ACC_PUBLIC) { mb ->
                    mb.withCode { cb -> cb.withMapFrom(className, constructor, props, src, dest) }
                }
            }
    File(root, "${className.replace(".", "/")}.class")
        .also { it.parentFile.mkdirs() }
        .writeBytes(bytes)
}

//    ALOAD 1
//    CHECKCAST pt/isel/PersonDto
//    ASTORE 2
//    NEW pt/isel/Person
//    DUP
//    ALOAD 2
//    INVOKEVIRTUAL pt/isel/PersonDto.getName ()Ljava/lang/String;
//    ALOAD 2
//    INVOKEVIRTUAL pt/isel/PersonDto.getFrom ()Ljava/lang/String;
//    INVOKESPECIAL pt/isel/Person.<init> (Ljava/lang/String;Ljava/lang/String;)V
//    ARETURN
fun <T : Any, R : Any> CodeBuilder.withMapFrom(
    className: String,
    constructor: KFunction<R>,
    props: List<PropInfo>,
    src: KClass<T>,
    dest: KClass<R>,
) {
    // val dto = src as PersonDto
    aload(1)
    checkcast(src.descriptor())
    astore(2)

    // new Person
    new_(dest.descriptor())
    dup()

    // For each property of source load its value on the Stack
    props.forEach { (srcProp, destParam, _) ->
        if (!srcProp.returnType
                .toKClass()
                .java.isPrimitive &&
            srcProp.returnType != typeOf<String>()
        ) {
            // LDC Lpt/isel/State;.class
            // LDC Lpt/isel/Country;.class
            // INVOKESTATIC pt/isel/ArtistSpotify2ArtistBaseline.loadMapper (Ljava/lang/Class;Ljava/lang/Class;)Lpt/isel/Mapper;
            ldc(constantPool().classEntry(srcProp.returnType.descriptor()))
            ldc(constantPool().classEntry(destParam.type.descriptor()))
            invokestatic(
                ClassDesc.of("pt.isel.DynamicMapperClassfileKt"),
                "loadDynamicMapper",
                MethodTypeDesc.of(Mapper::class.descriptor(), Class::class.descriptor(), Class::class.descriptor()),
            )
        }
        // e.g.
        //    ALOAD 2
        //    INVOKEVIRTUAL pt/isel/PersonDto.getFrom ()Ljava/lang/String;
        aload(2)
        invokevirtual(
            src.descriptor(), // Owner -> The class that owns the property e.g. PersonDto
            srcProp.javaGetter?.name, // Method Name -> e.g. getFrom
            MethodTypeDesc.of(srcProp.returnType.descriptor()),
        )
        if (!srcProp.returnType
                .toKClass()
                .java.isPrimitive &&
            srcProp.returnType != typeOf<String>()
        ) {
            invokeinterface(
                Mapper::class.descriptor(),
                "mapFrom",
                MethodTypeDesc.of(CD_Object, CD_Object),
            )
            checkcast(destParam.type.descriptor())
            // INVOKEINTERFACE pt/isel/Mapper.mapFrom (Ljava/lang/Object;)Ljava/lang/Object; (itf)
            // CHECKCAST pt/isel/Country
        }
    }

    // invokespecial <init>()
    invokespecial(
        dest.descriptor(),
        INIT_NAME,
        MethodTypeDesc.of(CD_void, constructor.parameters.map { it.type.descriptor() }),
    )
    areturn()
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
