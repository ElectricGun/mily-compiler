package mily.codegen.lines;

public class Jump extends Line {

    String to;

    public Jump(String condition, String to, int indent) {
        super(condition, indent);

        this.to = to;
    }

    @Override
    public String asMlog() {
        return indent() + "jump " + to + " " + string;
    }
}
