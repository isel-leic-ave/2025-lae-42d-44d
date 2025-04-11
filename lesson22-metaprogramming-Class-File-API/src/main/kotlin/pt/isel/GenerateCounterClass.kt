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

fun main() {
    buildAndSaveCounterClass()
    Unit::class.java.classLoader
        .loadClass("pt.isel.Counter")
        .also { counterClass ->
            val counter = counterClass.kotlin
                .constructors
                .first()
                .call(7) as Sum
            counter.add(9).also { println(it) }

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
 *         this.nr = nr;
 *     }
 *
 *     public int add(int other) {
 *         return this.nr + other;
 *     }
 * }
 * </pre>
 */
private fun buildAndSaveCounterClass() {
    val className = "pt.isel.Counter"
    val classDesc = ClassDesc.of(className)
    val bytes: ByteArray =
        ClassFile.of().build(ClassDesc.of(className)) { clb ->
            clb.withInterfaces(Interfaces.ofSymbols(ClassDesc.of("pt.isel.Sum")).interfaces())
            clb.withField("nr", CD_int, ACC_PRIVATE or ACC_FINAL)
            clb.withMethod(INIT_NAME, MethodTypeDesc.of(CD_void, CD_int), ACC_PUBLIC) { mb ->
                mb.withCode { cb ->
                    // public Counter(int nr) { super(); this.nr = nr; }
                    cb
                        .aload(0) // load this
                        .invokespecial(CD_Object,INIT_NAME,MTD_void) // call base init
                        .aload(0)
                        .iload(1)
                        .putfield(classDesc, "nr", CD_int) // this.nr = nr
                        .return_()
                    // ALOAD 0
                    // INVOKESPECIAL java/lang/Object.<init> ()V
                    // ALOAD 0
                    // ILOAD 1
                    // PUTFIELD pt/isel/CounterBaseline.nr : I
                    // RETURN
                }
            }
            clb.withMethod("add",MethodTypeDesc.of(CD_int, CD_int),ACC_PUBLIC) { mb ->
                mb.withCode { cb ->
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
    File(resourcePath, "pt/isel/Counter.class")
        .also { it.parentFile.mkdirs() } // Ensure folders are created
        .writeBytes(bytes)
}
