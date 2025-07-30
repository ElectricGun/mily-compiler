package src.evaluators;

import src.tokens.*;
import java.util.*;

import static src.constants.Functions.*;
import static src.constants.Keywords.*;

/**
 * <h1> Class ElseNode </h1>
 * Else Statements
 * Routes:
 * <ul>
 *     <li> {@link ScopeNode} </li>
 *     <li> {@link IfStatementNode} </li>
 * </ul>
 * @author ElectricGun
 */

public class ElseNode extends EvaluatorNode {

    IfStatementNode ifStatementNode = null;
    ScopeNode block = null;

    public ElseNode(Token token, int depth) {
        super(token, depth);
    }

    public ScopeNode getScope() {
        return block;
    }

    public IfStatementNode getIfStatementNode() {
        return ifStatementNode;
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree, boolean debugMode) throws Exception {
        String indent = " ".repeat(depth);

        if (debugMode)
            System.out.printf(indent + "Parsing else block %s:%n", nameToken);

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();

            if (debugMode)
                System.out.printf(indent + "else\t:\t%s\t:\t%s%n", this.nameToken, token);

            if (isWhiteSpace(token)) {
                continue;

            } else if (keyEquals(KEY_CURLY_OPEN, token)) {
                block = new ScopeNode(this.nameToken, depth + 1, true);
                members.add(block.evaluate(tokenList, evaluatorTree, debugMode));
                return this;

            } else if (keyEquals(KEY_CONDITIONAL_IF, token)) {
                ifStatementNode = new IfStatementNode(this.nameToken, depth + 1);
                members.add(ifStatementNode.evaluate(tokenList, evaluatorTree, debugMode));
                return this;

            } else {
                throw new Exception();
            }
        }
        return throwSyntaxError("Unexpected end of file", nameToken);
    }

    @Override
    public String toString() {
        return "else";
    }
}
