package mily.parsing;

import mily.tokens.*;

import java.util.*;

import static mily.constants.Functions.*;
import static mily.constants.Keywords.*;

/**
 * <h1> Class IfStatementNode </h1>
 * If Statements
 * Routes:
 * <ul>
 *     <li> {@link ScopeNode} </li>
 * </ul>
 *
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
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree) throws Exception {
        String indent = " ".repeat(depth);

        if (evaluatorTree.debugMode)
            System.out.printf(indent + "Parsing if statement %n");

        while (!tokenList.isEmpty()) {
            Token token = tokenList.remove(0);

            if (isWhiteSpace(token)) {
                continue;
            }

            if (keyEquals(KEY_BRACKET_OPEN, token)) {
                parseOperation(tokenList, evaluatorTree, depth, evaluatorTree.debugMode);

            } else if (expression != null && scope == null) {
                if (keyEquals(KEY_CURLY_OPEN, token)) {
                    createBlock(tokenList, evaluatorTree, evaluatorTree.debugMode);
                    // dont return yet, check for an else statement

                } else {
                    return throwSyntaxError("Unexpected token on if statement", token);
                }

            } else if (scope != null) {
                if (keyEquals(KEY_CONDITIONAL_ELSE, token)) {
                    ElseNode elseNode = new ElseNode(token, depth + 1);
                    members.add(elseNode.evaluate(tokenList, evaluatorTree));
                    this.elseNode = elseNode;

                } else {
                    // undo the token consumption
                    tokenList.add(0, token);
                }
                return this;

            } else {
                return throwSyntaxError("Unexpected token on if statement", token);
            }
        }

        if (scope == null)
            return throwSyntaxError("Unexpected end of file", nameToken);
        else
            return this;
    }

    @Override
    public String toString() {
        return "if statement   #" + hashCode();
    }
}
