package mily.tokens;

import mily.abstracts.*;

/**
 * A token with a datatype
 *
 * @author ElectricGun
 */

public class TypedToken extends Token implements Typed {

    String type;

    public TypedToken(String string, String source, String type, int line) {
        super(string, source, line);

        this.type = type;
    }

    public static TypedToken fromToken(Token token, String type) {
        return new TypedToken(token.string, token.source, type, token.line);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "(" + super.toString() + " : " + type + ")";
    }
}
