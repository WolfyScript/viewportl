package com.wolfyscript.viewportl

import org.jetbrains.annotations.ApiStatus

interface ViewportlProvider {

    companion object {

        private var instance: Viewportl? = null

        fun get(): Viewportl {
            return instance ?: throw IllegalStateException("ViewportlProvider not initialized.")
        }

        fun registered(): Boolean {
            return instance != null
        }

        @JvmSynthetic
        @ApiStatus.Internal
        internal fun register(viewportl: Viewportl): Viewportl {
            if (registered()) {
                throw IllegalStateException("ViewportlProvider already registered.")
            }
            return viewportl
        }

    }

}