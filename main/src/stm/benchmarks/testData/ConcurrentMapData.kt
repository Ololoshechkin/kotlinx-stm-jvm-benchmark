package stm.benchmarks.testData

import kotlin.random.Random

val NUMBER_OF_THREADS_MAP = 10

val KEYS_COUNT = 1000
val QUERIES_COUNT = 10000

private val RANDOM = Random(seed = 239)

private val OP_CHAIN_SIZE = 100

private val READ_RATE = 0.7
private val WRITE_RATE = 0.1
private val CHAIN_RATE = 0.1
private val INSERT_RATE = 0.05
private val REMOVE_RATE = 0.05

private fun readCnt(total: Int) = (READ_RATE * total).toInt()
private fun writeCnt(total: Int) = (WRITE_RATE * total).toInt()
private fun chainCnt(total: Int) = (CHAIN_RATE * total).toInt()
private fun insertCnt(total: Int) = (INSERT_RATE * total).toInt()
private fun removeCnt(total: Int) = (REMOVE_RATE * total).toInt()

private val WORD_LENGTH = 10

private fun randString() = RANDOM.nextBytes(WORD_LENGTH).asSequence().map { it.toChar() }.joinToString()

val KEYS = (0 until KEYS_COUNT).map { randString() }
val VALUES = (0 until KEYS_COUNT).map { randString() }


sealed class Query {
    data class Write(val keyId: Int, val value: String) : Query()
    data class Read(val keyId: Int, var result: String? = null) : Query()
    data class Chain(val operations: List<Query>) : Query()
    data class Insert(val key: String, val value: String) : Query()
    data class Remove(val keyId: Int) : Query()
}

fun genQueries(total: Int, withChain: Boolean = true): List<Query> {
    val readQs = (0 until readCnt(total)).map {
        Query.Read(
            keyId = RANDOM.nextInt(from = 0, until = KEYS_COUNT)
        )
    }
    val writeQs = (0 until writeCnt(total)).map {
        Query.Write(
            keyId = RANDOM.nextInt(from = 0, until = KEYS_COUNT),
            value = randString()
        )
    }
    val chainQs = when {
        withChain -> (0 until chainCnt(total)).map {
            Query.Chain(
                operations = genQueries(OP_CHAIN_SIZE, withChain = false)
            )
        }
        else -> listOf<Query>()
    }

    val insertQs = (0 until insertCnt(total)).map {
        Query.Insert(
            key = randString(),
            value = randString()
        )
    }

    val removeQs = (0 until removeCnt(total)).map {
        Query.Remove(
            keyId = RANDOM.nextInt(from = 0, until = KEYS_COUNT)
        )
    }

    return (readQs + writeQs + chainQs + insertQs + removeQs).shuffled(RANDOM)
}

// 79% of operations are read-only
val MAP_QUERIES = genQueries(QUERIES_COUNT).also {
    println(it.size)
}

fun processMapQuery(map: MutableMap<String, String>, q: Query): Unit = when (q) {
    is Query.Write -> map[KEYS[q.keyId]] = q.value
    is Query.Read -> q.result = map[KEYS[q.keyId]]
    is Query.Insert -> map[q.key] = q.value
    is Query.Remove -> {
        map.remove(KEYS[q.keyId])
        Unit
    }
    is Query.Chain -> q.operations.forEach { processMapQuery(map, it) }
}