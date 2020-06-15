package stm.benchmarks.common

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

inline fun <T> multiThreadedRun(threads: Int, operations: List<T>, crossinline process: (T) -> Unit) {
    val ex = Executors.newFixedThreadPool(threads)
    val countDownLatch = CountDownLatch(operations.size)

    operations.forEach {
        ex.submit {
            process(it)
            countDownLatch.countDown()
        }
    }

    countDownLatch.await()
    ex.awaitTermination(100, TimeUnit.MILLISECONDS)
    ex.shutdown()
}