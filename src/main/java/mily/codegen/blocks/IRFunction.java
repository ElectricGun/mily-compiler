package mily.codegen.blocks;

import mily.parsing.callables.*;
import mily.structures.structs.Type;

import java.util.*;

public class IRFunction extends IRBlock {

    protected FunctionDeclareNode sourceFunctionNode;

    protected String callLabel;
    protected String callbackVar;
    protected String returnVar;
    protected String argPrefix;
    protected List<String> argNames = new ArrayList<>();
    protected List<Type> argTypes = new ArrayList<>();

    public IRFunction(FunctionDeclareNode sourceFunctionNode, String callLabel, String callbackVar, String argPrefix, String retunVar) {
        this.sourceFunctionNode = sourceFunctionNode;
        this.argPrefix = argPrefix;
        this.callLabel = callLabel;
        this.callbackVar = callbackVar;
        this.returnVar = retunVar;
    }

    public String getCallbackVar() {
        return callbackVar;
    }

    public String getReturnVar() {
        return returnVar;
    }

    public String getArg(int i) {
        return argNames.get(i);
    }

    public void addArg(Type type, String arg) {
        argNames.add(arg);
        argTypes.add(type);
    }

    public String getCallLabel() {
        return callLabel;
    }
}
