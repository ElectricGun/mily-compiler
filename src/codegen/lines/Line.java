package src.codegen.lines;

public class Line {

    String name;
    String string;
    int indent;

    public Line (String name, String string, int indent) {
        this.name = name;
        this.string = string;
        this.indent = indent;
    }

    public String asMlog() {
        return indent() + string;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return  name + ": " + string;
    }

    protected String indent() {
        return "    ".repeat(indent);
    }
}
