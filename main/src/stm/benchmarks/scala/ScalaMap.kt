package stm.benchmarks.scala

import stm.benchmarks.testData.KEYS
import stm.benchmarks.testData.VALUES
import java.util.concurrent.Callable


class ScalaMap(private val hmap: HashMap<String, String>) : MutableMap<String, String> {
    override val size: Int = scala.concurrent.stm.japi.STM.atomic(Callable {
        hmap.size
    })

    override fun containsKey(key: String) = scala.concurrent.stm.japi.STM.atomic(Callable {
        key in hmap
    })

    override fun containsValue(value: String) = scala.concurrent.stm.japi.STM.atomic(Callable {
        hmap.containsValue(value)
    })

    override fun get(key: String) = scala.concurrent.stm.japi.STM.atomic(Callable {
        hmap[key]
    })

    override fun isEmpty() = scala.concurrent.stm.japi.STM.atomic(Callable {
        hmap.isEmpty()
    })

    override val entries: MutableSet<MutableMap.MutableEntry<String, String>> =
        scala.concurrent.stm.japi.STM.atomic(Callable {
            hmap.entries
        })

    override val keys: MutableSet<String> = scala.concurrent.stm.japi.STM.atomic(Callable {
        hmap.keys
    })

    override val values: MutableCollection<String> = scala.concurrent.stm.japi.STM.atomic(Callable {
        hmap.values
    })

    override fun clear() = scala.concurrent.stm.japi.STM.atomic(Callable {
        hmap.clear()
    })

    override fun put(key: String, value: String) = scala.concurrent.stm.japi.STM.atomic(Callable {
        hmap.put(key, value)
    })

    override fun putAll(from: Map<out String, String>) = scala.concurrent.stm.japi.STM.atomic(Callable {
        hmap.putAll(from)
    })

    override fun remove(key: String) = scala.concurrent.stm.japi.STM.atomic(Callable {
        hmap.remove(key)
    })

}
