package stm.benchmarks.scala

import scala.concurrent.stm.Ref
import scala.concurrent.stm.Txn
import scala.concurrent.stm.`Ref$`
import scala.concurrent.stm.`TxnUnknown$`
import scala.reflect.`ManifestFactory$`

inline fun <reified T> refOf(t: T) = `Ref$`.`MODULE$`.apply(t, `ManifestFactory$`.`MODULE$`.Object()) as Ref<T>

fun getContext() = run {
    val ctx = Txn.findCurrent(`TxnUnknown$`.`MODULE$`)
    when {
        ctx.isDefined -> ctx.get()
        else -> null
    }
}