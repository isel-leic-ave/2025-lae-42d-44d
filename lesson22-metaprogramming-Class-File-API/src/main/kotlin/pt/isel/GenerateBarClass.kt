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

}

private fun generateAndSaveBarClass() {

}
