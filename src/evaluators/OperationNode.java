package src.evaluators;

import java.util.*;
import src.constants.*;
import src.tokens.*;

import static src.constants.Functions.*;
import static src.constants.Keywords.*;

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
 * @author ElectricGun
 */

public class OperationNode extends EvaluatorNode {

    String type = KEY_OP_TYPE_CONSTANT;
    String operator = "";

    public Token constantToken = null;
    public List<OperationBracketNode> bracketOperations = new ArrayList<>();
    // this list MUST always end with a semicolon token, including generated ones
    // all operations, including suboperations, are parsed when a semicolon is detected
    public List<Token> operationTokens = new ArrayList<>();
    public boolean isReturnOperation;

    private OperationNode leftSide = null;
    private OperationNode rightSide = null;

    public OperationNode(Token token, int depth) {
        super(token, depth);
    }

    public OperationNode(Token token, int depth, boolean isReturnOperation) {
        super(token, depth);

        this.isReturnOperation = true;
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

    public OperationNode getRightSide() {
        return this.rightSide;
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

    public void setLeftSide(OperationNode leftSide) {
        if (members.contains(this.leftSide)) {
            members.set(members.indexOf(this.leftSide), leftSide);

        } else {
            members.addFirst(leftSide);
        }
        this.leftSide = leftSide;
    }

    public void setRightSide(OperationNode rightSide) {
        if (members.contains(this.rightSide)) {
            members.set(members.indexOf(this.rightSide), rightSide);

        } else {
            members.addLast(rightSide);
        }
        this.rightSide = rightSide;
    }

    private String getSideConstantTokenString(OperationNode side) {
        if (side != null && !side.isBlank()) {
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

    public int getLeftConstantInteger() {
        return getLeftConstantString() != null ?
                (int) Double.parseDouble(getLeftConstantString()) : null;
    }

    public int getRightConstantInteger() {
        return getRightConstantString() != null ?
                (int) Double.parseDouble(getRightConstantString()) : null;
    }

    public String getLeftConstantType() {
        return getValueType(getLeftConstantString());

    }

    public String getRightConstantType() {
        return getValueType(getRightConstantString());
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree) throws Exception {
        String indent = " ".repeat(depth);

        System.out.printf(indent + "Parsing Operation Declaration %s:%n", token);

        Token previousToken = null;

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();

            System.out.printf(indent + "operation : %s : %s%n", this.token, token);

            // evaluate punctuations
            if (token.length() == 1 && isPunctuation(token)) {
                if (isWhiteSpace(token)) {
                    continue;
                }

                // function calls should be evaluated here because they dont change the order of operations
                // and can be regarded as constants
                // store them as class FunctionCallToken

                // TODO IMPLEMENT CASTS
                // casts also should be regarded as unary operators
                // store them as class CastToken

                if (Functions.equals(KEY_BRACKET_OPEN, token)) {
                    if (isVariableName(previousToken)) {
                        System.out.printf(indent + "Parsing function call : prev %s : %s%n", previousToken, token);
                        // remove last token because it will be replaced by a single FunctionCallToken
                        operationTokens.removeLast();

                        FunctionCallNode functionCallNode = new FunctionCallNode(previousToken, depth + 1);
                        FunctionCallNode evaluated = (FunctionCallNode) functionCallNode.evaluate(tokenList, evaluatorTree);

                        FunctionCallToken functionCallToken = new FunctionCallToken(evaluated.token.string, token.line, evaluated);
                        operationTokens.add(functionCallToken);

                    } else {
                        System.out.println(indent + "bracket found");
                        // see if its an explicit cast
                        // it has to be in this order:
                        // variable name or datatype -> ) -> constant or opening bracket
                        List<Token> castTokens = new ArrayList<>();

                        boolean datatypeFound = false;
                        boolean closeBracketFound = false;
                        boolean constantFound = false;

                        Token datatypeToken = null;
                        Token castConstantToken = null;

                        while (!datatypeFound || !closeBracketFound || !constantFound) {
                            Token currToken = tokenList.removeFirst();
                            castTokens.add(currToken);

                            System.out.println(indent + "parsing cast : " + currToken);

                            if (isWhiteSpace(currToken)) {
                                continue;

                            } else if (!datatypeFound && isDeclaratorAmbiguous(currToken)) {
                                datatypeToken = currToken;
                                datatypeFound = true;
                                System.out.println(indent + "datatype found : " + datatypeToken);

                            } else if (!closeBracketFound && Functions.equals(KEY_BRACKET_CLOSE, currToken)) {
                                closeBracketFound = true;
                                System.out.println(indent + "close bracket found");

                            } else if (!constantFound && (isDeclaratorAmbiguous(currToken) || isNumeric(currToken) || Functions.equals(KEY_BRACKET_OPEN, currToken) || isUnaryOperator(currToken))) {
                                constantFound = true;
                                castConstantToken = currToken;
                                System.out.println(indent + "constant found");

                            } else {
                                tokenList.addAll(0, castTokens);
                                System.out.println(indent + "cancelling cast");
                                break;
                            }
                        }

                        if (!datatypeFound || !closeBracketFound || !constantFound) {
                            operationTokens.add(token);
                            System.out.println(operationTokens);
                        } else {
                            System.out.println(indent + "cast found (" + datatypeToken.string + ")");
                            CastToken castToken = new CastToken(datatypeToken.string, datatypeToken.string, token.line);
                            operationTokens.add(castToken);

                            // add this back because it was only removed as a means of checking
                            tokenList.addFirst(castConstantToken);
                        }
                    }

                } else if (Functions.equals(KEY_BRACKET_CLOSE, token)) {
                    operationTokens.add(token);
                }

                // entire operations are evaluated after a semicolon is detected
                else if (Functions.equals(KEY_SEMICOLON, token)) {
                    System.out.printf(indent + "operation : %s tokens : %s%n", this.token, operationTokens);
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
                            throw new Exception("Reserved keyword \"%s\" found on operation at line %s".formatted(currentOperationToken, token.line));
                        }

                        int currentOrder = operationOrder(currentOperationToken);

                        // start bracket
                        if (currentOrder == -4) {
                            OperationBracketNode bracketOperation = new OperationBracketNode(new Token("b_" + this.token, this.token.line), depth + 1, i);
                            bracketOperations.add((OperationBracketNode) bracketOperation.evaluate(operationTokens, orders, evaluatorTree));
                        }

                        // because parentheses are constants
                        if (orderIsConstant(currentOrder) && !(currentOperationToken instanceof CastToken)) {
                            constantFound = true;
                        }

                        // this is checking for unary operators,
                        // if its an explicit cast
                        if (currentOperationToken instanceof CastToken) {
                            orders.set(i, -2);
                        }
                        // if two constants are side by side it just breaks (as it should)
                        else if ((!(previousOrder > -1 && currentOrder > -1 || !constantFound)) && !(previousOrder == -2 && currentOrder > -1)) {
                            orders.set(i, currentOrder);
                        }
                        // if the operators come before any constants, they should not be counted as binary
                        else {
                            orders.set(i, -2);
                        }

                        // if it is an operator, then set the order
                        if (orders.get(i) >= 0 && (largestOrder == -1 || orders.get(i) >= largestOrder)) {
                            // special case for exponents, because they supersede unary operators
                            if (largestOrder != -2 || orders.get(i) != 0) {
                                largestOrder = orders.get(i);
                                largestOrderIndex = i;
                            }

                        } else if (orders.get(i) == -2 && largestOrderIndex == -1) {
                            largestOrder = orders.get(i);
                            largestOrderIndex = i;
                        }

                        System.out.printf(indent + "%s order : %s : %s %n", currentOperationToken, largestOrder, orders.get(i));

                        previousOrder = currentOrder;
                    }

                    if (operationTokens.isEmpty()) {
                        constantToken = new VoidToken("void", token.line);
                        return this;
                    }

                    System.out.printf(indent + "operation tokens post : %s : %s%n", this.token, operationTokens);
                    System.out.printf(indent + "operation orders : %s : %s%n", this.token, orders);

                    // if amount of elements > 2
                    // for binary operations
                    if (orders.size() > 2 && largestOrder != -1) {
                        Token largestOp = operationTokens.get(largestOrderIndex);

                        if (largestOp instanceof CastToken castToken) {
                            type = KEY_OP_TYPE_CAST;
                            operator = castToken.getType();
                        } else {
                            type = KEY_OP_TYPE_OPERATION;
                            operator = largestOp.string;
                        }

                        List<Token> left = new ArrayList<>(operationTokens.subList(0, largestOrderIndex));
                        List<Token> right = new ArrayList<>(operationTokens.subList(largestOrderIndex + 1, operationTokens.size()));

                        left.add(new Token(";", operationTokens.getLast().line));
                        right.add(new Token(";", operationTokens.getLast().line));

                        if (left.size() > 1) {
                            OperationNode op = new OperationNode(new Token("l_" + this.token, this.token.line), depth + 1);
                            setLeftSide((OperationNode) op.evaluate(left, evaluatorTree));
                        }

                        if (right.size() > 1) {
                            OperationNode op = new OperationNode(new Token("r_" + this.token, this.token.line), depth + 1);
                            setRightSide((OperationNode) op.evaluate(right, evaluatorTree));

                        } else {
                            throw new Exception("Unexpected token on operation %s, \"%s\" at line %s".formatted(this.token, token, token.line));
                        }
                        return this;
                    }
                    // if it only has a -1 or -4
                    // for constant values
                    else if (orders.size() == 1 && orderIsConstant(orders.get(0))) {
                        Token newConstantToken = operationTokens.removeFirst();

                        if (newConstantToken instanceof BracketToken bracketToken) {
                            setLeftSide(bracketToken.getOperationEvaluator());
                            return this;

                        } else {
                            constantToken = newConstantToken;
                            return this;
                        }
                    }
                    // if it has -1 or -4 on the right and a -2 operator on the left
                    // for unary operators
                    else if (orders.size() == 2 && orderIsConstant(orders.get(1)) && orders.get(0) == -2) {

                        Token unaryOp = operationTokens.removeFirst();

                        if (unaryOp instanceof CastToken castToken) {
                            type = KEY_OP_TYPE_CAST;
                            operator = castToken.getType();

                        } else {
                            type = KEY_OP_TYPE_OPERATION;
                            operator =  unaryOp.string;
                        }
                        Token newConstantToken = operationTokens.removeFirst();

                        if (newConstantToken instanceof BracketToken bracketToken) {
                            setLeftSide(bracketToken.getOperationEvaluator());

                        } else {
                            OperationNode op = new OperationNode(this.token, depth + 1);
                            op.constantToken = newConstantToken;
                            setLeftSide(op);
                        }
                        return this;
                    }
                    // if all the values are -1 or -4 or -2 then funny error
                    else {
                        StringBuilder out = new StringBuilder();
                        for (Token operationToken : operationTokens)
                            out.append(" ").append(operationToken.string);

                        throw new Exception("Invalid operation %s \"%s\" at line %s".formatted(this.token, out +"...", token.line));
                    }
                } else {
                    throw new Exception("Unexpected token on operation %s, \"%s\" at line %s".formatted(this.token, token, token.line));
                }
            }
            else {
                operationTokens.add(token);
            }

            previousToken = token;
        }
        throw new Exception("Unexpected end of file");
    }

    public boolean isBlank() {
        return constantToken == null && type.equals(KEY_OP_TYPE_CONSTANT);
    }

    public boolean isConstant() {
        return Functions.equals(KEY_OP_TYPE_CONSTANT, type);
    }

    public boolean isGroup() {
        return Functions.equals(KEY_OP_TYPE_GROUP, type);
    }

    public void makeConstant(String newString) {
        this.constantToken = new Token(newString, this.token.line);
        this.type = KEY_OP_TYPE_CONSTANT;
        setLeftSide(null);
        setRightSide(null);
        this.members.clear();
    }

    public void makeConstant(Double newNumeric) {
        makeConstant(String.valueOf(newNumeric));
    }

    public void makeConstant(int newInt) {
        makeConstant(String.valueOf(newInt));
    }

    public boolean isUnary() {
        return (getLeftSide() == null || getRightSide() == null) && members.size() == 1;
    }

    public OperationNode asBinaryFromMember(int memberIndex) {
        OperationNode newOp = new OperationNode(this.token, depth);

        OperationNode memberChild = (OperationNode) this.getMember(memberIndex);
        memberChild.depth += 1;
        newOp.operator = KEY_OP_MUL;
        newOp.type = KEY_OP_TYPE_OPERATION;
        newOp.setLeftSide(memberChild);

        OperationNode factorConstant = new OperationNode(this.token, depth + 1);
        factorConstant.constantToken = new Token(this.operator.equals(KEY_OP_SUB) ? "-1" : "1", this.token.line);
        newOp.setRightSide(factorConstant);

        return newOp;
    }

    public boolean isCast() {
        return Functions.equals(KEY_OP_TYPE_CAST, type);
    }

    @Override
    public String toString() {
        //TODO fix this stupid thing
        String out = "";

        if (Functions.equals(KEY_OP_TYPE_CAST, type)) {
            return "unary cast(\"" + operator + "\")";
        }

        if (isBlank()) {
            return "empty";
        }

        if (isReturnOperation) {
            out = "return ";
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