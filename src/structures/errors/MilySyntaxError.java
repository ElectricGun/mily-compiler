package src.structures.errors;

public class MilySyntaxError extends MilyError  {

    public MilySyntaxError(String message) {
        super(message);
    }

    @Override
    public String getErrorMessage() {
        return "MilySyntaxError: " + message;
    }
}
