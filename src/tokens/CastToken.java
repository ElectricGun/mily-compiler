package src.tokens;

/**
 * A token used to indicate casting in an expression
 * @author ElectricGun
 */

public class CastToken extends Token {

    public CastToken(String string, int line) {
        super(string, line);
    }
}
