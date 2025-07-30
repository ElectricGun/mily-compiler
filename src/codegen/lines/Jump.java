package src.codegen.lines;

public class Jump extends Line{

    String to;

    public Jump(String name, String string, String to) {
        super(name, string);

        this.to = to;
    }
}
