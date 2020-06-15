package stm.benchmarks.scala

import org.openjdk.jmh.annotations.*
import stm.benchmarks.common.multiThreadedRun
import stm.benchmarks.testData.*
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

fun main() {
    println(g())
}

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 3)
@Measurement(iterations = 7, time = 2, timeUnit = TimeUnit.SECONDS)
open class StmPluginBenchmark {
//    private var user = User("", "")
//
//    @Setup
//    fun setUp() {
//        user = User("Vadim", "Briliantov")
//    }
//
//    @Benchmark
//    fun singleReadBenchmark() = scala.concurrent.stm.japi.STM.atomic(Callable {
//        user.firstName.get(getContext())
//    })
//
//    @Benchmark
//    fun doubleReadBenchmark(b: Blackhole) = scala.concurrent.stm.japi.STM.atomic(Callable {
//        b.consume(user.firstName.get(getContext()))
//        b.consume(user.lastName.get(getContext()))
//    })
//
//    @Benchmark
//    fun singleWriteBenchmark() = scala.concurrent.stm.japi.STM.atomic(Callable {
//        user.firstName.set("Vad", getContext())
//    })
//
//    @Benchmark
//    fun doubleWriteBenchmark() = scala.concurrent.stm.japi.STM.atomic(Callable {
//        user.firstName.set("Vad", getContext())
//        user.firstName.set("Bril", getContext())
//    })
//
//    private fun swapNames() = scala.concurrent.stm.japi.STM.atomic(Callable {
//        val tmp = user.firstName.get(getContext())
//        user.firstName.set(user.lastName.get(getContext()), getContext())
//        user.lastName.set(tmp, getContext())
//    })
//
//    @Benchmark
//    fun swapBenchmark() = swapNames()
//
//    @Benchmark
//    fun nestedTransactionBenchmark() = scala.concurrent.stm.japi.STM.atomic(Callable {
//        swapNames()
//        scala.concurrent.stm.japi.STM.atomic(Callable {
//            swapNames()
//        })
//    })
//
//    @Benchmark
//    fun gBenchmark() = g()
//
//    @Benchmark
//    fun multiThreadedAccountBenchmark() {
//        var currentTimestamp = 0
//
//        val accounts = FORTUNES.map { ScalaBankAccount(initial = it, timestamp = currentTimestamp) }
//        multiThreadedRun(NUMBER_OF_THREADS_BANK, TRANSFERS) { (from, to, amount) ->
//            currentTimestamp += 1
//            accounts[from].transferTo(accounts[to], amount, currentTimestamp)
//        }
//    }

    @Benchmark
    fun multiThreadedPrimitiveBenchmark() {
        val variables = VARIABLES.map { ScalaVariable(initial = it) }

        multiThreadedRun(NUMBER_OF_THREADS_PRIMITIVE, PRIMITIVE_OPERATIONS) { bucket ->
            scala.concurrent.stm.japi.STM.atomic(Callable {
                bucket.forEach {
                    when (it) {
                        is Operation.Assignment -> variables[it.toId].assign(variables[it.fromId])
                        is Operation.Swap -> variables[it.firstId].swap(variables[it.secondId])
                        is Operation.Write -> variables[it.varId].write(it.value)
                        is Operation.Read -> variables[it.varId].read(it)
                    }
                }
            })
        }
    }

    @Benchmark
    fun multiThreadedMapBenchmark() {
        val map = ScalaMap(hashMapOf(*KEYS.zip(VALUES).toTypedArray()))

        multiThreadedRun(NUMBER_OF_THREADS_MAP, MAP_QUERIES) { q ->
            processMapQuery(map, q)
        }
    }
}

