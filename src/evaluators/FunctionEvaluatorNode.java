package src.evaluators;

import src.constants.*;
import src.tokens.*;
import java.util.*;
import static src.constants.Functions.*;
import static src.constants.Keywords.*;

public class FunctionEvaluatorNode extends EvaluatorNode {
    List<String> argumentNames = new ArrayList<>();
    ScopeEvaluatorNode scope;
    public FunctionEvaluatorNode(Token name, int depth) {
        super(name, depth);
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, Evaluator evaluator) throws Exception {
        String indent = " ".repeat(depth);

        boolean isInitialized = false;
        boolean functionDeclared = false;
        boolean argumentWanted = false;

        System.out.printf(indent + "Parsing Function %s:%n", token);

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();

            System.out.printf(indent + "function\t:\t%s\t:\t%s%n", this.token, token);

            buffer += token;

            // evaluate punctuations
            if (token.length() == 1  && isPunctuation(token)) {

                if (isWhiteSpace(token)) {
                    continue;
                }

                if (argumentWanted) {
                    throw new Exception("Expecting an argument at function declaration %s: \"%s\" at line %s".formatted(this.token, token, token.line));
                } else if (Functions.equals(KEY_BRACKET_CLOSE, token)) {
                    functionDeclared = true;
                } else if (Functions.equals(KEY_COMMA, token)) {
                    argumentWanted = true;
                }
                else if (functionDeclared && Functions.equals(KEY_CURLY_OPEN, token)) {
                    System.out.printf(indent + "Function header \"%s(%s)\" created%n", this.token, String.join(", ", argumentNames));
                    scope = new ScopeEvaluatorNode(this.token, depth + 1, true, this);
                    members.add(scope.evaluate(tokenList, evaluator));
                    return this;
                } else {
                    throw new Exception("Unexpected punctuation at function declaration %s: \"%s\" at line %s".formatted(this.token, token, token.line));
                }
            }
            // evaluate operators
            else if (isOperator(token)) {
                throw new Exception("Unexpected operator at function declaration %s: \"%s\" at line %s".formatted(this.token, token, token.line));
            }
            // evaluate the rest
            else {
                if (!isInitialized || argumentWanted) {
                    argumentNames.add(token.string);
                    argumentWanted = false;
                    System.out.printf("Added argument %s%n", token);
                }
                isInitialized = true;
            }
        }
        return null;
    }
    @Override
    public String toString() {
        return "function : %s : %s(%s)".formatted(token, token, String.join(", ", argumentNames));
    }
}