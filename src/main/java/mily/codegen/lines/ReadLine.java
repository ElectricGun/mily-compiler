package mily.codegen.lines;

public class ReadLine extends MemoryLine {

    public ReadLine(String value, String cell, String position, int indent) {
        super(value, cell, position, indent);
    }

    @Override
    public String asMlog() {
        return asMlogMem("read");
    }
}
