package mily.constants;

import mily.parsing.*;
import mily.structures.structs.*;
import mily.tokens.*;

import java.util.*;
import java.util.function.*;

import static mily.constants.Keywords.*;

/**
 * <h1> Class Maps </h1>
 * Lists and maps of keywords
 *
 * @author ElectricGun
 * @see Keywords
 */

@SuppressWarnings("unused")
public class Maps {

    public final static OperationMap operationMap = new OperationMap();
    protected final static List<String> PUNCTUATION_KEYS = new ArrayList<>(Arrays.asList(
            KEY_BRACKET_OPEN,
            KEY_BRACKET_CLOSE,
            KEY_CURLY_OPEN,
            KEY_CURLY_CLOSE,
            KEY_SEMICOLON,
            KEY_COMMA,
            KEY_NEWLINE,
            KEY_SPACE,
            KEY_TAB,
            KEY_COMMENT_MULTILINE_START,
            KEY_COMMENT_MULTILINE_END,
            KEY_COMMENT_INLINE,
            KEY_COLON,
            KEY_MACRO_LITERAL,
            KEY_HASH,
            KEY_SYMBOL_IDENTIFIER,
            KEY_ESCAPE,
            KEY_SPEECH_MARK,
            KEY_TEMPLATE_RETURNS
    ));
    protected final static List<String> KEYWORD_KEYS = new ArrayList<>(Arrays.asList(
            KEY_RETURN,
            KEY_CONDITIONAL_IF,
            KEY_CONDITIONAL_ELSE,
            KEY_LOOPING_WHILE,
            KEY_LOOPING_FOR,
            KEY_RAW,
            KEY_INCLUDE
//            KEY_BREAK,
//            KEY_CONTINUE,
//            KEY_LOOPING_REPEAT,
//            KEY_SECTION,
//            KEY_END_SECTION,
//            KEY_ENDIF,
//            KEY_MEM,
//            KEY_LOCAL
    ));
    protected final static List<String> DECLARATOR_KEYS = new ArrayList<>(Arrays.asList(
//            KEY_DATA_DYNAMIC,
            KEY_DATA_DOUBLE,
            KEY_DATA_INT,
            KEY_DATA_STRING,
            KEY_DATA_BOOLEAN,
            KEY_DATA_VOID
    ));
    protected final static List<String> OPERATOR_KEYS = new ArrayList<>(Arrays.asList(
            KEY_OP_ASSIGN,
            KEY_OP_MUL,
            KEY_OP_ADD,
            KEY_OP_SUB,
            KEY_OP_DIV,
            KEY_OP_POW,
            KEY_OP_IDIV,
            KEY_OP_MOD,
            KEY_OP_EQUALS,
            KEY_OP_STRICT_EQUALS,
            KEY_OP_NOT_EQUAL,
            KEY_OP_AND,
            KEY_OP_LESS_THAN,
            KEY_OP_LESS_THAN_EQUALS,
            KEY_OP_GREATER_THAN,
            KEY_OP_GREATER_THAN_EQUALS,
            KEY_BOP_SHIFT_LEFT,
            KEY_BOP_SHIFT_RIGHT,
            KEY_BOP_B_AND,
            KEY_BOP_XOR,
            KEY_BOP_OR,
            KEY_OP_NEGATE
    ));
    protected final static List<String> UNARY_OPERATOR_KEYS = new ArrayList<>(Arrays.asList(
            KEY_OP_ADD,
            KEY_OP_SUB
    ));
    protected final static List<String> PUNC_OPERATOR_KEYWORDS = new ArrayList<>();
    protected final static List<String> BOOLEAN_KEYS = new ArrayList<>(Arrays.asList(
            KEY_BOOLEAN_TRUE,
            KEY_BOOLEAN_FALSE
    ));
    // todo: should make this a 2 way graph for brevity
    protected final static HashMap<String, String> OP_NEGATION_MAP = new HashMap<>();
    protected final static HashMap<String, Integer> PEMDAS = new HashMap<>();

    static {
        OPERATOR_KEYS.addAll(UNARY_OPERATOR_KEYS);
    }

    static {
        PUNC_OPERATOR_KEYWORDS.addAll(PUNCTUATION_KEYS);
        PUNC_OPERATOR_KEYWORDS.addAll(KEYWORD_KEYS);
        PUNC_OPERATOR_KEYWORDS.addAll(OPERATOR_KEYS);
        PUNC_OPERATOR_KEYWORDS.addAll(DECLARATOR_KEYS);
    }

