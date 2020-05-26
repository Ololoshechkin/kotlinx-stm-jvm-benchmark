package m

import kotlinx.stm.AtomicFunction
import kotlinx.stm.SharedMutable

import kotlinx.stm.runAtomically

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
fun a(u: User) {
    println(u)

    "Vadim".splitToSequence("").forEach {
        u.lastName = it
        println("${u.firstName} ${u.lastName}")
    }
}

fun g() {
    val u = User("Vadim", "Briliantov")

    runAtomically {
        val tmp = u.firstName
        u.firstName = u.lastName
        u.lastName = tmp

        a(u)
    }

    println(u.f())

    u.firstName = "Ololoshechkin"
    println(u.firstName)

}

@SharedMutable
object SM {
    var x: Int = 21
}

fun g2(): Boolean {
    SM.x = 42
    return SM.x == 42
}

fun main() {
    g()
    g2()
}