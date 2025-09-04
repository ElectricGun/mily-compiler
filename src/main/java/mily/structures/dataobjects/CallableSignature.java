package mily.structures.dataobjects;

import mily.abstracts.*;

import java.util.*;

public class CallableSignature implements Named {

    protected final List<Type> argTypes = new ArrayList<>();
    protected String name;

    public CallableSignature(String name) {
        this.name = name;
    }

    public CallableSignature(String name, Type... argTypes) {
        this(name);
        Collections.addAll(this.argTypes, argTypes);
    }

    public CallableSignature(String name, List<Type> argTypes) {
        this(name);
        this.argTypes.addAll(argTypes);
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CallableSignature that = (CallableSignature) o;
        return Objects.equals(name, that.name) && Objects.equals(argTypes, that.argTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, argTypes);
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder(name + "_");

        for (Type argType : argTypes) {
            out.append(argTypes);
        }

        return out.toString();
    }
}
