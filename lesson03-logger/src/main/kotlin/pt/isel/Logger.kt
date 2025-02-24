package pt.isel

import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

fun Appendable.log(target: Any, indentation: Int = 0) {
    this.appendLine(target::class.simpleName)
    target::class
        .declaredMemberProperties
        .forEach { prop ->
            indent(indentation)
            append("  - ${prop.name} = ")
            getPropValue(prop, target, indentation)
        }
}
fun Appendable.getPropValue(prop: KProperty<*>, target: Any, indentation: Int) {
    prop.isAccessible = true // TO read PRIVATE properties
    val propValue = prop.call(target)
    if(propValue == null) {
        appendLine("null")
    }
    // else if(prop.returnType.javaClass.isPrimitive || propValue is String) {
    else if(isPrimitive(propValue)) {
        appendLine(propValue.toString())
    } else {
        log(propValue, indentation + 2)
    }
}

fun Appendable.indent(indentation: Int) {
    for (i in 0..indentation) {
        append(" ")
    }
}

fun isPrimitive(value : Any) : Boolean {
    return value is String
        || value is Int
        || value is Long
        || value is Float
}
