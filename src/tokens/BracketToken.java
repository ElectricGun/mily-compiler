package src.tokens;

import src.parsing.*;

import static src.constants.Keywords.*;

/**
 * A token used to store parentheses within expressions
 *
 * @author ElectricGun
 */

public class BracketToken extends TypedToken {

    private final OperationNode operationNode;

    public BracketToken(String string, String source, int line, OperationNode operationNode) {
        super(string, source, KEY_DATA_UNKNOWN, line);

        this.operationNode = operationNode;
    }

    public OperationNode getOperationEvaluator() {
        return operationNode;
    }

    @Override
    public String toString() {
        return operationNode.toString();
    }
}
