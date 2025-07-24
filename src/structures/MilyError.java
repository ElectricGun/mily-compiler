package src.structures;

import src.interfaces.*;

public class MilyError implements MilyThrowable {

    String message;

    public MilyError(String message) {
        this.message = message;
    }

    @Override
    public void setErrorMessage(String message) {
        this.message = message;
    }

    @Override
    public String getErrorMessage() {
        return "MilyError: " + message;
    }
}
