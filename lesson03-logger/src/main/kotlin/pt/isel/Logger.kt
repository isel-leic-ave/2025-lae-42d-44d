package pt.isel

import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * Writes to the output info about the type of the target
 * and the values of its properties.
 * E.g.
 *  Artist
 *    - name = Nirvana
 *    - kind = Rock
 *    - country = Country
 *      - name = USA
 *      - language = en
 */
fun Appendable.log(target: Any, indentation: Int = 0) {
    this.appendLine(target::class.simpleName)
    target::class
        .memberProperties
        .forEach { prop ->
            indent(indentation)
            append("- ${prop.name} = ")
            appendPropValue(prop, target, indentation)
        }
}

fun Appendable.indent(indentation: Int) {
    for (i in 0..indentation) {
        append(" ")
    }
}

fun Appendable.appendPropValue(prop: KProperty<*>, target: Any, indentation: Int) {
    prop.isAccessible = true
    val propValue: Any? = prop.call(target)
    if(propValue == null) {
        appendLine("null")
    }
    else if(isPrimitive(propValue)) {
        appendLine(propValue.toString())
    } else {
        this.log(propValue, indentation + 2)
    }
}

fun isPrimitive(propValue: Any): Boolean {
    return propValue is String
        || propValue is Int
        || propValue is Long
        || propValue is Float
        || propValue is Double
        || propValue is Boolean
        || propValue is Short
}
