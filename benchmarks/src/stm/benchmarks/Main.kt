package stm.benchmarks

import kotlinx.stm.AtomicFunction
import kotlinx.stm.SharedMutable

import kotlinx.stm.runAtomically
import org.openjdk.jmh.annotations.*
import java.util.concurrent.*
import kotlin.math.*

@SharedMutable
class User(fname: String, lname: String) {
    var firstName: String = fname
    var lastName: String = lname
    fun f(): String {
        val x = firstName.hashCode()
        val y = firstName.hashCode()
        return "$x:$y"
    }

    override fun toString(): String {
        return "atomic(2) user is: $firstName $lastName"
    }
}

@AtomicFunction
fun a(u: User) = "Vadim"
    .splitToSequence("")
    .map {
        u.lastName = it
        "${u.firstName} ${u.lastName}"
    }
    .joinToString(separator = ", ")

fun g(): String {
    val u = User("Vadim", "Briliantov")

    var res = ""

    res += runAtomically {
        val tmp = u.firstName
        u.firstName = u.lastName
        u.lastName = tmp

        a(u)
    }

    res += u.f()

    u.firstName = "Ololoshechkin"
    res += u.firstName

    return res
}

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
class TestBenchmark {
    private var user = User("", "")

    @Setup
    fun setUp() {
        user = User("Vadim", "Briliantov")
    }

//    @Benchmark
//    fun fBenchmark() = user.f()
//
//    @Benchmark
//    fun toStringBenchmark() = user.toString()
//
//    @Benchmark
//    fun hashCodeBenchmark() = user.hashCode()
//
    @Benchmark
    fun gBenchmark() = g()

//    @Benchmark
//    fun atomicChangeBenchmark() {
//        runAtomically {
//            val tmp = user.firstName
//            user.firstName = user.lastName
//            user.lastName = tmp
//        }
//    }
}