package src.structures;

import src.interfaces.Typed;
import src.processing.Validation;

import java.util.*;

/**
 * <h1> Class Variable </h1>
 * Contains name and type, used in validation
 * @see Validation
 * @author ElectricGun
 */

public class Variable implements Typed {

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
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type + " : " + name;
    }
}
