package src.structures;

import src.interfaces.*;

public class MilyException implements MilyThrowable {

    String message;

    public MilyException(String message) {
        this.message = message;
    }

    @Override
    public void setErrorMessage(String message) {
        this.message = message;
    }

    @Override
    public String getErrorMessage() {
        return "MilyException: " + message;
    }
}
