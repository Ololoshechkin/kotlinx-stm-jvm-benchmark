package stm.benchmarks.plugin

import kotlinx.stm.SharedMutable
import kotlinx.stm.runAtomically

@SharedMutable
class PluginBankAccount(initial: Int, timestamp: Int) {
    var balance: Int = initial
    var lastUpdate: Int = timestamp
}

fun PluginBankAccount.transferTo(other: PluginBankAccount, amount: Int, timestamp: Int) = runAtomically {
    if (balance >= amount) {
        balance -= amount
        other.balance += amount

        lastUpdate = timestamp
        other.lastUpdate = timestamp
        true
    } else false
}