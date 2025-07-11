package src.evaluators;

import src.constants.Functions;
import src.tokens.*;
import java.util.*;

/**
 *
 * <h3> If statements </h3>
 * Conditionals / Routes:
 * <ul>
 *     <li> Token ")" if expression not null -> return this </li>
 *     <li> Token "{" if scope not null      -> {@link ScopeEvaluatorNode} </li>
 * </ul>
 * @author ElectricGun
 */

import static src.constants.Functions.*;
import static src.constants.Keywords.*;

public class IfStatementEvaluatorNode extends ConditionalEvaluatorNode{

    ScopeEvaluatorNode scope = null;
    ElseEvaluatorNode elseNode = null;

    public IfStatementEvaluatorNode(Token token, int depth) {
        super(token, depth);
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, Evaluator evaluator) throws Exception {
        String indent = " ".repeat(depth);
        System.out.printf(indent + "Parsing if statement %n");

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();

            if (isWhiteSpace(token)) {
                continue;

            } else if (Functions.equals(KEY_BRACKET_OPEN, token)) {
                    parseOperation(tokenList, evaluator);

            } else if (expression != null && scope == null) {
                if (Functions.equals(KEY_CURLY_OPEN, token)) {
                    ScopeEvaluatorNode scopeEvaluatorNode = new ScopeEvaluatorNode(this.token, depth + 1, true);
                    scopeEvaluatorNode.evaluate(tokenList, evaluator);
                    members.add(scopeEvaluatorNode);
                    scope = scopeEvaluatorNode;
                    // dont return yet, check for an else statement

                } else {
                    throw new Exception();
                }

            } else if (scope != null) {
                if (Functions.equals(KEY_CONDITIONAL_ELSE, token)) {
                    ElseEvaluatorNode elseEvaluatorNode = new ElseEvaluatorNode(this.token, depth + 1);
                    elseEvaluatorNode.evaluate(tokenList, evaluator);
                    members.add(elseEvaluatorNode);
                    elseNode = elseEvaluatorNode;
                    return this;

                } else {
                    // undo the token consumption
                    tokenList.add(0, token);
                    return this;
                    }
            } else {
                throw new Exception(("Unexpected token \"%s\" on if statement on line " + token.line).formatted(token));
            }
        }
        throw new Exception("Unexpected end of file");
    }

    @Override
    public String toString() {
        return "if statement";
    }
}
