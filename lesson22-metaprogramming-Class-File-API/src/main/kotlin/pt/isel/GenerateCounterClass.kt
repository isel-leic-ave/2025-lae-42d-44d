package pt.isel

import java.io.File
import java.lang.classfile.ClassFile
import java.lang.classfile.ClassFile.ACC_FINAL
import java.lang.classfile.ClassFile.ACC_PRIVATE
import java.lang.classfile.ClassFile.ACC_PUBLIC
import java.lang.classfile.Interfaces
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs.CD_Object
import java.lang.constant.ConstantDescs.CD_int
import java.lang.constant.ConstantDescs.CD_void
import java.lang.constant.ConstantDescs.INIT_NAME
import java.lang.constant.ConstantDescs.MTD_void
import java.lang.constant.MethodTypeDesc

private val resourcePath =
    Unit::class.java
        .getResource("/")
        ?.toURI()
        ?.path


private val className = "pt.isel.Counter"
private val classDesc = ClassDesc.of(className)

fun main() {
    buildAndSaveCounterClass()
    Unit::class.java.classLoader
        .loadClass(className)
        .also { counterClass ->
            val counter = counterClass.kotlin
                .constructors
                .first()
                .call(19) as Sum
            counter.add(7).also { println(it) }
        }
}

/**
 * Dynamically generates a class similar to:
 * <pre>
 * package pt.isel;
 *
 * class Counter implements Sum {
 *     private final int nr;
 *
 *     public Counter(int nr) {
 *         super();
 *         this.nr = nr;
 *     }
 *     public int add(int other) {
 *         return this.nr + other;
 *     }
 * }
 * </pre>
 */
private fun buildAndSaveCounterClass() {
    val bytes: ByteArray =
        ClassFile
            .of()
            .build(ClassDesc.of(className)) { clb ->
                clb.withInterfaces(Interfaces.ofSymbols(ClassDesc.of(Sum::class.qualifiedName)).interfaces())
                clb.withField("nr", CD_int, ACC_PRIVATE or ACC_FINAL)
                clb.withMethod(INIT_NAME, MethodTypeDesc.of(CD_void, CD_int), ACC_PUBLIC) { mb ->
                    mb.withCode { cb ->
                        cb
                            .aload(0)
                            .invokespecial(CD_Object, INIT_NAME, MTD_void)
                            .aload(0) // this
                            .iload(1) // other
                            .putfield(classDesc, "nr", CD_int)
                            .return_()
                        //   ALOAD 0
                        //   INVOKESPECIAL java/lang/Object.<init> ()V
                        //   ALOAD 0
                        //   ILOAD 1
                        //   PUTFIELD pt/isel/CounterBaseline.nr : I
                        //   RETURN
                    }
                }
                clb.withMethod("add", MethodTypeDesc.of(CD_int, CD_int), ACC_PUBLIC) { mb ->
                    mb.withCode { cb ->
                        // return this.nr + other;
                        //    ALOAD 0
                        //    GETFIELD pt/isel/CounterBaseline.nr : I
                        //    ILOAD 1
                        //    IADD
                        //    IRETURN
                        cb
                            .aload(0)
                            .getfield(classDesc, "nr", CD_int)
                            .iload(1)
                            .iadd()
                            .ireturn()
                    }
                }
            }
    File(resourcePath, "${className.replace(".", "/")}.class")
        .also { it.parentFile.mkdirs() }
        .writeBytes(bytes)
}
