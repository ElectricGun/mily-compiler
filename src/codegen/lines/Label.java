package src.codegen.lines;

public class Label extends Line {

    public Label(String labelName, int indent) {
        super(labelName, indent);
    }

    @Override
    public String asMlog() {
        return indent() + this.string + ":";
    }
}
