package src.evaluators;

import java.util.*;
import src.constants.*;
import src.tokens.*;

import static src.constants.Functions.*;
import static src.constants.Keywords.*;

public class ForLoopNode extends EvaluatorNode {

    VariableNode initial;
    OperationNode condition;
    AssignmentNode updater;
    ScopeNode scope;
    private boolean isExpectingOpeningBracket = true;

    public ForLoopNode(Token token, int depth) {
        super(token, depth);
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree) throws Exception {
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
                if (!Functions.equals(KEY_BRACKET_OPEN, previousToken)) {
                    if (isVariableName(previousToken) && Functions.equals(KEY_OP_ASSIGN, token)) {
                        initial = new AssignmentNode(previousToken, depth + 1);
                        members.add(initial.evaluate(tokenList, evaluatorTree));

                    } else if (isDeclaratorAmbiguous(previousToken)) {
                        if (isVariableName(token)) {
                            // VARIABLE DECLARATION
                            initial = new DeclarationNode(previousToken.string, token, depth + 1);
                            members.add(initial.evaluate(tokenList, evaluatorTree));
                        } else {
                            throw new Exception("Unexpected token \"%s\" in for loop initial variable declaration at line %s".formatted(token, token.line));
                        }
                    } else if (!isDeclaratorAmbiguous(token)) {
                        throw new Exception("Unexpected token \"%s\" in for loop initial at line %s".formatted(token, token.line));
                    }
                    System.out.println(indent + " Created initial " + initial);
                }
            } else if (condition == null) {
                List<Token> operationTokens = new ArrayList<>();
                operationTokens.add(token);

                while (!Functions.equals(KEY_SEMICOLON, operationTokens.getLast())) {
                    Token currentToken = tokenList.removeFirst();
                    operationTokens.add(currentToken);
                }
                condition = new OperationNode(this.token, depth + 1);
                members.add(condition.evaluate(operationTokens, evaluatorTree));

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
                            updater = new AssignmentNode(variableName, depth + 1);
                            members.add(updater.evaluate(operationTokens, evaluatorTree));
                            break;

                        } else if (!isWhiteSpace(opToken)) {
                            throw new Exception("Malform update expression on for loop from token \"%s\" at line %s".formatted(token, token.line));
                        }
                    }

                } else {
                    throw new Exception("Malform update expression on for loop from token \"%s\" at line %s".formatted(token, token.line));
                }

            } else if (Functions.equals(KEY_CURLY_OPEN, token)) {
                scope = new ScopeNode(this.token, depth + 1, true);
                members.add(scope.evaluate(tokenList, evaluatorTree));
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
