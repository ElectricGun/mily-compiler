package mily.codegen.lines;

public class Line {

    protected final String string;
    protected final int indent;

    public Line(String string, int indent) {
        this.string = string;
        this.indent = indent;
    }

    public String asMlog() {
        return indent() + string;
    }

    @Override
    public String toString() {
        return asMlog().trim();
    }

    protected String indent() {
        return "  ".repeat(indent);
    }

    public int getIndent() {
        return indent;
    }
}
