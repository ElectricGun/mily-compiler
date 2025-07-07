package src.evaluators;

import src.tokens.BracketToken;
import src.tokens.Token;

import java.util.*;

import static src.Vars.*;

public class OperationEvaluatorNode extends EvaluatorNode {
    public String type = OP_CONSTANT;
    public String constantValue = "";
    public EvaluatorNode leftSide = null;
    public EvaluatorNode rightSide = null;
    public List<BracketEvaluatorNode> bracketOperations = new ArrayList<>();
    // this list MUST always end with a semicolon token, including generated ones.
    // all operations, including suboperations, are parsed when a semicolon is detected
    public List<Token> operationTokens = new ArrayList<>();

    public OperationEvaluatorNode(Token token, int depth) {
        super(token, depth);
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, Evaluator evaluator) throws Exception {
        String indent = " ".repeat(depth);

        System.out.printf(indent + "Parsing Operation Declaration %s:%n", token);

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();

            System.out.printf(indent + "operation : %s : %s%n", this.token, token);

            // evaluate punctuations
            if (token.length() == 1 && isPunctuation(token.charAt(0))) {
                char c = token.charAt(0);
                if (isWhiteSpace(c)) {
                    continue;
                }

                if (CHAR_BRACKET_OPEN == c || CHAR_BRACKET_CLOSE == c) {
                    operationTokens.add(token);
                }

                // entire operations are evaluated after a semicolon is detected
                else if (CHAR_SEMICOLON == c) {
                    System.out.printf(indent + "operation : %s tokens : %s%n", this.token, operationTokens);
                    List<Integer> orders = new ArrayList<>();

                    for (int i = 0; i < operationTokens.size(); i++) {
                        orders.add(0);
                    }

                    // if largest value is -1 then its probably a constant or something is very wrong
                    int largestOrderIndex = -1;
                    int largestOrder = -1;
                    // -2 means operator without a constant to the left
                    int previousOrder = -2;

                    boolean constantFound = false;
                    for (int i = 0; i < orders.size(); i++) {
                        int currentOrder = operationOrder(operationTokens.get(i));

                        // start bracket
                        if (currentOrder == -4) {
                            BracketEvaluatorNode bracketOperation = new BracketEvaluatorNode(new Token("b_" + this.token, this.token.line), depth + 1, i);
                            bracketOperation.evaluate(operationTokens, orders, evaluator);
                            bracketOperations.add(bracketOperation);

                            currentOrder = -1;
                        }

                        // because parentheses are constants
                        if (isConstant(currentOrder)) {
                            constantFound = true;
                        }
                        // this is checking for unary operators,
                        // if two constants are side by side it just breaks (as it should)
                        // if the operators come before any constants, they should not be counted as binary
                        orders.set(i, !(previousOrder > -1 && currentOrder > -1 || !constantFound) ?
                                currentOrder : -2);

                        // if it is an operator, then set the order
                        if (orders.get(i) >= 0 && (largestOrder == -1 || orders.get(i) >= largestOrder)) {
                            largestOrder = orders.get(i);
                            largestOrderIndex = i;
                        }
                        else if (orders.get(i) == -2 && largestOrderIndex == -1) {
                            largestOrder = orders.get(i);
                            largestOrderIndex = i;
                        }

                        previousOrder = currentOrder;
                    }

                    System.out.printf(indent + "operation orders : %s : %s%n", this.token, orders);

                    // if amount of elements > 2
                    // then its a binary operation,
                    if (orders.size() > 2 && largestOrder != -1) {
                        type = operationTokens.get(largestOrderIndex).string;
                    }
                    // if it only has a -1 or -4 then its a constant value
                    else if (orders.size() == 1 && isConstant(orders.get(0))) {
                        Token constantToken = operationTokens.removeFirst();

                        if (constantToken instanceof BracketToken bracketToken) {
                            return bracketToken.getOperationEvaluator();
                        } else {
                            constantValue = constantToken.string;
                        }
                        return this;
                    }
                    // if it has -1 or -4 on the right and a -2 operator on the left, it's a regular unary - or +
                    else if (orders.size() == 2 && isConstant(orders.get(1)) && orders.get(0) == -2) {
                        type = operationTokens.removeFirst().string;
                        Token constantToken = operationTokens.removeFirst();

                        if (constantToken instanceof BracketToken bracketToken) {
                            bracketToken.getOperationEvaluator().type = type;

                            members.add(bracketToken.getOperationEvaluator());
                            return bracketToken.getOperationEvaluator();

                        } else {
                            constantValue = constantToken.string;
                        }
                        return this;
                    }
                    // if all the values are -1 or -4 or -2 then funny error
                    else {
                        String out = "";
                        for (Token operationToken : operationTokens)
                            out += " " + operationToken.string;

                        throw new Exception("Invalid operation %s \"%s\" at line %s".formatted(this.token, out +"...", token.line));
                    }

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
                    }

                    return this;
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
        }
        return this;
    }

    @Override
    public String toString() {
        if (rightSide == null || leftSide == null)
            return "%s%s".formatted(!type.equals(OP_CONSTANT) ? "unary operator " + type + " " : "", constantValue);
        return "%s %s".formatted( leftSide == null ? constantValue : "operator", type.equals(OP_CONSTANT) ? "" : type);
    }
}