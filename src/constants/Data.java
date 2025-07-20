package src.constants;

import src.evaluators.*;
import java.util.*;
import java.util.function.*;

import static src.constants.Keywords.*;

/**
 * <h1> Class Data </h1>
 * Utilities regarding typing, operations, etc
 */

public class Data {

    // TODO implement more operators
    // This doesn't look elegant and can be improved, but eh
    public static final Map<String, Consumer<OperationNode>> operationsParserMap = new HashMap<>();
    static {
        operationsParserMap.put(KEY_OP_ADD, o -> {
            if ((KEY_DATA_INT.equals(o.getLeftConstantType()) && KEY_DATA_INT.equals(o.getRightConstantType()))) {
                o.makeConstant((int) (o.getLeftConstantNumeric() + o.getRightConstantNumeric()));
            } else {
                o.makeConstant(o.getLeftConstantNumeric() + o.getRightConstantNumeric());
            }
        });
        operationsParserMap.put(KEY_OP_SUB, o -> {
            if ((KEY_DATA_INT.equals(o.getLeftConstantType()) && KEY_DATA_INT.equals(o.getRightConstantType()))) {
                o.makeConstant((int) (o.getLeftConstantNumeric() - o.getRightConstantNumeric()));

            } else {
                o.makeConstant(o.getLeftConstantNumeric() - o.getRightConstantNumeric());
            }
        });
        operationsParserMap.put(KEY_OP_MUL, o -> {
            if ((KEY_DATA_INT.equals(o.getLeftConstantType()) && KEY_DATA_INT.equals(o.getRightConstantType()))) {
                o.makeConstant((int) (o.getLeftConstantNumeric() * o.getRightConstantNumeric()));

            } else {
                o.makeConstant(o.getLeftConstantNumeric() * o.getRightConstantNumeric());
            }
        });
        operationsParserMap.put(KEY_OP_DIV, o -> o.makeConstant(o.getLeftConstantNumeric() / o.getRightConstantNumeric()));
        operationsParserMap.put(KEY_OP_MOD, o -> {
            if ((KEY_DATA_INT.equals(o.getLeftConstantType()) && KEY_DATA_INT.equals(o.getRightConstantType()))) {
                o.makeConstant((int) (o.getLeftConstantNumeric() % o.getRightConstantNumeric()));

            } else {
                o.makeConstant(o.getLeftConstantNumeric() % o.getRightConstantNumeric());
            }
        });
        operationsParserMap.put(KEY_OP_IDIV, o -> o.makeConstant(
                (int) Math.floor(
                        o.getLeftConstantNumeric()
                                /
                                o.getRightConstantNumeric()
                )
        ));
        operationsParserMap.put(KEY_OP_POW, o ->{
            if ((KEY_DATA_INT.equals(o.getLeftConstantType()) && KEY_DATA_INT.equals(o.getRightConstantType()))) {
                o.makeConstant((int) Math.pow(
                        o.getLeftConstantNumeric(),
                        o.getRightConstantNumeric()
                ));

            } else {
                o.makeConstant( Math.pow(
                        o.getLeftConstantNumeric(),
                        o.getRightConstantNumeric()
                ));
            }
        });
    }

    public static final Map<String, Consumer<OperationNode>> castsParserMap = new HashMap<>();
    static {
        castsParserMap.put(KEY_DATA_INT, o -> {
            o.setType(KEY_DATA_INT);
            o.makeConstant((int) Double.parseDouble(((OperationNode) o.getMember(0)).constantToken.string)
            );
        });
        castsParserMap.put(KEY_DATA_DOUBLE, o -> {
            // TODO currently this does nothing,
            // also would it even logically do anything?
        });
        castsParserMap.put(KEY_DATA_BOOLEAN, o -> {
            // TODO currently this does nothing
        });
        castsParserMap.put(KEY_DATA_STRING, o -> {
            // TODO currently this does nothing
        });
        castsParserMap.put(KEY_DATA_LET, o -> {
            // TODO currently this does nothing
        });
    }

    // TODO: This should be a nested map or graph, operator -> left -> right
    public static final Map<String, Set<String>> validTypesMap = new HashMap<>();
    static {
        validTypesMap.put(KEY_DATA_LET, new HashSet<>(Arrays.asList(
                KEY_DATA_UNKNOWN, KEY_DATA_BOOLEAN,
                KEY_DATA_DOUBLE, KEY_DATA_STRING,
                KEY_DATA_LET, KEY_DATA_INT
        )));
        validTypesMap.put(KEY_DATA_INT, new HashSet<>(Arrays.asList(
                KEY_DATA_DOUBLE, KEY_DATA_LET
        )));
        validTypesMap.put(KEY_DATA_DOUBLE, new HashSet<>(Arrays.asList(
                KEY_DATA_INT, KEY_DATA_LET
        )));
        validTypesMap.put(KEY_DATA_STRING, new HashSet<>(
                //
        ));
        validTypesMap.put(KEY_DATA_BOOLEAN, new HashSet<>(
                //
        ));
        validTypesMap.put(KEY_DATA_VOID, new HashSet<>(
                //
        ));
        validTypesMap.put(KEY_DATA_UNKNOWN, new HashSet<>(List.of(KEY_DATA_LET)));
    }

    public static final Map<String, Set<String>> implicitCastsMap = new HashMap<>();
    static {
        implicitCastsMap.put(KEY_DATA_INT, new HashSet<>(List.of(
                KEY_DATA_DOUBLE
        )));
    }
}
