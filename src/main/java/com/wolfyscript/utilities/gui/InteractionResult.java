package com.wolfyscript.utilities.gui;

public class InteractionResult {

    private ResultType type;

    InteractionResult() {
        type = ResultType.DEFAULT;
    }

    InteractionResult(ResultType type) {
        this.type = type;
    }

    public boolean isCancelled() {
        return getType() == ResultType.DENY;
    }

    public void setCancelled(boolean cancel) {
        setType(cancel ? ResultType.DENY : ResultType.DEFAULT);
    }

    public ResultType getType() {
        return type;
    }

    public void setType(ResultType type) {
        this.type = type;
    }

    public static InteractionResult cancel(boolean cancelled) {
        InteractionResult result = new InteractionResult();
        result.setCancelled(cancelled);
        return result;
    }

    public static InteractionResult def() {
        return new InteractionResult();
    }

    public static InteractionResult type(ResultType type) {
        return new InteractionResult(type);
    }

    public enum ResultType {

        /**
         * Deny the event. Depending on the event, the action indicated by the
         * event will either not take place or will be reverted. Some actions
         * may not be denied.
         */
        DENY,
        /**
         * Neither deny nor allow the event. The server will proceed with its
         * normal handling.
         */
        DEFAULT,
        /**
         * Allow / Force the event. The action indicated by the event will
         * take place if possible, even if the server would not normally allow
         * the action. Some actions may not be allowed.
         */
        ALLOW;
    }
}
