package pt.isel

import java.io.File
import java.io.FileOutputStream
import java.lang.classfile.ClassBuilder
import java.lang.classfile.ClassFile
import java.lang.classfile.ClassFile.ACC_PUBLIC
import java.lang.classfile.CodeBuilder
import java.lang.classfile.MethodBuilder
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs.CD_Object
import java.lang.constant.ConstantDescs.CD_int
import java.lang.constant.ConstantDescs.INIT_NAME
import java.lang.constant.ConstantDescs.MTD_void
import java.lang.constant.MethodTypeDesc
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.functions

private val resourcePath =
    Unit::class.java
        .getResource("/")
        ?.toURI()
        ?.path

fun main() {
    generateAndSaveBarClass()
    Unit::class
        .java
        .classLoader
        .loadClass("pt.isel.Bar")
        .also { barClass ->
            val bar = barClass.kotlin.createInstance()
            val foo = barClass.kotlin.functions.first { it.name == "foo" }
            foo.call(bar).also { println(it) }
        }
}

private fun generateAndSaveBarClass() {
    val className = "pt.isel.Bar"
    val bytes: ByteArray = ClassFile.of().build(ClassDesc.of(className)) { clb ->
        clb.withMethod(INIT_NAME, MTD_void, ACC_PUBLIC) { mb ->
            mb.withCode { cb ->
                cb
                    .aload(0)    // push stack this
                    .invokespecial( // call base constructor
                        CD_Object,
                        INIT_NAME,
                        MTD_void
                    )
                    .return_()
            }
            //  ALOAD 0
            //  INVOKESPECIAL java/lang/Object.<init> ()V
            //  RETURN
        }
        clb.withMethod(
            "foo",
            MethodTypeDesc.of(CD_int),
            ACC_PUBLIC
        ) { mb ->
            mb.withCode { cb ->
                cb
                    .sipush(6574)
                    .ireturn()
            }
            //    SIPUSH 6574
            //    IRETURN

        }
    }
    File(resourcePath, "pt/isel/Bar.class")
        .also { it.parentFile.mkdirs() } // Ensure folders are created
        .writeBytes(bytes)
}