    static {
        OP_NEGATION_MAP.put(KEY_OP_GREATER_THAN, KEY_OP_LESS_THAN_EQUALS);
        OP_NEGATION_MAP.put(KEY_OP_GREATER_THAN_EQUALS, KEY_OP_LESS_THAN);
        OP_NEGATION_MAP.put(KEY_OP_LESS_THAN, KEY_OP_GREATER_THAN_EQUALS);
        OP_NEGATION_MAP.put(KEY_OP_LESS_THAN_EQUALS, KEY_OP_GREATER_THAN);
        OP_NEGATION_MAP.put(KEY_OP_EQUALS, KEY_OP_NOT_EQUAL);
        OP_NEGATION_MAP.put(KEY_OP_NOT_EQUAL, KEY_OP_EQUALS);
    }

    // todo: i should put these somewhere else and clean it up a bit

    static {
        // TODO this system is not good, a separate ordering for unary operators can be nice, and negatives are too arbitrary.
        // negatives are reserved for special characters

        PEMDAS.put(KEY_BRACKET_OPEN, -4);
        PEMDAS.put(KEY_BRACKET_CLOSE, -3);
        // -2 is reserved for unary operators
        PEMDAS.put(KEY_OP_NEGATE, -2);
        // -1 is reserved for constants (default value)
        PEMDAS.put(KEY_OP_POW, 0); // 0 is the highest priority, supersedes unary operators (special case)
        PEMDAS.put(KEY_OP_MUL, 1);
        PEMDAS.put(KEY_OP_DIV, 1);
        PEMDAS.put(KEY_OP_IDIV, 1);
        PEMDAS.put(KEY_OP_MOD, 1);
        PEMDAS.put(KEY_OP_ADD, 2);
        PEMDAS.put(KEY_OP_SUB, 2);
        PEMDAS.put(KEY_BOP_SHIFT_LEFT, 3);
        PEMDAS.put(KEY_BOP_SHIFT_RIGHT, 3);
        PEMDAS.put(KEY_OP_LESS_THAN, 4);
        PEMDAS.put(KEY_OP_LESS_THAN_EQUALS, 4);
        PEMDAS.put(KEY_OP_GREATER_THAN, 4);
        PEMDAS.put(KEY_OP_GREATER_THAN_EQUALS, 4);
        PEMDAS.put(KEY_OP_EQUALS, 5);
        PEMDAS.put(KEY_OP_NOT_EQUAL, 5);
        PEMDAS.put(KEY_BOP_B_AND, 6);
        PEMDAS.put(KEY_BOP_XOR, 7);
        PEMDAS.put(KEY_BOP_OR, 8);
        PEMDAS.put(KEY_OP_AND, 9);
        PEMDAS.put(KEY_OP_OR, 10);
    }

