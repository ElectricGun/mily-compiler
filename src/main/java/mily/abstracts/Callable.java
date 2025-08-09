package mily.abstracts;

import java.util.*;

public interface Callable extends Named, Typed, HasFunctionKey {
    List<String> getArgumentNames();
    List<String> getArgumentTypes();
    boolean isOverload(String name, String... types);
}
