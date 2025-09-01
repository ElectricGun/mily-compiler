package mily.tokens;

import mily.parsing.invokes.*;
import mily.structures.structs.Type;

import static mily.constants.Keywords.*;

/**
 * A token used to store function calls within expressions
 *
 * @author ElectricGun
 */

public class CallerNodeToken extends TypedToken {

    final CallerNode callerNode;

    public CallerNodeToken(String string, String source, int line, CallerNode callerNode) {
        super(string, source, KEY_DATA_UNKNOWN, line);

        this.callerNode = callerNode;
    }

    public CallerNode getNode() {
        return callerNode;
    }

    @Override
    public Type getType() {
        return callerNode.getType();
    }

    @Override
    public void setType(Type type) {
        callerNode.setType(type);
    }

    @Override
    public String toString() {
        return callerNode.toString() + ": " + callerNode.getType();
    }
}
