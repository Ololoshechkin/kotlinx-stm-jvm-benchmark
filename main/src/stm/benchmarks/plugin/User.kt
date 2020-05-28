package stm.benchmarks.plugin

import kotlinx.stm.AtomicFunction
import kotlinx.stm.SharedMutable
import kotlinx.stm.runAtomically

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