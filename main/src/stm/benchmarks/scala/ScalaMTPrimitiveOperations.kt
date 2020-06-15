package stm.benchmarks.scala

import stm.benchmarks.testData.Operation

class ScalaVariable(initial: String) {
    var value = refOf(initial)
}

fun ScalaVariable.assign(other: ScalaVariable) {
    value.set(other.value.get(getContext()), getContext())
}

fun ScalaVariable.swap(other: ScalaVariable) {
    val res = other.value.get(getContext())
    other.value.set(value.get(getContext()), getContext())
    value.set(res, getContext())
}

fun ScalaVariable.write(newValue: String) {
    value.set(newValue, getContext())
}

fun ScalaVariable.read(op: Operation.Read) {
    op.result = value.get(getContext())
}