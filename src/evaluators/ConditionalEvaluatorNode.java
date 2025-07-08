package src.evaluators;

import src.tokens.*;
import java.util.*;

/**
 * @author ElectricGun
 * <h3> If and While statements </h3>
 * Conditionals / Routes:
 * <ul>
 *     <li> Token ")" if expression not null -> return this </li>
 * </ul>
 */

public class ConditionalEvaluatorNode extends EvaluatorNode{

    OperationEvaluatorNode expression = null;

    public ConditionalEvaluatorNode(Token token, int depth) {
        super(token, depth);
    }

    public OperationEvaluatorNode getExpression() {
        return expression;
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
