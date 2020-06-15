package stm.benchmarks.testData

import kotlin.random.Random

val NUMBER_OF_THREADS_PRIMITIVE = 2

val VARIABLES_COUNT = 10
val OPERATIONS_IN_BUCKET_COUNT = 10
val BUCKETS_COUNT = 500

private val RANDOM = Random(seed = 239)

private val WORD_LENGTH = 10

private fun randString() = RANDOM.nextBytes(WORD_LENGTH).asSequence().map { it.toChar() }.joinToString()

val VARIABLES = (0 until VARIABLES_COUNT).map { randString() }

sealed class Operation {
    data class Assignment(val fromId: Int, val toId: Int) : Operation()
    data class Swap(val firstId: Int, val secondId: Int) : Operation()
    data class Write(val varId: Int, val value: String) : Operation()
    data class Read(val varId: Int, var result: String? = null) : Operation()
}

val READ_PERCENTAGE = 79

// 79% of operations are read-only
val PRIMITIVE_OPERATIONS = (0 until BUCKETS_COUNT).map {
    (0 until OPERATIONS_IN_BUCKET_COUNT).map {
        when (RANDOM.nextInt(from = 0, until = 100)) {
            in (0 until READ_PERCENTAGE / 3) -> Operation.Assignment(
                fromId = RANDOM.nextInt(from = 0, until = VARIABLES_COUNT),
                toId = RANDOM.nextInt(from = 0, until = VARIABLES_COUNT)
            )
            in (READ_PERCENTAGE / 3 until 2 * READ_PERCENTAGE / 3) -> Operation.Swap(
                firstId = RANDOM.nextInt(from = 0, until = VARIABLES_COUNT),
                secondId = RANDOM.nextInt(from = 0, until = VARIABLES_COUNT)
            )
            in (2 * READ_PERCENTAGE / 3 until READ_PERCENTAGE) -> Operation.Write(
                varId = RANDOM.nextInt(from = 0, until = VARIABLES_COUNT),
                value = randString()
            )
            else -> Operation.Read(
                varId = RANDOM.nextInt(from = 0, until = VARIABLES_COUNT)
            )
        }
    }
}