package src.codegen.lines;

public class Set extends Line {

    public Set(String var, String value) {
        super(var, value);
    }

    public String getVar() {
        return name;
    }

    public String getValue() {
        return string;
    }

    @Override
    public String asMlog() {
        return "set " + getVar() + " " + getValue();
    }
}
