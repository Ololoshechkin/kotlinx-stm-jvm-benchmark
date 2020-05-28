package stm.benchmarks.plugin

import kotlinx.stm.SharedMutable
import kotlinx.stm.runAtomically

@SharedMutable
class BankAccount(initial: Int, timestamp: Int) {
    var balance: Int = initial
    var lastUpdate: Int = timestamp
}

fun BankAccount.adjustBy(amount: Int, timestamp: Int) = runAtomically {
    if (balance + amount >= 0) {
        balance += amount
        lastUpdate = timestamp
        true
    } else false
}

fun BankAccount.transferTo(other: BankAccount, amount: Int, timestamp: Int) = runAtomically {
    adjustBy(-amount, timestamp) && other.adjustBy(+amount, timestamp)
}