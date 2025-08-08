package mily.codegen.lines;

public class CommentLine extends Line {

    public CommentLine(String string, int indent) {
        super(string, indent);
    }

    @Override
    public String asMlog() {
        return indent() + "# " + string;
    }
}
