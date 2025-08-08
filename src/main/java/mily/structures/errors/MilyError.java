package mily.structures.errors;

import mily.interfaces.*;

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
