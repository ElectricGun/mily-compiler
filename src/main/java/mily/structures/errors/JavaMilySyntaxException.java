package mily.structures.errors;

import mily.tokens.*;

public class JavaMilySyntaxException extends JavaMilyException {

    Token token;

    public JavaMilySyntaxException(String message, Token token) {
        super(message);

        this.token = token;
    }

    public Token getToken() {
        return token;
    }
}
