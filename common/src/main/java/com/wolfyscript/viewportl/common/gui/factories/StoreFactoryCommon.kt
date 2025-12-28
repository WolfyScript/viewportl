package com.wolfyscript.viewportl.common.gui.factories

import com.wolfyscript.viewportl.common.gui.model.SimpleDataStoreMap
import com.wolfyscript.viewportl.gui.factories.DataStoreFactory
import com.wolfyscript.viewportl.gui.model.DataStoreMap

class StoreFactoryCommon : DataStoreFactory {

    override fun createDataStoreMap(): DataStoreMap {
        return SimpleDataStoreMap()
    }

}