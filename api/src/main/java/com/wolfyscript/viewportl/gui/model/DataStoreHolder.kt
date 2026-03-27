package com.wolfyscript.viewportl.gui.model

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.viewportl.viewportl
import java.util.UUID

class DataStoreHolder : StoreOwner {

    override val sharedStore: DataStoreMap = ScafallProvider.get().viewportl.guiFactory.dataStoreFactory.createDataStoreMap()
    private val viewerStores: MutableMap<UUID, DataStoreMap> = mutableMapOf()

    override fun getViewerStore(viewer: UUID): DataStoreMap {
        return viewerStores[viewer] ?: throw IllegalStateException("Viewer $viewer not part of runtime")
    }

    fun clearViewer(viewer: UUID) {
        viewerStores.remove(viewer)
    }

    fun addViewer(viewer: UUID) {
        viewerStores[viewer] = ScafallProvider.get().viewportl.guiFactory.dataStoreFactory.createDataStoreMap()
    }

}