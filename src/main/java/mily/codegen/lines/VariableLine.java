package mily.codegen.lines;

public class VariableLine extends Line {

    protected String varName;

    public VariableLine(String varName, String string, int indent) {
        super(string, indent);

        this.varName = varName;
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }
}
