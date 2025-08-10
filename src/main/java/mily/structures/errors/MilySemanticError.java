package mily.structures.errors;

public class MilySemanticError extends MilyError {

    public MilySemanticError(String message) {
        super(message);
    }

    @Override
    public String getErrorMessage() {
        return "MilySemanticError: " + message;
    }
}
