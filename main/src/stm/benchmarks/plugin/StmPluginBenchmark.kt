package stm.benchmarks.plugin

import kotlinx.benchmark.Blackhole
import kotlinx.stm.runAtomically
import org.openjdk.jmh.annotations.*
import stm.benchmarks.common.multiThreadedRun
import stm.benchmarks.testData.*
import java.util.concurrent.TimeUnit


fun main() {
    println(g())
}

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 3)
@Measurement(iterations = 7, time = 2, timeUnit = TimeUnit.SECONDS)
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

    @Benchmark
    fun multiThreadedAccountBenchmark() {
        var currentTimestamp = 0
        val accounts = FORTUNES.map { PluginBankAccount(initial = it, timestamp = currentTimestamp) }

        multiThreadedRun(NUMBER_OF_THREADS_BANK, TRANSFERS) { (from, to, amount) ->
            currentTimestamp += 1
            accounts[from].transferTo(accounts[to], amount, currentTimestamp)
        }
    }

    @Benchmark
    fun multiThreadedPrimitiveBenchmark() {
        val variables = VARIABLES.map { PluginVariable(it) }

        multiThreadedRun(NUMBER_OF_THREADS_PRIMITIVE, PRIMITIVE_OPERATIONS) { bucket ->
            runAtomically {
                bucket.forEach {
                    when (it) {
                        is Operation.Assignment -> variables[it.toId].assign(variables[it.fromId])
                        is Operation.Swap -> variables[it.firstId].swap(variables[it.secondId])
                        is Operation.Write -> variables[it.varId].write(it.value)
                        is Operation.Read -> variables[it.varId].read(it)
                    }
                }
            }
        }
    }

    @Benchmark
    fun multiThreadedMapBenchmark() {
        println("multiThreadedMapBenchmark")
        val map = PluginMap(hashMapOf(*KEYS.zip(VALUES).toTypedArray()))
        println("map done")

        multiThreadedRun(NUMBER_OF_THREADS_MAP, MAP_QUERIES) { q ->
            processMapQuery(map, q)
        }
    }
}
