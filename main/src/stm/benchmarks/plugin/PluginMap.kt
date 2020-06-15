package stm.benchmarks.plugin

import kotlinx.stm.SharedMutable

@SharedMutable
class PluginMap(val _hmap: HashMap<String, String>) : MutableMap<String, String> {
    private val hmap = _hmap

    override val size = hmap.size
    override fun containsKey(key: String) = hmap.containsKey(key)
    override fun containsValue(value: String) = hmap.containsValue(value)
    override fun get(key: String) = hmap.get(key)
    override fun isEmpty() = hmap.isEmpty()
    override val entries: MutableSet<MutableMap.MutableEntry<String, String>> = hmap.entries
    override val keys: MutableSet<String> = hmap.keys
    override val values: MutableCollection<String> = hmap.values
    override fun clear() = hmap.clear()
    override fun put(key: String, value: String) = hmap.put(key, value)
    override fun putAll(from: Map<out String, String>) = hmap.putAll(from)
    override fun remove(key: String) = hmap.remove(key)
}