package mily.tokens;

import mily.parsing.invokes.*;

import static mily.constants.Keywords.*;

/**
 * A token used to store function calls within expressions
 *
 * @author ElectricGun
 */

public class CallerNodeToken extends TypedToken {

    CallerNode callerNode;

    public CallerNodeToken(String string, String source, int line, CallerNode callerNode) {
        super(string, source, KEY_DATA_UNKNOWN, line);

        this.callerNode = callerNode;
    }

    public CallerNode getNode() {
        return callerNode;
    }

    @Override
    public String getType() {
        return callerNode.getType();
    }

    @Override
    public void setType(String type) {
        callerNode.setType(type);
    }

    @Override
    public String toString() {
        return callerNode.toString() + ": " + callerNode.getType();
    }
}
