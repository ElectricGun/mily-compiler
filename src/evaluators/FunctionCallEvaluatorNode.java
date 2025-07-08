package src.evaluators;

import src.constants.Functions;
import src.tokens.*;
import java.util.*;
import static src.constants.Functions.*;
import static src.constants.Keywords.*;


public class FunctionCallEvaluatorNode extends EvaluatorNode {
    public FunctionCallEvaluatorNode(Token token, int depth) {
        super(token, depth);
    }

    public List<Token> arguments = new ArrayList<>();

    private Token previousToken = null;
    private boolean expectingArgument = true;
    private boolean isInitialized = false;

    private void tryAddArgument(Token token) throws Exception {
        if (isVariableName(token) || isNumeric(token)) {
            arguments.add(token);
            expectingArgument = false;
        } else {
            throw new Exception("ERROR" + token.string);
        }
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, Evaluator evaluator) throws Exception {
        String indent = " ".repeat(depth);

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();

            System.out.printf(indent + "function call : %s:%n", token);


            if (isWhiteSpace(token)) {
                continue;
            }
            if (Functions.equals(KEY_BRACKET_CLOSE, token) && (!expectingArgument || !isInitialized)) {
                return this;

            } else if (Functions.equals(KEY_COMMA, token) && isInitialized) {
                expectingArgument = true;
            } else if (expectingArgument) {
                tryAddArgument(token);
            }
            else if (isInitialized) {
                throw new Exception("Unexpected token \"%s\" in function call at line %s".formatted(token, token.line));
            }

            previousToken = token;
            isInitialized = true;
        }

        return null;
    }

    @Override
    public String toString() {
        String arguments = "";
        int i = 0;
        for (Token token : this.arguments) {
            arguments += (i > 0 ?  ", " : "") + token.string;
            i++;
        }
        return "call " + token.string + "("  + arguments  + ")";
    }
}
