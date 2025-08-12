package mily.parsing.callables;

import mily.abstracts.*;
import mily.parsing.*;
import mily.tokens.*;

import java.util.*;

public abstract class CallableNode extends EvaluatorNode implements Callable {
    List<String> argumentNames = new ArrayList<>();
    List<String> argumentTypes = new ArrayList<>();
    String returnType;

    public CallableNode(Token nameToken, int depth) {
        super(nameToken, depth);
    }

    @Override
    public List<String> getArgumentTypes() {
        return new ArrayList<>(argumentTypes);
    }

    @Override
    public List<String> getArgumentNames() {
        return new ArrayList<>(argumentNames);
    }

    @Override
    public void setType(String type) {
        this.returnType = type;
    }

    @Override
    public String getType() {
        return returnType;
    }

    // todo probably give this a name var
    @Override
    public String getName() {
        return nameToken.string;
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
