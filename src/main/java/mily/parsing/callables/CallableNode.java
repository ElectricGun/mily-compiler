package mily.parsing.callables;

import mily.abstracts.*;
import mily.parsing.*;
import mily.structures.structs.CallableSignature;
import mily.structures.structs.Type;
import mily.tokens.*;

import java.util.*;

public abstract class CallableNode extends EvaluatorNode implements Callable {

    protected String name;
    protected List<String> argumentNames = new ArrayList<>();
    protected List<Type> argumentTypes = new ArrayList<>();
    protected Type returnType;

    public CallableNode(String name, Token nameToken, int depth) {
        super(nameToken, depth);

        this.name = name;
    }

    public CallableSignature signature() {
        return new CallableSignature(name, argumentTypes);
    }

    @Override
    public List<Type> getArgumentTypes() {
        return argumentTypes;
    }

    @Override
    public List<String> getArgumentNames() {
        return argumentNames;
    }

    @Override
    public Type getType() {
        return returnType;
    }

    @Override
    public void setType(Type type) {
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

    public void setArgName(int i, String newArg) {
        argumentNames.set(i, newArg);
    }

    public String[] getArgumentNamesArr() {
        String[] out = new String[argumentNames.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = argumentNames.get(i);
        }
        return out;
    }

    public Type[] getArgumentTypesArr() {
        Type[] out = new Type[argumentTypes.size()];
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

    public Type getArgType(int i) {
        return argumentTypes.get(i);
    }
}
