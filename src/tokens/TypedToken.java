package src.tokens;

import src.interfaces.*;

/**
 * A token with a datatype
 * @author ElectricGun
 */

public class TypedToken extends Token implements Typed {

    String type;

    public TypedToken(String string, int line, String type) {
        super(string, line);

        this.type = type;
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
        return super.toString() + " : " + type;
    }

    public static TypedToken fromToken(Token token, String type) {
        return new TypedToken(token.string, token.line, type);
    }
}
