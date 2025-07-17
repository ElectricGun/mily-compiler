package src.evaluators;

import java.util.*;
import src.constants.*;
import src.tokens.*;

import static src.constants.Functions.*;
import static src.constants.Keywords.*;

/**
 * <h3> Parses expressions </h3>
 * Parses expressions, such as 1 + 1, x * y, and f(x) > 10. <br>
 * Conditionals / Routes: <br>
 * <ul>
 *     <li> Token ";"          -> return this </li>
 *     <li> String token + "(" -> appends a new FunctionCallEvaluatorNode in a FunctionCallToken into operationTokens </li>
 * </ul>
 * @author ElectricGun
 */

public class OperationNode extends EvaluatorNode {

    public String type = KEY_OP_TYPE_CONSTANT;
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

//        members = new ArrayList<>(Arrays.asList(null, null));
    }

    public OperationNode(Token token, int depth, boolean isReturnOperation) {
        super(token, depth);

        this.isReturnOperation = true;
//        members = new ArrayList<>(Arrays.asList(null, null));
    }

    // TODO bad code, fix later

    public OperationNode getLeftSide() {
//        if (members.isEmpty())
//            return null;
//        return (OperationEvaluatorNode) members.get(0);
        return this.leftSide;
    }

    public OperationNode getRightSide() {
//        if (members.size() < 2)
//            return null;
//        return (OperationEvaluatorNode) members.get(1);
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
//        members.set(0, leftSide);
        if (members.contains(this.leftSide)) {
            members.set(members.indexOf(this.leftSide), leftSide);
        } else {
            members.addFirst(leftSide);
        }
        this.leftSide = leftSide;
    }

    public void setRightSide(OperationNode rightSide) {
//        members.set(1, rightSide);
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

                // TODO IMPLEMENT DATATYPES BEFORE CASTS
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
                        operationTokens.add(token);
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
                        if (orderIsConstant(currentOrder)) {
                            constantFound = true;
                        }
                        // this is checking for unary operators,
                        // if two constants are side by side it just breaks (as it should)
                        // if the operators come before any constants, they should not be counted as binary
                        if ((!(previousOrder > -1 && currentOrder > -1 || !constantFound)) && !(previousOrder == -2 && currentOrder > -1)) {
                            orders.set(i, currentOrder);
                        } else {
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
                        type = operationTokens.get(largestOrderIndex).string;

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
                        type = operationTokens.removeFirst().string;
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

    public void makeConstant(String newConstantValue) {
        this.constantToken = new Token(newConstantValue, this.token.line);
        this.type = KEY_OP_TYPE_CONSTANT;
        setLeftSide(null);
        setRightSide(null);
        this.members.clear();
    }

    public void makeConstant(Double newConstantValueNumeric) {
        makeConstant(String.valueOf(newConstantValueNumeric));
    }

    public boolean isUnary() {
        return (getLeftSide() == null || getRightSide() == null) && members.size() == 1;
    }

    public OperationNode asBinaryFromMember(int memberIndex) {
        OperationNode newOp = new OperationNode(this.token, depth);

        OperationNode memberChild = (OperationNode) this.getMember(memberIndex);
        memberChild.depth += 1;
        newOp.type = KEY_OP_MUL;
        newOp.setLeftSide(memberChild);

        OperationNode factorConstant = new OperationNode(this.token, depth + 1);
        factorConstant.constantToken = new Token(this.type.equals(KEY_OP_SUB) ? "-1" : "1", this.token.line);
        newOp.setRightSide(factorConstant);

        return newOp;
    }

    @Override
    public String toString() {
        //TODO fix this stupid thing
        String out = "";

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
            out += "operation " + (type.equals(KEY_OP_TYPE_CONSTANT) ? "" : type);
        }

        if (getLeftSide() == null || getRightSide() == null) {
            out += type.equals(KEY_OP_TYPE_GROUP) ? "group " : "";
            out += constantToken != null ? "const " + type + " " + constantToken : "";
        }

        return out
//                + " #" + hashCode()
                ;
    }
}