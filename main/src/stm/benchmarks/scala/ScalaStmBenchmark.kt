package stm.benchmarks.scala

import kotlinx.benchmark.Blackhole
import org.openjdk.jmh.annotations.*
import stm.benchmarks.testData.FORTUNES
import stm.benchmarks.testData.NUMBER_OF_THREADS
import stm.benchmarks.testData.TRANSFERS
import stm.benchmarks.testData.TRANSFER_COUNT
import java.util.concurrent.*

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

    @Benchmark
    fun multiThreadedAccountBenchmark() {
        var currentTimestamp = 0

        val accounts = FORTUNES.map { ScalaBankAccount(initial = it, timestamp = currentTimestamp) }
        val ex = Executors.newFixedThreadPool(NUMBER_OF_THREADS)
        val countDownLatch = CountDownLatch(TRANSFER_COUNT)

        TRANSFERS.forEach { (from, to, amount) ->
            currentTimestamp += 1

            ex.submit {
                accounts[from].transferTo(accounts[to], amount, currentTimestamp)
                countDownLatch.countDown()
            }
        }

        countDownLatch.await()
        ex.awaitTermination(100, TimeUnit.MILLISECONDS)
        ex.shutdown()
    }
}

