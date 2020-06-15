package stm.benchmarks.plugin

import kotlinx.stm.AtomicFunction
import kotlinx.stm.SharedMutable
import stm.benchmarks.testData.Operation

@SharedMutable
class PluginVariable(var value: String)

@AtomicFunction
fun PluginVariable.assign(other: PluginVariable) {
    value = other.value
}

@AtomicFunction
fun PluginVariable.swap(other: PluginVariable) {
    val res = other.value
    other.value = value
    value = res
}

@AtomicFunction
fun PluginVariable.write(newValue: String) {
    value = newValue
}

@AtomicFunction
fun PluginVariable.read(op: Operation.Read) {
    op.result = value
}