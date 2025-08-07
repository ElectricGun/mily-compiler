package src.structures.errors;

import src.interfaces.*;

public class MilyError implements MilyThrowable {

    String message;

    public MilyError(String message) {
        this.message = message;
    }

    @Override
    public String getErrorMessage() {
        return "MilyError: " + message;
    }
}
