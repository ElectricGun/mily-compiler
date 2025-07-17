package src.evaluators;

import src.constants.*;
import src.tokens.*;
import java.util.*;
import static src.constants.Functions.*;
import static src.constants.Keywords.*;

/**
 * <h3> Parses function declarations </h3>
 * @author ElectricGun
 */

public class FunctionDeclareNode extends EvaluatorNode {

    public FunctionDeclareNode(Token name, int depth) {
        super(name, depth);
    }

    List<String> argumentNames = new ArrayList<>();
    ScopeNode scope;

    private boolean isInitialized = false;
    private boolean functionDeclared = false;
    private boolean argumentWanted = false;

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree) throws Exception {
        String indent = " ".repeat(depth);

        System.out.printf(indent + "Parsing Function %s:%n", token);

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();

            System.out.printf(indent + "function\t:\t%s\t:\t%s%n", this.token, token);

            if (isWhiteSpace(token)) {
                continue;

            } else if (isPunctuation(token) && !isWhiteSpace(token)) {
                 if (argumentWanted) {
                    throw new Exception("Expecting an argument at function declaration %s: \"%s\" at line %s".formatted(this.token, token, token.line));

                } else if (Functions.equals(KEY_BRACKET_CLOSE, token)) {
                    functionDeclared = true;

                } else if (Functions.equals(KEY_COMMA, token)) {
                    argumentWanted = true;

                } else if (functionDeclared && Functions.equals(KEY_CURLY_OPEN, token)) {
                    System.out.printf(indent + "Function header \"%s(%s)\" created%n", this.token, String.join(", ", argumentNames));
                    scope = new ScopeNode(this.token, depth + 1, true, this);
                    members.add(scope.evaluate(tokenList, evaluatorTree));
                    return this;

                } else {
                    throw new Exception("Unexpected punctuation at function declaration %s: \"%s\" at line %s".formatted(this.token, token, token.line));
                }
            } else if (isOperator(token)) {
                throw new Exception("Unexpected operator at function declaration %s: \"%s\" at line %s".formatted(this.token, token, token.line));

            } else {
                if (isVariableName(token)) {
                    if (!isInitialized || argumentWanted) {
                        argumentNames.add(token.string);
                        argumentWanted = false;

                        DeclarationNode declarationNode = new DeclarationNode(token, depth + 1);
                        declarationNode.variableName = token.string;
                        members.add(declarationNode);
                        System.out.printf("Added argument %s%n", token);

                    } else {
                        throw new Exception("Unexpected token at function declaration %s: \"%s\" at line %s".formatted(this.token, token, token.line));
                    }
                }
                isInitialized = true;
            }
        }
        throw new Exception("Unexpected end of file");
    }

    @Override
    public String toString() {
        return "declare function : %s : %s(%s)".formatted(token, token, String.join(", ", argumentNames));
    }
}