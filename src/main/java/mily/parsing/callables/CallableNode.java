package mily.parsing.callables;

import mily.abstracts.*;
import mily.parsing.*;
import mily.tokens.*;

import java.util.*;

public abstract class CallableNode extends EvaluatorNode implements Callable {

    protected String name;
    protected List<String> argumentNames = new ArrayList<>();
    protected List<String> argumentTypes = new ArrayList<>();
    protected String returnType;

    public CallableNode(String name, Token nameToken, int depth) {
        super(nameToken, depth);

        this.name = name;
    }

    @Override
    public List<String> getArgumentTypes() {
        return argumentTypes;
    }

    @Override
    public List<String> getArgumentNames() {
        return argumentNames;
    }

    @Override
    public String getType() {
        return returnType;
    }

    @Override
    public void setType(String type) {
        this.returnType = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String[] getArgumentNamesArr() {
        String[] out = new String[argumentNames.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = argumentNames.get(i);
        }
        return out;
    }

    public String[] getArgumentTypesArr() {
        String[] out = new String[argumentTypes.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = argumentTypes.get(i);
        }
        return out;
    }

    public int getArgCount() {
        return argumentNames.size();
    }

    public String getArg(int i) {
        return argumentNames.get(i);
    }

    public String getArgType(int i) {
        return argumentTypes.get(i);
    }
}
