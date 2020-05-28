package stm.benchmarks.testData

import kotlin.random.Random

val NUMBER_OF_THREADS = 4

val ACCOUNT_NUMBER = 10
val TRANSFER_COUNT = 10

val MIN_FORTUNE = 1000
val MAX_FORTUNE = 100500


val MIN_TRANSFER_AMMOUNT = 10
val MAX_TRANSFER_AMMOUNT = 100


private val RANDOM = Random(seed = 239)

val FORTUNES = (0 until ACCOUNT_NUMBER).map { RANDOM.nextInt(from = MIN_FORTUNE, until = MAX_FORTUNE) }

data class Transfer(val from: Int, val to: Int, val amount: Int)

val TRANSFERS = (0 until TRANSFER_COUNT).map {
    Transfer(
        from = RANDOM.nextInt(from = 0, until = ACCOUNT_NUMBER),
        to = RANDOM.nextInt(from = 0, until = ACCOUNT_NUMBER),
        amount = RANDOM.nextInt(from = MIN_TRANSFER_AMMOUNT, until = MAX_TRANSFER_AMMOUNT)
    )
}