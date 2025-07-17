package src.evaluators;

import src.constants.*;
import src.tokens.*;
import java.util.*;

import static src.constants.Functions.*;
import static src.constants.Keywords.*;

/**
 *
 * <h3> If statements </h3>
 * Conditionals / Routes:
 * <ul>
 *     <li> Token ")" if expression not null -> return this </li>
 *     <li> Token "{" if scope not null      -> {@link ScopeNode} </li>
 * </ul>
 * @author ElectricGun
 */

public class IfStatementNode extends ConditionalNode {

    ElseNode elseNode = null;

    public IfStatementNode(Token token, int depth) {
        super(token, depth);
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree) throws Exception {
        String indent = " ".repeat(depth);
        System.out.printf(indent + "Parsing if statement %n");

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();

            if (isWhiteSpace(token)) {
                continue;

            } else if (Functions.equals(KEY_BRACKET_OPEN, token)) {
                    parseOperation(tokenList, evaluatorTree, depth);

            } else if (expression != null && scope == null) {
                if (Functions.equals(KEY_CURLY_OPEN, token)) {
                    createBlock(tokenList, evaluatorTree);
                    // dont return yet, check for an else statement

                } else {
                    throw new Exception();
                }

            } else if (scope != null) {
                if (Functions.equals(KEY_CONDITIONAL_ELSE, token)) {
                    ElseNode elseNode = new ElseNode(this.token, depth + 1);
                    members.add(elseNode.evaluate(tokenList, evaluatorTree));
                    this.elseNode = elseNode;

                } else {
                    // undo the token consumption
                    tokenList.addFirst(token);
                }
                return this;
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
