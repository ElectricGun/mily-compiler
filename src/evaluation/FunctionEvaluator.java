package src.evaluation;

import java.util.ArrayList;
import java.util.List;

import static src.Vars.*;
import static src.Vars.isOperator;

public class FunctionEvaluator extends EvaluatorNode {
    List<String> argumentNames = new ArrayList<>();
    ScopeEvaluator scope;
    public FunctionEvaluator(Token name, int depth) {
        super(name, depth);
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, Evaluator evaluator) throws Exception {
        String indent = " ".repeat(depth);

        boolean isInitialized = false;
        boolean functionDeclared = false;
        boolean argumentWanted = false;

        System.out.printf(indent + "Parsing Function %s:%n", name);

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();

            System.out.printf(indent + "function\t:\t%s\t:\t%s%n",name, token);

            buffer += token;

            // evaluate punctuations
            if (token.length() == 1 && isPunctuation(token.charAt(0))) {
                char c = token.charAt(0);
                if (isWhiteSpace(c)) {
                    continue;
                }

                if (argumentWanted) {
                    throw new Exception("Expecting an argument at function declaration %s: \"%s\" at line %s".formatted(name, c, token.line));
                } else if (CHAR_BRACKET_CLOSE == c) {
                    functionDeclared = true;
                } else if (CHAR_COMMA == c) {
                    argumentWanted = true;
                }
                else if (functionDeclared && CHAR_CURLY_OPEN == c) {
                    System.out.printf(indent + "Function header \"%s(%s)\" created%n", name, String.join(", ", argumentNames));
                    scope = new ScopeEvaluator(name, depth + 1, true, this);
                    members.add(scope.evaluate(tokenList, evaluator));
                    return this;
                } else {
                    throw new Exception("Unexpected punctuation at function declaration %s: \"%s\" at line %s".formatted(name, c, token.line));
                }
            }
            // evaluate operators
            else if (isOperator(token)) {
                throw new Exception("Unexpected operator at function declaration %s: \"%s\" at line %s".formatted(name, token, token.line));
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
        return "function : %s : %s(%s)".formatted(name, name, String.join(", ", argumentNames));
    }
}