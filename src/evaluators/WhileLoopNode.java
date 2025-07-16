package src.evaluators;

import src.constants.*;
import src.tokens.*;
import java.util.*;
import static src.constants.Functions.*;
import static src.constants.Keywords.*;

/**
 * <h3> While Loop </h3>
 *  @author ElectricGun
 */

public class WhileLoopNode extends ConditionalNode {

    public WhileLoopNode(Token token, int depth) {
        super(token, depth);
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree) throws Exception {

        String indent = " ".repeat(depth);

        System.out.printf(indent + "Parsing While Loop %s:%n", token);

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();

            System.out.printf(indent + "while\t:\t%s\t:\t%s%n", this.token, token);

            if (isWhiteSpace(token)) {
                continue;

            } else if (Functions.equals(KEY_BRACKET_OPEN, token)) {
                parseOperation(tokenList, evaluatorTree, depth);

            } else if (expression != null && scope == null) {
                if (Functions.equals(KEY_CURLY_OPEN, token)) {
                    createBlock(tokenList, evaluatorTree);
                    return this;

                } else {
                    throw new Exception();
                }
            } else {
                throw new Exception(("Unexpected token \"%s\" on while loop on line " + token.line).formatted(token));
            }
        }
        throw new Exception("Unexpected end of file");
    }

    @Override
    public String toString() {
        return "while loop #" + hashCode();
    }
}
