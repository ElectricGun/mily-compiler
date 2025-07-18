package src.structure;

import java.util.*;

/**
 * <h1> Variable </h1>
 * Contains name and type, used in validation
 * @see Validation
 * @author ElectricGun
 */

public class Variable {

    String name;
    String type;

    public Variable(String type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public boolean equals(Object variable) {
        return variable.getClass() == this.getClass() &&
                Objects.equals(((Variable) variable).type, this.type) &&
                Objects.equals(((Variable) variable).name, this.name);
    }

    @Override
    public String toString() {
        return type + " : " + name;
    }
}
