package src.evaluation;

import java.util.*;

import static src.Vars.*;

public class OperationEvaluator extends EvaluatorNode {
    public String type = OP_CONSTANT;
    public String constantValue = "";
    public EvaluatorNode leftSide = null;
    public EvaluatorNode rightSide = null;
    public List<Token> operationTokens = new ArrayList<>();

    public OperationEvaluator(Token name, int depth) {
        super(name, depth);
    }
    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, Evaluator evaluator) throws Exception {
        String indent = " ".repeat(depth);

        System.out.printf(indent + "Parsing Operation Declaration %s:%n", name);

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();

            System.out.printf(indent + "operation : %s : %s%n",name, token);

            // evaluate punctuations
            if (token.length() == 1 && isPunctuation(token.charAt(0))) {
                char c = token.charAt(0);
                if (isWhiteSpace(c)) {
                    continue;
                }

                if (CHAR_SEMICOLON == c) {
                    System.out.printf(indent + "operation : %s tokens : %s%n",name, operationTokens);
                    int[] orders = new int[operationTokens.size()];

                    // if largest value is -1 then its probably a constant or something is very wrong
                    int largestOrderIndex = -1;
                    int largestOrder = -1;
                    // -2 means operator without a constant to the left
                    int previousOrder = -2;

                    boolean constantFound = false;
                    for (int i = 0; i < orders.length; i++) {
                        int currentOrder = operationOrder(operationTokens.get(i));
                        if (currentOrder == -1) {
                            constantFound = true;
                        }
                        // this is checking for unary operators,
                        // if two constants side by side it just breaks (as it should)
                        // if the operators come before any constants, they should not be counted as binary
                        orders[i] = !(previousOrder > -1 && currentOrder > -1 || !constantFound) ?
                                currentOrder : -2;

                        // if it is an operator, then set the order
                        if (orders[i] >= 0 && (largestOrder == -1 || orders[i] >= largestOrder)) {
                            largestOrder = orders[i];
                            largestOrderIndex = i;
                        }
                        else if (orders[i] == -2 && largestOrderIndex == -1) {
                            largestOrder = orders[i];
                            largestOrderIndex = i;
                        }

                        previousOrder = currentOrder;
                    }

                    // if amount of elements > 2
                    // then its a binary operation,
                    if (orders.length > 2 && largestOrder != -1) {
                        type = operationTokens.get(largestOrderIndex).string;
                    }
                    // if it only has a -1 then its a constant value
                    else if (orders.length == 1 && orders[0] == -1) {
                        constantValue = operationTokens.removeFirst().string;
                        return this;
                    }
                    // if it has -1 on the right and a -2 operator on the left, it's a regular unary - or +
                    else if (orders.length == 2 && orders[1] == -1 && orders[0] < -1) {
                        type = operationTokens.removeFirst().string;
                        constantValue = operationTokens.removeFirst().string;
                        return this;
                    }
                    // if all the values are -1 or -2 then funny error
                    else {
                        String out = "";
                        for (Token operationToken : operationTokens)
                            out += " " + operationToken.string;

                        throw new Exception("Invalid operation %s \"%s\" at line %s".formatted(name, out +"...", token.line));
                    }

                    List<Token> left = new ArrayList<>(operationTokens.subList(0, largestOrderIndex));
                    List<Token> right = new ArrayList<>(operationTokens.subList(largestOrderIndex + 1, operationTokens.size()));

                    left.add(new Token(";", operationTokens.getLast().line));
                    right.add(new Token(";", operationTokens.getLast().line));

                    if (left.size() > 1) {
                        leftSide = new OperationEvaluator(new Token("l_" + name, name.line), depth + 1);
                        leftSide.evaluate(left, evaluator);
                        members.add(leftSide);
                    }

                    if (right.size() > 1) {
                        rightSide = new OperationEvaluator(new Token("r_" + name, name.line), depth + 1);
                        rightSide.evaluate(right, evaluator);
                        members.add(rightSide);
                    }

                    return this;
                } else {
                    throw new Exception("Unexpected token on operation %s, \"%s\" at line %s".formatted(name, token, token.line));
                }
            }
            // evaluate operators
            else if (isOperator(token)) {
                // entire operations are evaluated after a semicolon is detected, so this isn't really used
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