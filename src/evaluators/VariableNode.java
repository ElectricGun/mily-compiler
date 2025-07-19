package src.evaluators;

import src.interfaces.Typed;
import src.tokens.*;

/**
 * <h1> Class VariableNode </h1>
 * Template for Declaration and Assignment
 * @author ElectricGun
 */

public abstract class VariableNode extends EvaluatorNode implements Typed {

    String variableName;
    String type;

    public VariableNode(String type, Token token, int depth) {
        super(token, depth);
        this.type = type;
        this.variableName = token.string;
    }

    public String getVariableName() {
        return variableName;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }

    public boolean isDeclared() {
        return !variableName.isEmpty();
    }

}
