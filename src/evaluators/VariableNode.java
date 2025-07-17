package src.evaluators;

import src.tokens.*;

/**
 * <h3> Template for Declaration and Assignment </h3>
 *  @author ElectricGun
 */

public abstract class VariableNode extends EvaluatorNode {

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

    public String getType() {
        return type;
    }


    public boolean isDeclared() {
        return !variableName.isEmpty();
    }
}
