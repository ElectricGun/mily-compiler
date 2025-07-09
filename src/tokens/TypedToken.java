package src.tokens;

/**
 * A token with a datatype
 * @author ElectricGun
 */

public class TypedToken extends Token {

    public String type;

    public TypedToken(String string, int line, String type) {
        super(string, line);

        this.type = type;
    }

    public TypedToken(String string, int line) {
        super(string, line);
    }
}