    static {

        // TODO looks quite horrible rn
        // TODO add more operators

        // -------- Binary Operators --------

        // addition
        Consumer<OperationNode> addConsumer = o -> {
            if ((KEY_DATA_INT.equals(o.getLeftTokenType()) && KEY_DATA_INT.equals(o.getRightTokenType()))) {
                o.makeConstant((int) (o.getLeftConstantNumeric() + o.getRightConstantNumeric()));
            } else {
                o.makeConstant(o.getLeftConstantNumeric() + o.getRightConstantNumeric());
            }
        };
        addGenericNumericOperation(KEY_OP_ADD, addConsumer);

        // subtraction
        Consumer<OperationNode> subConsumer = o -> {
            if ((KEY_DATA_INT.equals(o.getLeftTokenType()) && KEY_DATA_INT.equals(o.getRightTokenType()))) {
                o.makeConstant((int) (o.getLeftConstantNumeric() - o.getRightConstantNumeric()));

            } else {
                o.makeConstant(o.getLeftConstantNumeric() - o.getRightConstantNumeric());
            }
        };
        addGenericNumericOperation(KEY_OP_SUB, subConsumer);

        // multiplication
        Consumer<OperationNode> mulConsumer = o -> {
            if ((KEY_DATA_INT.equals(o.getLeftTokenType()) && KEY_DATA_INT.equals(o.getRightTokenType()))) {
                o.makeConstant((int) (o.getLeftConstantNumeric() * o.getRightConstantNumeric()));

            } else {
                o.makeConstant(o.getLeftConstantNumeric() * o.getRightConstantNumeric());
            }
        };
        addGenericNumericOperation(KEY_OP_MUL, mulConsumer);

        // division
        Consumer<OperationNode> divConsumer = o ->
                o.makeConstant(o.getLeftConstantNumeric() / o.getRightConstantNumeric());

        addNumericOperationToDouble(KEY_OP_DIV, divConsumer);

        // modulo
        Consumer<OperationNode> modConsumer = o -> {
            if ((KEY_DATA_INT.equals(o.getLeftTokenType()) && KEY_DATA_INT.equals(o.getRightTokenType()))) {
                o.makeConstant((int) (o.getLeftConstantNumeric() % o.getRightConstantNumeric()));

            } else {
                o.makeConstant(o.getLeftConstantNumeric() % o.getRightConstantNumeric());
            }
        };
        addGenericNumericOperation(KEY_OP_MOD, modConsumer);

        // integer division
        Consumer<OperationNode> intdivConsumer = o -> o.makeConstant(
                (int) Math.floor(
                        o.getLeftConstantNumeric()
                                /
                                o.getRightConstantNumeric()
                )
        );
        addNumericOperationToInt(KEY_OP_IDIV, intdivConsumer);

        // power
        Consumer<OperationNode> powConsumer = o -> {
            if ((KEY_DATA_INT.equals(o.getLeftTokenType()) && KEY_DATA_INT.equals(o.getRightTokenType()))) {
                o.makeConstant((int) Math.pow(
                        o.getLeftConstantNumeric(),
                        o.getRightConstantNumeric()
                ));

            } else {
                o.makeConstant(Math.pow(
                        o.getLeftConstantNumeric(),
                        o.getRightConstantNumeric()
                ));
            }
        };
        addGenericNumericOperation(KEY_OP_POW, powConsumer);

        // ---- Casts
        Consumer<OperationNode> castToInt = o ->
                o.makeConstant((int) Double.parseDouble(((OperationNode) o.getMember(0)).getConstantToken().string));

        Consumer<OperationNode> castToDouble = o ->
                o.makeConstant(Double.parseDouble(((OperationNode) o.getMember(0)).getConstantToken().string));

        // implicit casts
        operationMap.addOperation(KEY_OP_CAST_IMPLICIT, KEY_DATA_INT, KEY_DATA_DOUBLE, KEY_DATA_DOUBLE, castToDouble);

        // explicit casts
        operationMap.addOperation(KEY_OP_CAST_EXPLICIT, KEY_DATA_INT, KEY_DATA_INT, KEY_DATA_INT, castToInt);
        operationMap.addOperation(KEY_OP_CAST_EXPLICIT, KEY_DATA_DOUBLE, KEY_DATA_DOUBLE, KEY_DATA_DOUBLE, castToDouble);
        operationMap.addOperation(KEY_OP_CAST_EXPLICIT, KEY_DATA_DOUBLE, KEY_DATA_INT, KEY_DATA_INT, castToInt);
        operationMap.addOperation(KEY_OP_CAST_EXPLICIT, KEY_DATA_DOUBLE, KEY_DATA_INT, KEY_DATA_INT, castToInt);

        // comparisons
        addGenericNumericComparison(KEY_OP_LESS_THAN, o ->
                o.makeConstant(o.getLeftConstantNumeric() < o.getRightConstantNumeric()));

        addGenericNumericComparison(KEY_OP_GREATER_THAN, o ->
                o.makeConstant(o.getLeftConstantNumeric() > o.getRightConstantNumeric()));

        addGenericNumericComparison(KEY_OP_LESS_THAN_EQUALS, o ->
                o.makeConstant(o.getLeftConstantNumeric() <= o.getRightConstantNumeric()));

        addGenericNumericComparison(KEY_OP_GREATER_THAN_EQUALS, o ->
                o.makeConstant(o.getLeftConstantNumeric() >= o.getRightConstantNumeric()));

        addGenericNumericComparison(KEY_OP_EQUALS, o ->
                o.makeConstant(Objects.equals(o.getLeftConstantNumeric(), o.getRightConstantNumeric())));

        addGenericNumericComparison(KEY_OP_NOT_EQUAL, o ->
                o.makeConstant(!Objects.equals(o.getLeftConstantNumeric(), o.getRightConstantNumeric())));

        // TODO: this is the same as ==
        addGenericNumericComparison(KEY_OP_STRICT_EQUALS, o ->
                o.makeConstant(Objects.equals(o.getLeftConstantNumeric(), o.getRightConstantNumeric())));

        // boolean
        operationMap.addOperation(KEY_OP_AND, KEY_DATA_BOOLEAN, KEY_DATA_BOOLEAN, KEY_DATA_BOOLEAN, o -> {
        });

        // -------- Unary Operators --------

        Consumer<UnaryToBinaryStruct> baseUnaryConsumer = b -> {
            b.getNewOp().setOperator(KEY_OP_MUL);
            b.getFactor().setConstantToken(new TypedToken(
                    b.getOldOp().getOperator().equals(KEY_OP_SUB) ? "-1" : "1",
                    b.getOldOp().nameToken.source,
                    KEY_DATA_INT,
                    b.getOldOp().nameToken.line
            ));
        };

        operationMap.addUnaryOperationConverter(KEY_OP_ADD, baseUnaryConsumer);
        operationMap.addUnaryOperationConverter(KEY_OP_SUB, baseUnaryConsumer);
    }

