package src.codegen.lines;

public abstract class Line {

    String string;
    int indent;

    public Line (String string, int indent) {
        this.string = string;
        this.indent = indent;
    }

    public String asMlog() {
        return indent() + string;
    }

    @Override
    public String toString() {
        return  string;
    }

    protected String indent() {
        return "  ".repeat(indent);
    }
}
