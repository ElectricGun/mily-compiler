package mily.abstracts;

import java.util.*;

public interface Callable extends Named, Typed, CallSignatured {

    List<String> getArgumentTypes();

    List<String> getArgumentNames();

    boolean isOverload(Caller caller, String name, String... types);

    boolean isOverload(Callable callable, String name, String... types);

    boolean isOverload(String name, String... types);
}
