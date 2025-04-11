package pt.isel

import java.io.File
import java.lang.classfile.ClassFile
import java.lang.classfile.ClassFile.ACC_PUBLIC
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs.CD_Object
import java.lang.constant.ConstantDescs.CD_int
import java.lang.constant.ConstantDescs.INIT_NAME
import java.lang.constant.ConstantDescs.MTD_void
import java.lang.constant.MethodTypeDesc
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.functions

private val resourcePath =
    Unit::class.java
        .getResource("/")
        ?.toURI()
        ?.path

fun main() {
    generateAndSaveBarClass()
    Unit::class.java.classLoader
        .loadClass("pt.isel.Bar")
        .also { barClass ->
            val bar = barClass.kotlin.createInstance()
            val foo = barClass.kotlin.functions.first { it.name == "foo" }
            foo.call(bar).also { println(it) }
        }
}

private fun generateAndSaveBarClass() {
    val bytes: ByteArray =
        ClassFile
            .of()
            .build(ClassDesc.of("pt.isel.Bar")) { clb ->
                clb.withMethod(INIT_NAME, MTD_void, ACC_PUBLIC) { mb ->
                    //  this.super() <=>
                    //  ALOAD 0
                    //  INVOKESPECIAL java/lang/Object.<init> ()V
                    //  RETURN
                    mb.withCode { cb ->
                        cb
                            .aload(0)
                            .invokespecial(CD_Object, INIT_NAME, MTD_void)
                            .return_()
                    }
                }
                clb.withMethod("foo", MethodTypeDesc.of(CD_int), ACC_PUBLIC) { mb ->
                    // SIPUSH 6754
                    // IRETURN
                    mb.withCode { cb ->
                        cb
                            .sipush(6754)
                            .ireturn()
                    }
                }
            }
    File(resourcePath, "pt/isel/Bar.class")
        .also { it.parentFile.mkdirs() }
        .writeBytes(bytes)
}
