package mily.tokens;

import mily.abstracts.Named;
import mily.constants.Functions;

/**
 * Basic token, contains a string and line number.
 *
 * @author ElectricGun
 */

public class Token implements Named {

    public final String source;
    public final int line;
    public String string;

    public Token(String string, String source, int line) {
        this.string = string;
        this.line = line;
        this.source = source;
    }

    @Override
    public String getName() {
        return string;
    }

    @Override
    public void setName(String name) {
        this.string = name;
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

    @SuppressWarnings("unused")
    public boolean equalsKey(Token key) {
        return string.equals(key.string);
    }

    public boolean isWhiteSpace() {
        return Functions.isWhiteSpace(this);
    }

    public boolean isVariableName() {
        return Functions.isVariableName(this);
    }
}
