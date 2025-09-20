package mily.codegen.lines;

public class WriteLine extends MemoryLine {

    public WriteLine(String value, String cell, String position, int indent) {
        super(value, cell, position, indent);
    }

    @Override
    public String asMlog() {
        return asMlogMem("write");
    }
}
