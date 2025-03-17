package com.wolfyscript.viewportl.common.gui.reactivity

import com.wolfyscript.viewportl.gui.reactivity.Resource
import java.util.Optional
import kotlin.reflect.KProperty

class ResourceImpl<T>(val id: NodeId, private val type: Class<Optional<Result<T>>>) : Resource<T> {

    override fun get(): Optional<Result<T>> {
        id.runtime.reactiveSource.subscribe(id)
        return getNoTracking()
    }

    override fun getNoTracking(): Optional<Result<T>> {
        return id.runtime.reactiveSource.getValue(id, type)
    }

    override fun getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ): Optional<Result<T>> {
        return get()
    }
}