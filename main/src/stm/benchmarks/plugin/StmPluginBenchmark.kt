package stm.benchmarks.plugin

import kotlinx.benchmark.Blackhole
import kotlinx.stm.AtomicFunction
import kotlinx.stm.SharedMutable
import kotlinx.stm.findJavaSTM

import kotlinx.stm.runAtomically
import org.openjdk.jmh.annotations.*
import java.util.*
import java.util.concurrent.*
import kotlin.math.*

@SharedMutable
class User(fname: String, lname: String) {
    var firstName: String = fname
    var lastName: String = lname
}

@AtomicFunction
fun a(u: User) =
    "Vadim"
        .splitToSequence("")
        .map {
            u.lastName = it
            "${u.firstName} ${u.lastName}"
        }
        .joinToString(", ")

fun g(): String {
    val u = User("Vadim", "Briliantov")

    var res = ""
    res += runAtomically {
        val tmp = u.firstName
        u.firstName = u.lastName
        u.lastName = tmp

        a(u)
    }

    u.firstName = "Ololoshechkin"
    res += u.firstName
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
    fun singleReadBenchmark() = runAtomically {
        user.firstName
    }

    @Benchmark
    fun doubleReadBenchmark(b: Blackhole) = runAtomically {
        b.consume(user.firstName)
        b.consume(user.lastName)
    }

    @Benchmark
    fun singleWriteBenchmark() = runAtomically {
        user.firstName = "Vad"
    }

    @Benchmark
    fun doubleWriteBenchmark() = runAtomically {
        user.firstName = "Vad"
        user.firstName = "Bril"
    }

    private fun swapNames() = runAtomically {
        val tmp = user.firstName
        user.firstName = user.lastName
        user.lastName = tmp
    }

    @Benchmark
    fun swapBenchmark() = swapNames()

    @Benchmark
    fun nestedTransactionBenchmark() = runAtomically {
        swapNames()
        runAtomically {
            swapNames()
        }
    }

    @Benchmark
    fun gBenchmark() = g()
}
