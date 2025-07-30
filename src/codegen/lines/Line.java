package src.codegen.lines;

public class Line {

    String name;
    String string;

    public Line (String name, String string) {
        this.name = name;
        this.string = string;
    }

    public String asMlog() {
        return string;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name + ": " + string;
    }
}
