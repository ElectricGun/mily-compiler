package src.codegen.lines;

public class SetLine extends VariableLine {

    public SetLine(String var, String value, int indent) {
        super(var, value, indent);
    }

    public String getVar() {
        return varName;
    }

    public String getValue() {
        return string;
    }

    @Override
    public String asMlog() {
        return indent() + "set " + getVar() + " " + getValue();
    }
}
