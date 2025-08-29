package mily.parsing;

import mily.abstracts.*;
import mily.tokens.*;

/**
 * <h1> Class VariableNode </h1>
 * Template for Declaration and Assignment
 *
 * @author ElectricGun
 */

public abstract class VariableNode extends EvaluatorNode implements Typed, Named {

    protected String variableName;
    protected String type;

    public VariableNode(String type, Token token, int depth) {
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
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    public boolean isDeclared() {
        return !variableName.isEmpty();
    }

}
