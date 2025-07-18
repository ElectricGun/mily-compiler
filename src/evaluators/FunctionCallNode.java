package src.evaluators;

import src.constants.*;
import src.tokens.*;
import java.util.*;

import static src.constants.Functions.*;
import static src.constants.Keywords.*;

/**
 * <h1> Class FunctionCallNode </h1>
 * Function Calls
 * Purpose: Parses function calls, such as f(), f(x), and f(x, y) <br>
 * Conditionals / Routes:
 * <ul>
 *     <li> Token ")" on first iteration             -> return this </li>
 *     <li> Token ")" when not expecting a parameter -> return this </li>
 * </ul>
 * @author ElectricGun
 */

public class FunctionCallNode extends EvaluatorNode {

    public List<Token> arguments = new ArrayList<>();

    private boolean expectingArgument = true;
    private boolean isInitialized = false;

    public FunctionCallNode(Token token, int depth) {
        super(token, depth);
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree) throws Exception {
        String indent = " ".repeat(depth);

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();

            System.out.printf(indent + "function call %s: %s:%n", this.token, token);

            if (isWhiteSpace(token)) {
                continue;

            } else if (Functions.equals(KEY_BRACKET_CLOSE, token) && (!expectingArgument || !isInitialized)) {
                return this;

            } else if (Functions.equals(KEY_COMMA, token) && isInitialized) {
                expectingArgument = true;

            } else if (expectingArgument) {
                tryAddArgument(token);

            } else if (isInitialized) {
                throw new Exception("Unexpected token \"%s\" in function call at line %s".formatted(token, token.line));
            }
            isInitialized = true;
        }
        throw new Exception("Unexpected end of file");
    }

    private void tryAddArgument(Token token) throws Exception {
        if (isVariableName(token) || isNumeric(token)) {
            arguments.add(token);
            expectingArgument = false;

        } else {
            throw new Exception("ERROR" + token.string);
        }
    }

    @Override
    public String toString() {
        StringBuilder arguments = new StringBuilder();
        int i = 0;

        for (Token token : this.arguments) {
            arguments.append(i > 0 ? ", " : "").append(token.string);
            i++;
        }
        return "call " + token.string + "("  + arguments  + ")";
    }
}
