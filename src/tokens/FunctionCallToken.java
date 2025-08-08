package src.tokens;

import src.parsing.*;
import static src.constants.Keywords.*;

/**
 * A token used to store function calls within expressions
 * @author ElectricGun
 */

public class FunctionCallToken extends TypedToken {

    FunctionCallNode functionCallNode;

    public FunctionCallToken(String string, int line, FunctionCallNode functionCallNode) {
        super(string, line, KEY_DATA_UNKNOWN);

        this.functionCallNode = functionCallNode;
    }

    public FunctionCallNode getNode() {
        return functionCallNode;
    }

    @Override
    public String getType() {
        return functionCallNode.getType();
    }

    @Override
    public void setType(String type) {
        functionCallNode.setType(type);
    }

    @Override
    public String toString() {
        return functionCallNode.toString() + ": " + functionCallNode.getType();
    }
}
