package src.evaluators;

import src.tokens.*;
import java.util.*;

/**
 * If and While statements
 *
 */

public class ConditionalEvaluatorNode extends EvaluatorNode{

    public ConditionalEvaluatorNode(Token token, int depth) {
        super(token, depth);
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, Evaluator evaluator) throws Exception {
        throw new UnsupportedOperationException("This method is not yet implemented.");
//        String indent = " ".repeat(depth);
//        System.out.printf(indent + "Parsing %s Conditional %s:%n", token);
//
//        while (!tokenList.isEmpty()) {
//
//        }
//
//        return null;
    }
}
