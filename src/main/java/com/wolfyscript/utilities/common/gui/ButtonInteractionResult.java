package com.wolfyscript.utilities.common.gui;

public class ButtonInteractionResult {

    private ResultType type;

    ButtonInteractionResult() {
        type = ResultType.DEFAULT;
    }

    ButtonInteractionResult(ResultType type) {
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

    public static ButtonInteractionResult cancel(boolean cancelled) {
        ButtonInteractionResult result = new ButtonInteractionResult();
        result.setCancelled(cancelled);
        return result;
    }

    public static ButtonInteractionResult def() {
        return new ButtonInteractionResult();
    }

    public static ButtonInteractionResult type(ResultType type) {
        return new ButtonInteractionResult(type);
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
