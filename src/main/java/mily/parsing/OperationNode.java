package mily.parsing;

import java.util.*;

import mily.parsing.invokes.*;
import mily.tokens.*;

import static mily.constants.Functions.*;
import static mily.constants.Keywords.*;
import static mily.constants.Maps.*;

/**
 * <h1> Class OperationNode </h1>
 * Operation Parser
 * Parses expressions, such as 1 + 1, x * y, and f(x) > 10. <br>
 * Routes:
 * <ul>
 *     <li> {@link OperationNode}</li>
 *     <li> {@link FunctionCallNode}</li>
 *     <li> {@link OperationBracketNode}</li>
 * </ul>
 *
 * @author ElectricGun
 */

public class OperationNode extends EvaluatorNode {

    public List<OperationBracketNode> bracketOperations = new ArrayList<>();
    // this list MUST always end with a semicolon token, including generated ones
    // all operations, including suboperations, are parsed when a semicolon is detected
    public List<Token> operationTokens = new ArrayList<>();
    protected TypedToken constantToken = null;
    protected boolean isReturnOperation;
    protected String type = KEY_OP_TYPE_CONSTANT;
    protected String operator = "";
    private OperationNode leftSide = null;
    private OperationNode rightSide = null;

    public OperationNode(Token token, int depth) {
        super(token, depth);
    }

    public OperationNode(Token token, int depth, boolean isReturnOperation) {
        super(token, depth);

        this.isReturnOperation = isReturnOperation;
    }

    // its private here because it is too dangerous to be used outside of this class
    private static String guessValueType(String s) {
        if (s == null)
            return null;

        if (isInteger(s)) {
            return KEY_DATA_INT;

        } else if (isNumeric(s)) {
            return KEY_DATA_DOUBLE;

        } else if (s.startsWith("\"") && s.endsWith("\"")) {
            return KEY_DATA_STRING;

        } else if (s.equals(KEY_BOOLEAN_FALSE) || s.equals(KEY_BOOLEAN_TRUE)) {
            return KEY_DATA_BOOLEAN;
        }
        return KEY_DATA_UNKNOWN;
    }

    public boolean isReturnOperation() {
        return isReturnOperation;
    }

