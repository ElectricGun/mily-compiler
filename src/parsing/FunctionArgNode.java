package src.parsing;

import src.tokens.*;

/**
 * <h1> Class FunctionArgNode </h1>
 * Function Args
 * Variable declaration node for function arguments,
 * functionally the same
 *
 * @author ElectricGun
 * @see src.parsing.DeclarationNode
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
