package mily.abstracts;

import java.util.*;

public interface Callable extends Named, Typed, HasFunctionKey {
    List<String> getArgumentTypes();
    List<String> getArgumentNames();
    boolean isOverload(Caller caller, String name, String... types);
}
