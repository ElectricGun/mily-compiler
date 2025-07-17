package src.structure;

public class Variable {

    String name;
    String type;

    public Variable(String type, String name) {
        this.name = name;
        this.type = type;
    }

    @Override
    public boolean equals(Object variable) {
        return this.name.equals(((Variable) variable).name) && this.type.equals(((Variable) variable).type);
    }

    @Override
    public String toString() {
        return type + " " + name;
    }
}
