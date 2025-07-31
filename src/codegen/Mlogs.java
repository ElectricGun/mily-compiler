package src.codegen;

import java.util.*;

import static src.constants.Keywords.*;

public class Mlogs {
    protected static final HashMap<String, String> mlogOperationMap = new HashMap<>();
    static {
        // TODO add more ops
        mlogOperationMap.put(KEY_OP_ADD, "add");
        mlogOperationMap.put(KEY_OP_MUL, "mul");
        mlogOperationMap.put(KEY_OP_SUB, "sub");
        mlogOperationMap.put(KEY_OP_DIV, "div");
        mlogOperationMap.put(KEY_OP_POW, "pow");
        mlogOperationMap.put(KEY_OP_IDIV, "idiv");
        mlogOperationMap.put(KEY_OP_MOD, "mod");
    }

    public static String opAsMlog(String op) throws IllegalArgumentException {
        if (!mlogOperationMap.containsKey(op))
            throw new IllegalArgumentException(String.format("Operation \"%s\" has no mlog equivalent", op));

        return mlogOperationMap.get(op);
    }
}
