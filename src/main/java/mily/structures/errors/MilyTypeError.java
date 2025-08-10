package mily.structures.errors;

public class MilyTypeError extends MilySemanticError {

    public MilyTypeError(String message) {
        super(message);
    }

    @Override
    public String getErrorMessage() {
        return "MilyTypeError: " + message;
    }
}
