package com.wolfyscript.viewportl.gui.reactivity

import java.util.Optional

interface Resource<T> : ReadOnlySignal<Optional<Result<T>>>