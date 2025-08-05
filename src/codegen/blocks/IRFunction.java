package src.codegen.blocks;

import java.util.*;

public class IRFunction extends IRBlock {

    protected String callLabel;
    protected String callbackVar;
    protected String returnVar;
    protected String argPrefix;
    protected List<String> argNames = new ArrayList<>();

    public IRFunction(String callLabel, String callbackVar, String argPrefix, String retunVar) {
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

    public void addArg(String arg) {
        argNames.add(arg);
    }

    public String getCallLabel() {
        return callLabel;
    }
}
