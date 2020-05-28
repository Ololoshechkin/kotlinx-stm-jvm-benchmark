package stm.benchmarks.scala

import java.util.concurrent.Callable

class BankAccount(initial: Int, timestamp: Int) {
    var balance = refOf(initial)
    var lastUpdate = refOf(timestamp)
}

fun BankAccount.adjustBy(amount: Int, timestamp: Int) = scala.concurrent.stm.japi.STM.atomic(Callable {
    if (balance.get(getContext()) + amount >= 0) {
        balance.set(balance.get(getContext()) + amount, getContext())
        lastUpdate.set(timestamp, getContext())
        true
    } else false
})

fun BankAccount.transferTo(other: BankAccount, amount: Int, timestamp: Int) =
    scala.concurrent.stm.japi.STM.atomic(Callable {
        adjustBy(-amount, timestamp) && other.adjustBy(+amount, timestamp)
    })