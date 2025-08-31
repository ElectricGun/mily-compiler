package mily.tokens;

import mily.abstracts.*;
import mily.structures.structs.*;

/**
 * A token with a datatype
 *
 * @author ElectricGun
 */

public class TypedToken extends Token implements Typed {

    protected Type type;
    protected boolean isVariableRef = false;

    public TypedToken(String string, String source, Type type, int line) {
        super(string, source, line);

        this.type = type;
    }

    public TypedToken(String string, String source, String datatypeString, int line) {
        super(string, source, line);

        this.type = new Type(datatypeString);
    }

    public static TypedToken fromToken(Token token, Type type) {
        return new TypedToken(token.string, token.source, type, token.line);
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void setType(Type type) {
        this.type = type;
    }

    public boolean isVariableRef() {
        return isVariableRef;
    }

    public void setVariableRef(boolean variableRef) {
        isVariableRef = variableRef;
    }

    @Override
    public String toString() {
        return "(" + super.toString() + " : " + type + " | " + (isVariableRef ? "REF" : "VAL") + ")";
    }
}
