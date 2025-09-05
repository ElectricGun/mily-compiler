package mily.interfaces;

import mily.structures.dataobjects.*;

import java.util.*;

public interface Callable extends Named, Typed, CallSignatured {

    List<Type> getArgumentTypes();

    List<String> getArgumentNames();

    boolean isOverload(String name, Type... types);
}
