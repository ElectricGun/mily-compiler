package src.codegen;

import java.util.*;

import static src.constants.Keywords.*;
import static src.constants.Functions.*;

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
        mlogOperationMap.put(KEY_OP_EQUALS, "equal");
        mlogOperationMap.put(KEY_OP_NOT_EQUAL, "notEqual");
        mlogOperationMap.put(KEY_OP_LESS_THAN, "lessThan");
        mlogOperationMap.put(KEY_OP_LESS_THAN_EQUALS, "lessThanEq");
        mlogOperationMap.put(KEY_OP_GREATER_THAN, "greaterThan");
        mlogOperationMap.put(KEY_OP_GREATER_THAN_EQUALS, "greaterThanEq");
        mlogOperationMap.put(KEY_OP_STRICT_EQUALS, "strictEqual");
        mlogOperationMap.put(KEY_OP_AND, "and");
    }
    public static String opAsMlog(String op) throws IllegalArgumentException {
        if (op.equals(KEY_OP_CAST_EXPLICIT))
            // make this a mily error
            throw new IllegalArgumentException("Primitive explicit casting may only be done on constant values in compile time");

        if (!mlogOperationMap.containsKey(op))
            throw new IllegalArgumentException(String.format("Operation \"%s\" has no mlog equivalent", op));

        return mlogOperationMap.get(op);
    }

    public static String valueAsMlog(String value) {
        if (isBoolean(value)) {
            if (keyEquals(KEY_BOOLEAN_FALSE, value)) {
                return "0";

            } else if (keyEquals(KEY_BOOLEAN_TRUE, value)) {
                return "1";

            } else {
                throw new IllegalArgumentException(String.format("Cannot boolean value to mlog \"%s\"", value));
            }
        } else {
            return value;
        }
    }
}
