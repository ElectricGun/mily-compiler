package mily.abstracts;

import mily.structures.structs.*;

import java.util.*;

public interface Callable extends Named, Typed, CallSignatured {

    List<Type> getArgumentTypes();

    List<String> getArgumentNames();

    boolean isOverload(String name, Type... types);
}
