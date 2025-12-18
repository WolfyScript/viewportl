package com.wolfyscript.viewportl.gui.model

import java.util.UUID

interface StoreOwner {

    val sharedStore: DataStoreMap

    fun getViewerStore(viewer: UUID): DataStoreMap

}