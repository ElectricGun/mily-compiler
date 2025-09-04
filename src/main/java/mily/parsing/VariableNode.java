package mily.parsing;

import mily.abstracts.*;
import mily.structures.dataobjects.*;
import mily.tokens.*;

/**
 * <h1> Class VariableNode </h1>
 * Template for Declaration and Assignment
 *
 * @author ElectricGun
 */

public abstract class VariableNode extends EvaluatorNode implements Typed, Named {

    protected String variableName;
    protected Type type;

    public VariableNode(Type type, Token token, int depth) {
        super(token, depth);
        this.type = type;
        this.variableName = token.string;
    }

    @Override
    public String getName() {
        return variableName;
    }

    @Override
    public void setName(String variableName) {
        this.variableName = variableName;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void setType(Type type) {
        this.type = type;
    }

    public boolean isDeclared() {
        return !variableName.isEmpty();
    }

}
