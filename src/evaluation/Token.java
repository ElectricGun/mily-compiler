package src.evaluation;

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

    public char charAt(int i) {
        return string.charAt(i);
    }

    @Override
    public String toString() {
        return string;
    }
}
