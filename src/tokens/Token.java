package src.tokens;

/**
 * Basic token, contains a string and line number.
 * @author ElectricGun
 */

public class Token {

    public String string;
    public int line;

    public Token(String string, int line) {
        this.string = string;
        this.line = line;
    }

    public int length() {
        return string.length();
    }

    @Override
    public String toString() {
        return string;
    }
}
