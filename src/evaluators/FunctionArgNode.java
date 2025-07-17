package src.evaluators;

import src.tokens.*;
/**
 * Variable declaration node for function arguments,
 * functionally the same
 * @see src.evaluators.DeclarationNode
 * @author ElectricGun
 */

public class FunctionArgNode extends DeclarationNode {

    public FunctionArgNode(String type, Token token, int depth) {
        super(type, token, depth);
    }

    @Override
    public String toString() {
        return "arg : " + type + " " + variableName;
    }
}
