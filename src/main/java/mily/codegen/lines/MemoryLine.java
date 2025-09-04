package mily.codegen.lines;

public abstract class MemoryLine extends Line {

    protected final String position;
    protected final String cell;

    public MemoryLine(String value, String cell, String position, int indent) {
        super(value, indent);

        this.position = position;
        this.cell = cell;
    }

    public String asMlogMem(String op) {
        return indent() + op + " " + string + " " + cell + " " + position;
    }
}