    static void addGenericNumericOperation(String operator, Consumer<OperationNode> consumer) {
        operationMap.addOperation(operator, KEY_DATA_INT, KEY_DATA_INT, KEY_DATA_INT, consumer);
        operationMap.addOperation(operator, KEY_DATA_INT, KEY_DATA_DOUBLE, KEY_DATA_DOUBLE, consumer);
        operationMap.addOperation(operator, KEY_DATA_DOUBLE, KEY_DATA_INT, KEY_DATA_DOUBLE, consumer);
        operationMap.addOperation(operator, KEY_DATA_DOUBLE, KEY_DATA_DOUBLE, KEY_DATA_DOUBLE, consumer);
    }

    static void addGenericNumericComparison(String operator, Consumer<OperationNode> consumer) {
        operationMap.addOperation(operator, KEY_DATA_INT, KEY_DATA_INT, KEY_DATA_BOOLEAN, consumer);
        operationMap.addOperation(operator, KEY_DATA_INT, KEY_DATA_DOUBLE, KEY_DATA_BOOLEAN, consumer);
        operationMap.addOperation(operator, KEY_DATA_DOUBLE, KEY_DATA_INT, KEY_DATA_BOOLEAN, consumer);
        operationMap.addOperation(operator, KEY_DATA_DOUBLE, KEY_DATA_DOUBLE, KEY_DATA_BOOLEAN, consumer);
    }

    @SuppressWarnings("SameParameterValue")
    static void addNumericOperationToInt(String operator, Consumer<OperationNode> consumer) {
        operationMap.addOperation(operator, KEY_DATA_INT, KEY_DATA_INT, KEY_DATA_INT, consumer);
        operationMap.addOperation(operator, KEY_DATA_INT, KEY_DATA_DOUBLE, KEY_DATA_INT, consumer);
        operationMap.addOperation(operator, KEY_DATA_DOUBLE, KEY_DATA_INT, KEY_DATA_INT, consumer);
        operationMap.addOperation(operator, KEY_DATA_DOUBLE, KEY_DATA_DOUBLE, KEY_DATA_INT, consumer);
    }

    @SuppressWarnings("SameParameterValue")
    static void addNumericOperationToDouble(String operator, Consumer<OperationNode> consumer) {
        operationMap.addOperation(operator, KEY_DATA_INT, KEY_DATA_INT, KEY_DATA_DOUBLE, consumer);
        operationMap.addOperation(operator, KEY_DATA_INT, KEY_DATA_DOUBLE, KEY_DATA_DOUBLE, consumer);
        operationMap.addOperation(operator, KEY_DATA_DOUBLE, KEY_DATA_INT, KEY_DATA_DOUBLE, consumer);
        operationMap.addOperation(operator, KEY_DATA_DOUBLE, KEY_DATA_DOUBLE, KEY_DATA_DOUBLE, consumer);
    }

    static void addBooleanOperator(String operator, Consumer<OperationNode> consumer) {
        operationMap.addOperation(operator, KEY_DATA_BOOLEAN, KEY_DATA_BOOLEAN, KEY_DATA_BOOLEAN, consumer);
    }
}
