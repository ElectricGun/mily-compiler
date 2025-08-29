package mily.parsing;

import mily.parsing.callables.*;
import mily.tokens.*;

import java.util.*;

import static mily.constants.Keywords.*;

/**
 * <h1> Class AssignmentNode </h1>
 * Used for both operation and function assignments.
 *
 * @author ElectricGun
 * @see mily.parsing.OperationNode
 * @see FunctionDeclareNode
 */

public class AssignmentNode extends VariableNode {

    protected OperationNode expression = null;

    public AssignmentNode(Token token, int depth) {
        super(KEY_DATA_UNKNOWN, token, depth);
    }

    @Override
    public String errorName() {
        return "assign " + "\"" + getName() + "\"";
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree) throws Exception {
        String indent = " ".repeat(depth);

        if (evaluatorTree.debugMode)
            System.out.printf(indent + "Parsing Variable Declaration %s:%n", nameToken);

        if (!tokenList.isEmpty()) {
            expression = (OperationNode) new OperationNode(this.nameToken, depth + 1).evaluate(tokenList, evaluatorTree);
            members.add(expression);
            variableName = this.nameToken.string;
            return this;
        }

        return throwSyntaxError("Unexpected end of file", nameToken);
    }

    @Override
    public String toString() {
        return String.format("assign %s = %s", type, variableName);
    }
}
