package com.wolfyscript.viewportl.gui.factories

import com.wolfyscript.viewportl.gui.model.DataStoreMap

interface DataStoreFactory {

    fun createDataStoreMap(): DataStoreMap

}