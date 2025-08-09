package mily.codegen.blocks;

import mily.parsing.callables.*;

import java.util.*;

public class IRFunction extends IRBlock {

    protected FunctionDeclareNode sourceFunctionNode;

    protected String callLabel;
    protected String callbackVar;
    protected String returnVar;
    protected String argPrefix;
    protected List<String> argNames = new ArrayList<>();
    protected List<String> argTypes = new ArrayList<>();

    public IRFunction(FunctionDeclareNode sourceFunctionNode, String callLabel, String callbackVar, String argPrefix, String retunVar) {
        this.sourceFunctionNode = sourceFunctionNode;
        this.argPrefix = argPrefix;
        this.callLabel = callLabel;
        this.callbackVar = callbackVar;
        this.returnVar = retunVar;
    }

    public String getArgPrefix() {
        return argPrefix;
    }

    public String getCallbackVar() {
        return callbackVar;
    }

    public String getReturnVar() {
        return returnVar;
    }

    public int getArgCount() {
        return argNames.size();
    }

    public String getArg(int i) {
        return argNames.get(i);
    }

    public void addArg(String type, String arg) {
        argNames.add(arg);
        argTypes.add(type);
    }

    public String getCallLabel() {
        return callLabel;
    }
}
