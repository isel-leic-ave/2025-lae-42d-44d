package pt.isel

import java.io.File
import java.io.FileOutputStream
import java.lang.classfile.ClassBuilder
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
 
}
