package src.codegen.lines;

public class Jump extends Line{

    String to;

    public Jump(String name, String string, String to, int indent) {
        super(name, string, indent);

        this.to = to;
    }

    @Override
    public String asMlog() {
        return indent() + "jump " + to + " " + string;
    }
}
