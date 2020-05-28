package stm.benchmarks.scala

import scala.concurrent.stm.Ref
import java.util.concurrent.Callable

class User(fname: String, lname: String) {
    var firstName: Ref<String> = refOf(fname)
    var lastName: Ref<String> = refOf(lname)
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