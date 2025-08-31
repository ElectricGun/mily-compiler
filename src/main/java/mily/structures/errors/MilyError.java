package mily.structures.errors;

import mily.abstracts.*;

public class MilyError implements MilyThrowable {

    protected String message;

    public MilyError(String message) {
        this.message = message;
    }

    @Override
    public String getErrorMessage() {
        return "MilyError: " + message;
    }
}
