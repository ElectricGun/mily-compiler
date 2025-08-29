package mily.structures.structs;

import mily.abstracts.*;

import java.util.*;

public class CallableSignature implements Named {

    String name;
    List<String> argTypes = new ArrayList<>();

    public CallableSignature(String name) {
        this.name = name;
    }

    public CallableSignature(String name, String... argTypes) {
        this(name);
        Collections.addAll(this.argTypes, argTypes);
    }

    public CallableSignature(String name, List<String> argTypes) {
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
        return name + "_" + String.join("_", argTypes);
    }
}
