package stm.benchmarks.scala

import java.util.concurrent.Callable

class ScalaBankAccount(initial: Int, timestamp: Int) {
    var balance = refOf(initial)
    var lastUpdate = refOf(timestamp)
}

fun ScalaBankAccount.transferTo(other: ScalaBankAccount, amount: Int, timestamp: Int) =
    scala.concurrent.stm.japi.STM.atomic(Callable {
        val ctx = getContext()

        if (balance.get(ctx) >= amount) {
            balance.set(balance.get(ctx) - amount, ctx)
            other.balance.set(other.balance.get(ctx) + amount, ctx)

            lastUpdate.set(timestamp, ctx)
            other.lastUpdate.set(timestamp, ctx)
            true
        } else false
    })