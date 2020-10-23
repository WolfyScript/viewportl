package me.wolfyscript.utilities.main.messages;

public class InputButtonMessage {

    private final String buttonID;
    private final String message;

    public InputButtonMessage(String buttonID, String message) {
        this.buttonID = buttonID;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getButtonID() {
        return buttonID;
    }
}