    public void setReturnOperation(boolean returnOperation) {
        isReturnOperation = returnOperation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    // TODO bad code, fix later
    public OperationNode getLeftSide() {
        return this.leftSide;
    }

    public void setLeftSide(OperationNode leftSide) {
        if (members.contains(this.leftSide)) {
            members.set(members.indexOf(this.leftSide), leftSide);

        } else {
//            members.addFirst(leftSide);
            members.add(0, leftSide);
        }
        this.leftSide = leftSide;
    }

    public OperationNode getRightSide() {
        return this.rightSide;
    }

    public void setRightSide(OperationNode rightSide) {
        if (members.contains(this.rightSide)) {
            members.set(members.indexOf(this.rightSide), rightSide);

        } else {
//            members.addLast(rightSide);
            members.add(0, rightSide);
        }
        this.rightSide = rightSide;
    }

    @Override
    public void replaceMember(EvaluatorNode replaced, EvaluatorNode replacement) {
        super.replaceMember(replaced, replacement);

        if (leftSide == replaced) {
            leftSide = (OperationNode) replacement;

        } else if (rightSide == replaced) {
            rightSide = (OperationNode) replacement;
        }
    }

    private String getSideConstantTokenString(OperationNode side) {
        if (side != null && !side.isEmptyConstant()) {
            return side.constantToken.string;

        } else {
            return null;
        }
    }

    public String getLeftConstantString() {
        return getSideConstantTokenString(getLeftSide());
    }

    public String getRightConstantString() {
        return getSideConstantTokenString(getRightSide());
    }

    public Double getLeftConstantNumeric() {
        return getLeftConstantString() != null ?
                Double.parseDouble(getLeftConstantString()) : null;
    }

    public Double getRightConstantNumeric() {
        return getRightConstantString() != null ?
                Double.parseDouble(getRightConstantString()) : null;
    }

    public String getLeftTokenType() {
        return getLeftSide().constantToken.getType();
    }

    public String getRightTokenType() {
        return getRightSide().constantToken.getType();
    }

    public TypedToken getConstantToken() {
        return constantToken;
    }

    public void setConstantToken(TypedToken constantToken) {
        this.constantToken = constantToken;
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree) throws Exception {
        String indent = " ".repeat(depth);

        if (evaluatorTree.debugMode)
            System.out.printf(indent + "Parsing Operation Declaration %s:%n", nameToken);

        Token previousToken = null;

        boolean previousIsSymbolIdentifier = false;

        while (!tokenList.isEmpty()) {
            Token token = tokenList.remove(0);

            if (evaluatorTree.debugMode)
                System.out.printf(indent + "operation : %s : %s%n", this.nameToken, token);

            if (!previousIsSymbolIdentifier && token.equalsKey(KEY_SYMBOL_IDENTIFIER)) {
                previousIsSymbolIdentifier = true;

            } else if (previousIsSymbolIdentifier) {
                // Process raw text (symbols)

                if (isVariableOrDeclarator(token)) {
                    operationTokens.add(new TypedToken(token.string, token.source, KEY_DATA_SYMBOL, token.line));
                    previousIsSymbolIdentifier = false;

                } else {
                    return this.throwSyntaxError("Invalid token on @ symbol", token);
                }
            } else if (token.length() == 1 && isPunctuation(token)) {
                // evaluate punctuations

                if (isWhiteSpace(token)) {
                    continue;
                }

                if (token.equalsKey(KEY_SPEECH_MARK)) {
                    // Process STRINGS
                    StringBuilder stringTokenBuffer = new StringBuilder();
                    Token prevStringToken = null;
                    while (true) {
                        if (tokenList.isEmpty()) {
                            return throwSyntaxError("Unclosed string in operation from line " + token.line, token);
                        }
                        Token stringToken = tokenList.remove(0);

                        if (!stringToken.equalsKey(KEY_SPEECH_MARK) || (prevStringToken != null && prevStringToken.equalsKey(KEY_ESCAPE))) {
                            stringTokenBuffer.append(stringToken.string);
                        } else {
                            break;
                        }
                        prevStringToken = stringToken;
                    }

                    operationTokens.add(new TypedToken(stringTokenBuffer.toString(), token.source, KEY_DATA_STRING, token.line));

                } else if (token.equalsKey(KEY_MACRO_LITERAL) && isVariableName(previousToken)) {
                    if (evaluatorTree.debugMode)
                        System.out.printf(indent + "Parsing raw template invoke : prev %s : %s%n", previousToken, token);

                    // remove last token because it will be replaced by a single RawTemplateInvoke
                    operationTokens.remove(operationTokens.size() - 1);

                    RawTemplateInvoke functionCallNode = new RawTemplateInvoke(previousToken.string, previousToken, depth + 1);
                    RawTemplateInvoke evaluated = (RawTemplateInvoke) functionCallNode.evaluate(tokenList, evaluatorTree);

                    CallerNodeToken callerNodeToken = new CallerNodeToken(evaluated.nameToken.string, token.source, token.line, evaluated);
                    operationTokens.add(callerNodeToken);

                } else if (keyEquals(KEY_BRACKET_OPEN, token)) {
                    // function calls should be evaluated here because they don't change the order of operations
                    // and can be regarded as constants
                    // store them as class FunctionCallToken
                    if (isVariableName(previousToken)) {

                        if (evaluatorTree.debugMode)
                            System.out.printf(indent + "Parsing function call : prev %s : %s%n", previousToken, token);

                        // remove last token because it will be replaced by a single FunctionCallToken
                        operationTokens.remove(operationTokens.size() - 1);

                        FunctionCallNode functionCallNode = new FunctionCallNode(previousToken.string, previousToken, depth + 1);
                        FunctionCallNode evaluated = (FunctionCallNode) functionCallNode.evaluate(tokenList, evaluatorTree);

                        CallerNodeToken callerNodeToken = new CallerNodeToken(evaluated.nameToken.string, token.source, token.line, evaluated);
                        operationTokens.add(callerNodeToken);

                    } else {
                        if (evaluatorTree.debugMode)
                            System.out.println(indent + "bracket found");
                        // see if it's an explicit cast
                        // it has to be in this order:
                        // variable name or datatype -> ) -> constant or opening bracket
                        List<Token> castTokens = new ArrayList<>();

                        boolean datatypeFound = false;
                        boolean closeBracketFound = false;
                        boolean constantFound = false;

                        Token datatypeToken = null;
                        Token castConstantToken = null;

                        while (!datatypeFound || !closeBracketFound || !constantFound) {
                            Token currToken = tokenList.remove(0);
                            castTokens.add(currToken);

                            if (evaluatorTree.debugMode)
                                System.out.println(indent + "parsing cast : " + currToken);

                            if (isWhiteSpace(currToken)) {
                                continue;
                            }

                            if (!datatypeFound && isVariableOrDeclarator(currToken)) {
                                datatypeToken = currToken;
                                datatypeFound = true;

                                if (evaluatorTree.debugMode)
                                    System.out.println(indent + "datatype found : " + datatypeToken);

                            } else if (!closeBracketFound && keyEquals(KEY_BRACKET_CLOSE, currToken)) {
                                closeBracketFound = true;
                                if (evaluatorTree.debugMode)
                                    System.out.println(indent + "close bracket found");

                            } else if (!constantFound && (isVariableOrDeclarator(currToken) || isNumeric(currToken) || keyEquals(KEY_BRACKET_OPEN, currToken) || isUnaryOperator(currToken))) {
                                constantFound = true;
                                castConstantToken = currToken;
                                if (evaluatorTree.debugMode)
                                    System.out.println(indent + "constant found");

                            } else {
                                tokenList.addAll(0, castTokens);
                                if (evaluatorTree.debugMode)
                                    System.out.println(indent + "cancelling cast");
                                break;
                            }
                        }

                        if (!datatypeFound || !closeBracketFound || !constantFound) {
                            operationTokens.add(token);
                        } else {
                            return throwSyntaxError("Illegal parenthesis value", token);
//                            if (evaluatorTree.debugMode)
//                                System.out.println(indent + "cast found (" + datatypeToken.string + ")");
//                            CastToken castToken = new CastToken(datatypeToken.string, datatypeToken.string, token.source, token.line);
//                            operationTokens.add(castToken);
//
//                            tokenList.add(0, castConstantToken);
                        }
                    }

                } else if (keyEquals(KEY_BRACKET_CLOSE, token)) {
                    operationTokens.add(token);
                }

                // entire operations are evaluated after a semicolon is detected
                else if (keyEquals(KEY_SEMICOLON, token)) {
                    if (evaluatorTree.debugMode)
                        System.out.printf(indent + "operation : %s tokens : %s%n", this.nameToken, operationTokens);
                    List<Integer> orders = new ArrayList<>();

                    for (int i = 0; i < operationTokens.size(); i++) {
                        orders.add(0);
                    }

                    // if largest value is -1 then it's a constant or something is very wrong
                    int largestOrderIndex = -1;
                    int largestOrder = -1;
                    // -2 means operator without a constant on the left
                    int previousOrder = -2;
                    boolean constantFound = false;

                    // OPERATION EVALUATION LOOP
                    for (int i = 0; i < orders.size(); i++) {

                        Token currentOperationToken = operationTokens.get(i);

                        if (isReserved(currentOperationToken)) {
                            return throwSyntaxError("Illegal keyword found in operation", token);
                        }

                        int currentOrder = operationOrder(currentOperationToken);

                        // start bracket
                        if (currentOrder == -4) {
                            OperationBracketNode bracketOperation = new OperationBracketNode(new Token("b_" + this.nameToken, this.nameToken.source, this.nameToken.line), orders, depth + 1, i);
                            bracketOperations.add((OperationBracketNode) bracketOperation.evaluate(operationTokens, evaluatorTree));
                        }

                        // because parentheses are constants
                        if (orderIsConstant(currentOrder)/* && !(currentOperationToken instanceof CastToken)*/) {
                            constantFound = true;
                        }

                        // this is checking for unary operators,
                        // if it's an explicit cast
//                        if (currentOperationToken instanceof CastToken) {
//                            orders.set(i, -2);
//                        }
                        // if two constants are side by side it just breaks (as it should)
//                        else
                        if ((!(previousOrder > -1 && currentOrder > -1 || !constantFound)) && !(previousOrder == -2 && currentOrder > -1)) {
                            orders.set(i, currentOrder);
                        }
                        // if the operators come before any constants, they should not be counted as binary
                        else {
                            orders.set(i, -2);
                        }

                        // if it is an operator, then set the order
                        if (orders.get(i) >= 0 && (largestOrder == -1 || orders.get(i) >= largestOrder)) {
                            // NOTE: REMOVED
//                            // special case for exponents, because they supersede unary operators
//                            if (largestOrder != -2 || orders.get(i) != 0) {
                                largestOrder = orders.get(i);
                                largestOrderIndex = i;
//                            }

                        } else if (orders.get(i) == -2 && largestOrderIndex == -1) {
                            largestOrder = orders.get(i);
                            largestOrderIndex = i;
                        }

                        if (evaluatorTree.debugMode)
                            System.out.printf(indent + "%s order : %s : %s %n", currentOperationToken, largestOrder, orders.get(i));

                        previousOrder = currentOrder;
                    }

                    boolean noOperators = orders.size() > 1;
                    for (int i = 0; i < orders.size(); i++) {
                        if (orders.get(i) != -1) {
                            noOperators = false;
                        }
                    }

                    if (noOperators) {
                        return throwSyntaxError("Illegal operation consisting of adjacent constants found", nameToken);
                    }

                    if (operationTokens.isEmpty()) {
                        constantToken = new VoidToken(token.source, token.line);
                        return this;
                    }

                    if (evaluatorTree.debugMode) {
                        System.out.printf(indent + "operation tokens post : %s : %s%n", this.nameToken, operationTokens);
                        System.out.printf(indent + "operation orders : %s : %s%n", this.nameToken, orders);
                    }

                    // if amount of elements > 2
                    // for binary operations
                    if (orders.size() > 2 && largestOrder != -1) {
                        Token largestOp = operationTokens.get(largestOrderIndex);

//                        if (largestOp instanceof CastToken castToken) {
//                            type = KEY_OP_CAST_EXPLICIT;
//                            operator = castToken.getType();
//                        } else {
                            type = KEY_OP_TYPE_OPERATION;
                            operator = largestOp.string;
//                        }

                        List<Token> left = new ArrayList<>(operationTokens.subList(0, largestOrderIndex));
                        List<Token> right = new ArrayList<>(operationTokens.subList(largestOrderIndex + 1, operationTokens.size()));

                        Token lastToken = operationTokens.get(operationTokens.size() - 1);
                        left.add(new Token(";", lastToken.source, lastToken.line));
                        right.add(new Token(";", lastToken.source, lastToken.line));

                        if (left.size() > 1) {
                            OperationNode op = new OperationNode(new Token("l_" + this.nameToken, this.nameToken.source, this.nameToken.line), depth + 1);
                            setLeftSide((OperationNode) op.evaluate(left, evaluatorTree));
                        }

                        if (right.size() > 1) {
                            OperationNode op = new OperationNode(new Token("r_" + this.nameToken, this.nameToken.source, this.nameToken.line), depth + 1);
                            setRightSide((OperationNode) op.evaluate(right, evaluatorTree));

                        } else {
                            return throwSyntaxError("Unexpected token in operation", token);
                        }
                        return this;

                        // if it only has a -1 or -4
                        // for constant values
                    } else if (orders.size() == 1 && orderIsConstant(orders.get(0))) {
                        Token newConstantToken = operationTokens.remove(0);

                        if (newConstantToken instanceof BracketToken bracketToken) {
                            setLeftSide(bracketToken.getOperationEvaluator());

                        } else if (newConstantToken instanceof CallerNodeToken callerNodeToken) {
                            constantToken = callerNodeToken;

                        } else if (newConstantToken instanceof TypedToken typedToken && !typedToken.getType().equals(KEY_DATA_UNKNOWN)) {
                            constantToken = typedToken;

                        } else {
                            String guessedValueType = guessValueType(newConstantToken.string);
                            constantToken = TypedToken.fromToken(newConstantToken, guessedValueType);

                            if (guessedValueType.equals(KEY_DATA_UNKNOWN)) {
                                constantToken.setVariableRef(true);
                            }
                        }
                        return this;

                        // if it has -1 or -4 on the right and a -2 operator on the left
                        // for unary operators
                    } else if (orders.size() == 2 && orderIsConstant(orders.get(1)) && orders.get(0) == -2) {

                        Token unaryOperator = operationTokens.remove(0);

//                        if (unaryOperator instanceof CastToken castToken) {
//                            type = KEY_OP_CAST_EXPLICIT;
//                            this.operator = castToken.getType();

//                        } else {
                            type = KEY_OP_TYPE_OPERATION;
                            this.operator = unaryOperator.string;
//                        }
                        Token newConstantToken = operationTokens.remove(0);

                        OperationNode op = new OperationNode(this.nameToken, depth + 1);
                        if (newConstantToken instanceof BracketToken bracketToken) {
                            setLeftSide(bracketToken.getOperationEvaluator());

                        } else if (newConstantToken instanceof CallerNodeToken callerNodeToken) {
                            op.constantToken = callerNodeToken;
                            setLeftSide(op);

                        } else if (newConstantToken instanceof TypedToken typedToken && !typedToken.getType().equals(KEY_DATA_UNKNOWN)) {
                            op.constantToken = typedToken;
                            setLeftSide(op);

                        } else {
                            String guessedValueType = guessValueType(newConstantToken.string);
                            op.constantToken = TypedToken.fromToken(newConstantToken, guessedValueType);
                            setLeftSide(op);

                            if (guessedValueType.equals(KEY_DATA_UNKNOWN)) {
                                op.constantToken.setVariableRef(true);
                            }
                        }
                        return this;
                    }
                    // if all the values are -1 or -4 or -2 then funny error
                    else {
                        return throwSyntaxError("Invalid operation", token);
                    }
                } else {
                    return throwSyntaxError("Unexpected token in operation", token);
                }
            } else {
                operationTokens.add(token);
            }

            previousToken = token;
        }
        return throwSyntaxError("Unexpected end of file", nameToken);
    }

    public boolean isEmptyConstant() {
        return constantToken == null && type.equals(KEY_OP_TYPE_CONSTANT);
    }

    public boolean isConstant() {
        return keyEquals(KEY_OP_TYPE_CONSTANT, type);
    }

    public void makeConstant(String newString) {
        String newTokenType = guessValueType(newString);
        setConstantToken(new TypedToken(newString, this.nameToken.source, newTokenType, this.nameToken.line));
        this.type = KEY_OP_TYPE_CONSTANT;
        setLeftSide(null);
        setRightSide(null);
        this.members.clear();
    }

    public void makeConstant(Double newNumeric) {
        makeConstant(String.valueOf(newNumeric));
    }

    public void makeConstant(boolean bool) {
        makeConstant(bool ? "true" : "false");
    }

    public void makeConstant(int newInt) {
        makeConstant(String.valueOf(newInt));
    }

    public boolean isUnary() {
        return (getLeftSide() == null || getRightSide() == null) && members.size() == 1;
    }

    public boolean isBinary() {
        return !isUnary() && !isConstant();
    }

    public OperationNode asBinaryFromMember(int memberIndex) throws IllegalArgumentException {
        return operationMap.generateBinaryFromUnaryAtMember(this, memberIndex);
    }

//    public boolean isCast() {
//        return keyEquals(KEY_OP_CAST_EXPLICIT, type);
//    }

    @Override
    public String toString() {
        //TODO fix this stupid thing
        String out = "operation ";

        if (isReturnOperation) {
            out += "return ";
        }

//        if (keyEquals(KEY_OP_CAST_EXPLICIT, type)) {
//            return out + "unary cast(\"" + operator + "\")";
//        }

        if (isEmptyConstant()) {
            return out + "empty";
        }

        if (isUnary()) {
            out += "unary ";
        }

        if (!members.isEmpty()) {
            out += "operation " + (type.equals(KEY_OP_TYPE_CONSTANT) ? "" : operator);
        }

        if (getLeftSide() == null || getRightSide() == null) {
            out += type.equals(KEY_OP_TYPE_GROUP) ? "group " : "";
            out += constantToken != null ? "const " + constantToken : "";
        }

        return out;
    }
}