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

public class OperationEvaluatorNode extends EvaluatorNode {

    public String type = KEY_OP_TYPE_CONSTANT;
    public Token constantToken = null;
    public EvaluatorNode leftSide = null;
    public EvaluatorNode rightSide = null;
    public List<OperationBracketEvaluatorNode> bracketOperations = new ArrayList<>();
    // this list MUST always end with a semicolon token, including generated ones
    // all operations, including suboperations, are parsed when a semicolon is detected
    public List<Token> operationTokens = new ArrayList<>();
    public boolean isReturnOperation;

    public OperationEvaluatorNode(Token token, int depth) {
        super(token, depth);
    }

    public OperationEvaluatorNode(Token token, int depth, boolean isReturnOperation) {
        super(token, depth);

        this.isReturnOperation = true;
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, Evaluator evaluator) throws Exception {
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

                        FunctionCallEvaluatorNode functionCallEvaluatorNode = new FunctionCallEvaluatorNode(previousToken, depth + 1);
                        functionCallEvaluatorNode.evaluate(tokenList, evaluator);

                        FunctionCallToken functionCallToken = new FunctionCallToken(functionCallEvaluatorNode.token.string, token.line, functionCallEvaluatorNode);
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

                        System.out.printf(indent + "%s order : %s%n", currentOperationToken, currentOrder);

                        // start bracket
                        if (currentOrder == -4) {
                            OperationBracketEvaluatorNode bracketOperation = new OperationBracketEvaluatorNode(new Token("b_" + this.token, this.token.line), depth + 1, i);
                            bracketOperation.evaluate(operationTokens, orders, evaluator);
                            bracketOperations.add(bracketOperation);
                        }

                        // because parentheses are constants
                        if (orderIsConstant(currentOrder)) {
                            constantFound = true;
                        }
                        // this is checking for unary operators,
                        // if two constants are side by side it just breaks (as it should)
                        // if the operators come before any constants, they should not be counted as binary
                        orders.set(i, !(previousOrder > -1 && currentOrder > -1 || !constantFound) ? currentOrder : -2);

                        // if it is an operator, then set the order
                        if (orders.get(i) >= 0 && (largestOrder == -1 || orders.get(i) >= largestOrder)) {
                            largestOrder = orders.get(i);
                            largestOrderIndex = i;

                        } else if (orders.get(i) == -2 && largestOrderIndex == -1) {
                            largestOrder = orders.get(i);
                            largestOrderIndex = i;
                        }

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
                            leftSide = new OperationEvaluatorNode(new Token("l_" + this.token, this.token.line), depth + 1);
                            members.add(leftSide.evaluate(left, evaluator));
                        }

                        if (right.size() > 1) {
                            rightSide = new OperationEvaluatorNode(new Token("r_" + this.token, this.token.line), depth + 1);
                            members.add(rightSide.evaluate(right, evaluator));

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
                            members.add(bracketToken.getOperationEvaluator());
                            return bracketToken.getOperationEvaluator();

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
                            bracketToken.getOperationEvaluator().type = type;
                            members.add(bracketToken.getOperationEvaluator());

                            return bracketToken.getOperationEvaluator();

                        } else {
                            constantToken = newConstantToken;
                            return this;
                        }
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
            // evaluate operators
            else if (isOperator(token)) {
                operationTokens.add(token);
            }
            // evaluate the rest
            else {
                operationTokens.add(token);
            }

            previousToken = token;
        }
        return this;
    }

    public boolean isEmpty() {
        return constantToken == null && type.equals(KEY_OP_TYPE_CONSTANT);
    }

    @Override
    public String toString() {

        String out = "";

        if (isEmpty()) {
            return "group";
        }

        if (isReturnOperation) {
            out += "return ";
        }

        if (rightSide == null || leftSide == null)
            return out + "%s%s".formatted(type.equals(KEY_OP_TYPE_GROUP) ? "group" : !type.equals(KEY_OP_TYPE_CONSTANT) ? "unary operator " + type + " " : "", "const " + constantToken);

        return out + "%s %s".formatted("operator", type.equals(KEY_OP_TYPE_CONSTANT) ? "" : type);
    }
}