package src.evaluators;

import src.tokens.Token;

public class FunctionArgNode extends VariableNode {

    public FunctionArgNode(Token token, int depth) {
        super(token, depth);
    }

    @Override
    public String toString() {
        return "arg : " + token;
    }
}
