package src.evaluators;

import src.tokens.*;

/**
 * <h3> Template for Declaration and Assignment </h3>
 *  @author ElectricGun
 */

public abstract class VariableEvaluatorNode extends EvaluatorNode {

    String variableName = "";

    public VariableEvaluatorNode(Token token, int depth) {
        super(token, depth);
    }

    public String getVariableName() {
        return variableName;
    }

    public boolean isDeclared() {
        return !variableName.isEmpty();
    }
}
