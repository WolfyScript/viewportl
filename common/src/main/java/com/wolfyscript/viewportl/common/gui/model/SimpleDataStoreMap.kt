package com.wolfyscript.viewportl.common.gui.model

import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.viewportl.gui.model.DataStoreMap
import com.wolfyscript.viewportl.gui.model.Store

class SimpleDataStoreMap : DataStoreMap {

    private val map: MutableMap<Key, Store> = mutableMapOf()

    override fun <T : Store> set(
        key: Key,
        instance: T,
    ) {
        map[key] = instance
    }

    override fun <T : Store> get(key: Key): T? {
        return map[key]?.let {
            return it as? T
        }
    }


}