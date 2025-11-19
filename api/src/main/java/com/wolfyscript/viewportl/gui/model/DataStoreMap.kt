package com.wolfyscript.viewportl.gui.model

import com.wolfyscript.scafall.identifier.Key

interface DataStoreMap {

    operator fun <T: Store> set(key: Key, instance: T)

    operator fun <T: Store> get(key: Key): T?

}