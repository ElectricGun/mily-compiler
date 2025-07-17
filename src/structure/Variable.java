package src.structure;

import java.util.Objects;

public class Variable {

    String name;
    String type;

    public Variable(String type, String name) {
        this.type = Objects.requireNonNullElse(type, "");
        this.name = Objects.requireNonNullElse(name, "");
    }

    @Override
    public boolean equals(Object variable) {
        return variable.getClass() == this.getClass() &&
                this.name.equals(((Variable) variable).name) &&
                this.type.equals(((Variable) variable).type);
    }

    @Override
    public String toString() {
        return type + " : " + name;
    }
}
