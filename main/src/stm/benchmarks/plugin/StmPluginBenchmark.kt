package stm.benchmarks.plugin

import kotlinx.benchmark.Blackhole
import kotlinx.stm.runAtomically
import org.openjdk.jmh.annotations.*
import stm.benchmarks.testData.FORTUNES
import stm.benchmarks.testData.NUMBER_OF_THREADS
import stm.benchmarks.testData.TRANSFERS
import stm.benchmarks.testData.TRANSFER_COUNT
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


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

    @Benchmark
    fun multiThreadedAccountBenchmark() {
        var currentTimestamp = 0

        val accounts = FORTUNES.map { BankAccount(initial = it, timestamp = currentTimestamp) }

        val ex = Executors.newFixedThreadPool(NUMBER_OF_THREADS)
        val countDownLatch = CountDownLatch(TRANSFER_COUNT)

        TRANSFERS.forEach { (from, to, ammount) ->
            currentTimestamp += 1

            ex.submit {
                accounts[from].transferTo(accounts[to], ammount, currentTimestamp)
                countDownLatch.countDown()
            }
        }

        countDownLatch.await()
        ex.awaitTermination(1, TimeUnit.SECONDS);
        ex.shutdown();
    }
}
