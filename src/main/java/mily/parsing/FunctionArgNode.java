package mily.parsing;

import mily.structures.structs.Type;
import mily.tokens.*;

/**
 * <h1> Class FunctionArgNode </h1>
 * Function Args
 * Variable declaration node for function arguments,
 * functionally the same
 *
 * @author ElectricGun
 * @see mily.parsing.DeclarationNode
 */

public class FunctionArgNode extends DeclarationNode {

    public FunctionArgNode(Type type, Token token, int depth) {
        super(type, token, depth);
    }

    @Override
    public String toString() {
        return "arg : " + type + " " + variableName;
    }
}
