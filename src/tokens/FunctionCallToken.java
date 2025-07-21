package src.tokens;

import src.evaluators.*;

/**
 * A token used to store function calls within expressions
 * @author ElectricGun
 */

public class FunctionCallToken extends Token {

    FunctionCallNode functionCallNode;

    public FunctionCallToken(String string, int line, FunctionCallNode functionCallNode) {
        super(string, line);

        this.functionCallNode = functionCallNode;
    }

    public FunctionCallNode getNode() {
        return functionCallNode;
    }

    @Override
    public String toString() {
        return functionCallNode.toString();
    }
}
