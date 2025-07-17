package src.evaluators;

import src.tokens.Token;

public class FunctionArgNode extends DeclarationNode {

    public FunctionArgNode(String type, Token token, int depth) {
        super(type, token, depth);
    }

    @Override
    public String toString() {
        return "arg : " + type + " " + variableName;
    }
}
