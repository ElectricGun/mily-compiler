package mily.parsing;

import java.util.*;

import mily.tokens.*;

import static mily.constants.Functions.*;
import static mily.constants.Keywords.*;

/**
 * <h1> Class ForLoopNode </h1>
 * For Loops
 * Parsing AST node for for loops.
 *
 * @author ElectricGun
 */

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
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree, boolean debugMode) throws Exception {
        String indent = " ".repeat(depth);

        Token previousToken = null;

        while (!tokenList.isEmpty()) {
            Token token = tokenList.remove(0);
// Token token = tokenList.removeFirst();
            if (debugMode)
                System.out.printf(indent + "for loop : %s%n", token);

            if (isWhiteSpace(token)) {
                continue;

            } else if (isExpectingOpeningBracket) {
                if (keyEquals(KEY_BRACKET_OPEN, token)) {
                    isExpectingOpeningBracket = false;

                } else {
                    return throwSyntaxError("Unexpected token in for loop", token);
                }
            } else if (initial == null) {
                if (!keyEquals(KEY_BRACKET_OPEN, previousToken)) {
                    if (isVariableName(previousToken) && keyEquals(KEY_OP_ASSIGN, token)) {
                        initial = new AssignmentNode(previousToken, depth + 1);
                        members.add(initial.evaluate(tokenList, evaluatorTree, debugMode));

                    } else if (isVariableOrDeclarator(previousToken)) {
                        if (isVariableName(token)) {
                            // VARIABLE DECLARATION
                            initial = new DeclarationNode(previousToken.string, token, depth + 1);
                            members.add(initial.evaluate(tokenList, evaluatorTree, debugMode));
                        } else {
                            return throwSyntaxError("Unexpected token in for loop initial variable declaration", token);
                        }
                    } else if (!isVariableOrDeclarator(token)) {
                        return throwSyntaxError("Unexpected token in for loop initial", token);
                    }
                    if (debugMode)
                        System.out.println(indent + " Created initial " + initial);
                }
            } else if (condition == null) {
                List<Token> operationTokens = new ArrayList<>();
                operationTokens.add(token);

//                while (!keyEquals(KEY_SEMICOLON, operationTokens.getLast())) {
//                    Token currentToken = tokenList.removeFirst();
                while (!keyEquals(KEY_SEMICOLON, operationTokens.get(operationTokens.size() - 1))) {
                    Token currentToken = tokenList.remove(0);
                    operationTokens.add(currentToken);
                }
                condition = new OperationNode(this.nameToken, depth + 1);
                members.add(condition.evaluate(operationTokens, evaluatorTree, debugMode));

            } else if (updater == null) {
                List<Token> operationTokens = new ArrayList<>();
                operationTokens.add(token);

//                while (!keyEquals(KEY_BRACKET_CLOSE, operationTokens.getLast())) {
//                    Token currentToken = tokenList.removeFirst();
                while (!keyEquals(KEY_BRACKET_CLOSE, operationTokens.get(operationTokens.size() - 1))) {
                    Token currentToken = tokenList.remove(0);
                    operationTokens.add(currentToken);
                }
                // remove last bracket
//                operationTokens.removeLast();
                operationTokens.remove(operationTokens.size() - 1);

//                if (isVariableName(operationTokens.getFirst())) {
//                    Token variableName = operationTokens.removeFirst();
//
//                    while (true) {
//                        Token opToken = operationTokens.removeFirst();

                if (isVariableName(operationTokens.get(0))) {
                    Token variableName = operationTokens.remove(0);

                    while (true) {
                        Token opToken = operationTokens.remove(0);

                        if (keyEquals(KEY_OP_ASSIGN, opToken)) {
                            operationTokens.add(new Token(KEY_SEMICOLON, nameToken.source, token.line));
                            updater = new AssignmentNode(variableName, depth + 1);
                            members.add(updater.evaluate(operationTokens, evaluatorTree, debugMode));
                            break;

                        } else if (!isWhiteSpace(opToken)) {
                            return throwSyntaxError("Malformed update expression in for loop", token);
                        }
                    }
                } else {
                    return throwSyntaxError("Malformed update expression in for loop", token);
                }

            } else if (keyEquals(KEY_CURLY_OPEN, token)) {
                scope = new ScopeNode(this.nameToken, depth + 1, true);
                members.add(scope.evaluate(tokenList, evaluatorTree, debugMode));
                return this;

            } else {
                return throwSyntaxError("Unexpected token in for loop", token);
            }
            previousToken = token;
        }
        return throwSyntaxError("Unexpected end of file", nameToken);
    }

    @Override
    public String toString() {
        return "for loop";
    }

    public ScopeNode getScope() {
        return scope;
    }

    @Override
    public EvaluatorNode getMember(int i) {
        return super.getMember(i);
    }

    public AssignmentNode getUpdater() {
        return updater;
    }

    public OperationNode getCondition() {
        return condition;
    }

    public VariableNode getInitial() {
        return initial;
    }
}
