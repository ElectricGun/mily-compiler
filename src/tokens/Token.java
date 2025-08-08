package src.tokens;

/**
 * Basic token, contains a string and line number.
 *
 * @author ElectricGun
 */

public class Token {

    public String string;
    public String source;
    public int line;

    public Token(String string, String source, int line) {
        this.string = string;
        this.line = line;
        this.source = source;
    }

    public int length() {
        return string.length();
    }

    @Override
    public String toString() {
        return string;
    }

    public boolean equalsKey(String key) {
        return string.equals(key);
    }

    public boolean equalsKey(Token key) {
        return string.equals(key.string);
    }
}
