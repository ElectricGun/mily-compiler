package src.evaluators;

import src.tokens.*;

/**
 * <h3> Template for Declaration and Assignment </h3>
 *  @author ElectricGun
 */

public abstract class VariableNode extends EvaluatorNode {

    String variableName = "";

    public VariableNode(Token token, int depth) {
        super(token, depth);
    }

    public String getVariableName() {
        return variableName;
    }

    public boolean isDeclared() {
        return !variableName.isEmpty();
    }
}
