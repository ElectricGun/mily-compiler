package src.evaluators;

import src.constants.*;
import src.tokens.*;
import java.util.*;

import static src.constants.Functions.*;
import static src.constants.Keywords.*;

/**
 *
 * <h1> Class IfStatementNode </h1>
 * If Statements
 * Routes:
 * <ul>
 *     <li> {@link ScopeNode} </li>
 * </ul>
 * @author ElectricGun
 */

public class IfStatementNode extends ConditionalNode {

    ElseNode elseNode = null;

    public IfStatementNode(Token token, int depth) {
        super(token, depth);
    }

    public ElseNode getElseNode() {
        return elseNode;
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree, boolean debugMode) throws Exception {
        String indent = " ".repeat(depth);

        if (debugMode)
            System.out.printf(indent + "Parsing if statement %n");

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();

            if (isWhiteSpace(token)) {
                continue;

            } else if (keyEquals(KEY_BRACKET_OPEN, token)) {
                    parseOperation(tokenList, evaluatorTree, depth, debugMode);

            } else if (expression != null && scope == null) {
                if (keyEquals(KEY_CURLY_OPEN, token)) {
                    createBlock(tokenList, evaluatorTree, debugMode);
                    // dont return yet, check for an else statement

                } else {
                    return throwSyntaxError("Unexpected token on if statement", token);
                }

            } else if (scope != null) {
                if (keyEquals(KEY_CONDITIONAL_ELSE, token)) {
                    ElseNode elseNode = new ElseNode(token, depth + 1);
                    members.add(elseNode.evaluate(tokenList, evaluatorTree, debugMode));
                    this.elseNode = elseNode;

                } else {
                    // undo the token consumption
                    tokenList.addFirst(token);
                }
                return this;

            } else {
                return throwSyntaxError("Unexpected token on if statement", token);
            }
        }

        if (scope == null)
            return throwSyntaxError("Unexpected end of file", token);
        else
            return this;
    }

    @Override
    public String toString() {
        return "if statement   #" + hashCode();
    }
}
