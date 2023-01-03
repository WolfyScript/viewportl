package com.wolfyscript.utilities.common.gui;

public interface InteractionDetails<D extends Data> {

    boolean isCancelled();

    InteractionResult.ResultType getResultType();


}
