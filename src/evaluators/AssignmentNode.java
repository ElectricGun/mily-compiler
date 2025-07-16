package src.evaluators;

import src.tokens.*;
import java.util.*;

/**
 * <h3> Variable Assignments </h3>
 *  @author ElectricGun
 */

public class AssignmentNode extends VariableNode {

    OperationNode expression = null;

    public AssignmentNode(Token token, int depth) {
        super(token, depth);
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree) throws Exception {
        String indent = " ".repeat(depth);
        System.out.printf(indent + "Parsing Variable Declaration %s:%n", token);

        if (!tokenList.isEmpty()) {
            expression = (OperationNode) new OperationNode(this.token, depth + 1).evaluate(tokenList, evaluatorTree);
            members.add(expression);
            variableName = this.token.string;
            return this;
        }
        throw new Exception("Unexpected end of file");
    }

    @Override
    public String toString() {
        return "assign %s =".formatted(token);
    }
}
