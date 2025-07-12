package src.evaluators;

import java.util.*;

import src.constants.Functions;
import src.tokens.*;
import static src.constants.Functions.*;
import static src.constants.Keywords.*;

public class ForLoopEvaluatorNode extends EvaluatorNode {

    VariableEvaluatorNode initial;
    OperationEvaluatorNode condition;
    AssignmentEvaluatorNode updater;
    ScopeEvaluatorNode scope;
    private boolean isExpectingOpeningBracket = true;

    public ForLoopEvaluatorNode(Token token, int depth) {
        super(token, depth);
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, Evaluator evaluator) throws Exception {
        String indent = " ".repeat(depth);

        Token previousToken = null;

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();

            System.out.printf(indent + "for loop : %s%n", token);

            if (isWhiteSpace(token)) {
                continue;

            } else if (isExpectingOpeningBracket) {
                if (Functions.equals(KEY_BRACKET_OPEN, token)) {
                    isExpectingOpeningBracket = false;

                } else {
                    throw new Exception("Unexpected token \"%s\" in for loop at line %s".formatted(token, token.line));
                }
            } else if (initial == null) {
                if (isVariableName(previousToken) && Functions.equals(KEY_OP_ASSIGN, token)) {
                    initial = new AssignmentEvaluatorNode(token, depth + 1);
                    initial.evaluate(tokenList, evaluator);
                    members.add(initial);

                } else if (Functions.equals(KEY_LET, token)) {
                    initial = new DeclarationEvaluatorNode(token, depth + 1);
                    initial.evaluate(tokenList, evaluator);
                    members.add(initial);

                } else if (!isVariableName(token)) {
                    throw new Exception("Unexpected tokens \"%s\" in for loop at line %s".formatted(token, token.line));
                }
            } else if (condition == null) {
                List<Token> operationTokens = new ArrayList<>();
                operationTokens.add(token);

                while (!Functions.equals(KEY_SEMICOLON, operationTokens.getLast())) {
                    Token currentToken = tokenList.removeFirst();
                    operationTokens.add(currentToken);
                }
                condition = new OperationEvaluatorNode(this.token, depth + 1);
                condition.evaluate(operationTokens, evaluator);
                members.add(condition);

            } else if (updater == null) {
                List<Token> operationTokens = new ArrayList<>();
                operationTokens.add(token);

                while (!Functions.equals(KEY_BRACKET_CLOSE, operationTokens.getLast())) {
                    Token currentToken = tokenList.removeFirst();
                    operationTokens.add(currentToken);
                }
                // remove last bracket
                operationTokens.removeLast();

                if (isVariableName(operationTokens.getFirst())) {
                    Token variableName = operationTokens.removeFirst();

                    while (true) {
                        Token opToken = operationTokens.removeFirst();

                        if (Functions.equals(KEY_OP_ASSIGN, opToken)) {
                            operationTokens.add(new Token(KEY_SEMICOLON, token.line));
                            updater = new AssignmentEvaluatorNode(variableName, depth + 1);
                            updater.evaluate(operationTokens, evaluator);
                            members.add(updater);
                            break;

                        } else if (!isWhiteSpace(opToken)) {
                            throw new Exception("Malform update expression on for loop from token \"%s\" at line %s".formatted(token, token.line));
                        }
                    }

                } else {
                    throw new Exception("Malform update expression on for loop from token \"%s\" at line %s".formatted(token, token.line));
                }

            } else if (Functions.equals(KEY_CURLY_OPEN, token)) {
                scope = new ScopeEvaluatorNode(this.token, depth + 1, true);
                scope.evaluate(tokenList, evaluator);
                members.add(scope);
                return this;

            } else {
                throw new Exception("Unexpected token \"%s\" in for loop at line %s".formatted(token, token.line));
            }
        previousToken = token;
        }
        throw new Exception("Unexpected end of file");
    }

    @Override
    public String toString() {
        return "for loop #" + hashCode();
    }
}
