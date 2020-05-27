package stm.benchmarks.scala

import kotlinx.benchmark.Blackhole
import scala.concurrent.stm.*
import scala.reflect.*
import java.util.concurrent.Callable

import org.openjdk.jmh.annotations.*
import java.util.concurrent.*
import kotlin.math.*

inline fun <reified T> refOf(t: T) = `Ref$`.`MODULE$`.apply(t, `ManifestFactory$`.`MODULE$`.Object()) as Ref<T>

class User(fname: String, lname: String) {
    var firstName: Ref<String> = refOf(fname)
    var lastName: Ref<String> = refOf(lname)
}

fun getContext() = run {
    val ctx = Txn.findCurrent(`TxnUnknown$`.`MODULE$`)
    when {
        ctx.isDefined -> ctx.get()
        else -> null
    }
}

fun a(u: User) =
    "Vadim"
        .splitToSequence("")
        .map {
            u.lastName.set(it, getContext())
            "${u.firstName.get(getContext())} ${u.lastName.get(getContext())}"
        }
        .joinToString(", ")

fun g(): String {
    val u = User("Vadim", "Briliantov")

    var res = ""
    res += scala.concurrent.stm.japi.STM.atomic(Callable {
        val tmp = u.firstName.get(getContext())
        u.firstName.set(u.lastName.get(getContext()), getContext())
        u.lastName.set(tmp, getContext())

        return@Callable a(u)
    })

    scala.concurrent.stm.japi.STM.atomic(Callable {
        return@Callable  u.firstName.set("Ololoshechkin", getContext())
    })
    res += scala.concurrent.stm.japi.STM.atomic(Callable {
        return@Callable u.firstName.get(getContext())
    })
    return res
}

fun main() {
    println(g())
}

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 3)
@Measurement(iterations = 7, time = 1, timeUnit = TimeUnit.SECONDS)
open class StmPluginBenchmark {
    private var user = User("", "")

    @Setup
    fun setUp() {
        user = User("Vadim", "Briliantov")
    }

    @Benchmark
    fun singleReadBenchmark() = scala.concurrent.stm.japi.STM.atomic(Callable {
        user.firstName.get(getContext())
    })

    @Benchmark
    fun doubleReadBenchmark(b: Blackhole) = scala.concurrent.stm.japi.STM.atomic(Callable {
        b.consume(user.firstName.get(getContext()))
        b.consume(user.lastName.get(getContext()))
    })

    @Benchmark
    fun singleWriteBenchmark() = scala.concurrent.stm.japi.STM.atomic(Callable {
        user.firstName.set("Vad", getContext())
    })

    @Benchmark
    fun doubleWriteBenchmark() = scala.concurrent.stm.japi.STM.atomic(Callable {
        user.firstName.set("Vad", getContext())
        user.firstName.set("Bril", getContext())
    })

    private fun swapNames() = scala.concurrent.stm.japi.STM.atomic(Callable {
        val tmp = user.firstName.get(getContext())
        user.firstName.set(user.lastName.get(getContext()), getContext())
        user.lastName.set(tmp, getContext())
    })

    @Benchmark
    fun swapBenchmark() = swapNames()

    @Benchmark
    fun nestedTransactionBenchmark() = scala.concurrent.stm.japi.STM.atomic(Callable {
        swapNames()
        scala.concurrent.stm.japi.STM.atomic(Callable {
            swapNames()
        })
    })

    @Benchmark
    fun gBenchmark() = g()
}

