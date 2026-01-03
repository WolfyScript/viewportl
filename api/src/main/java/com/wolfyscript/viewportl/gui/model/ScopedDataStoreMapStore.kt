package com.wolfyscript.viewportl.gui.model

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.viewportl.viewportl

/**
 * A DataStore, that holds nested [DataStoreMaps][DataStoreMap].
 */
class ScopedDataStoreMapStore : Store() {

    private val storeMap = mutableMapOf<Key, DataStoreMap>()

    fun getOrCreateStore(key: Key) : DataStoreMap {
        return storeMap.getOrPut(key) { ScafallProvider.get().viewportl.guiFactory.dataStoreFactory.createDataStoreMap() }
    }

    fun clearStore(key: Key) {
        storeMap.remove(key)?.clear()
    }

    override fun onCleared() {
        storeMap.forEach { (_, storeMap) -> storeMap.clear() }
    }

}