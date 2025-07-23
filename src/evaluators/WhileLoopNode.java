package src.evaluators;

import src.constants.*;
import src.interfaces.MilyThrowable;
import src.tokens.*;
import java.util.*;

import static src.constants.Functions.*;
import static src.constants.Keywords.*;

/**
 * <h1> Class WhileLoopNode </h1>
 * While Loops
 * Parses while loops
 * @author ElectricGun
 */

public class WhileLoopNode extends ConditionalNode {

    public WhileLoopNode(Token token, int depth) {
        super(token, depth);
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree, boolean debugMode) throws Exception {

        String indent = " ".repeat(depth);

        if (debugMode)
            System.out.printf(indent + "Parsing While Loop %s:%n", token);

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();

            if (debugMode)
                System.out.printf(indent + "while\t:\t%s\t:\t%s%n", this.token, token);

            if (isWhiteSpace(token)) {
                continue;

            } else if (Functions.equals(KEY_BRACKET_OPEN, token)) {
                parseOperation(tokenList, evaluatorTree, depth, debugMode);

            } else if (expression != null && scope == null) {
                if (Functions.equals(KEY_CURLY_OPEN, token)) {
                    createBlock(tokenList, evaluatorTree, debugMode);
                    return this;

                } else {
                    return throwException("Unexpected token on while loop on line", token);
                }
            } else {
                return throwException("Unexpected token on while loop on line", token);
            }
        }
        return throwException("Unexpected end of file", token);
    }

    @Override
    public String toString() {
        return "while loop";
    }
}
