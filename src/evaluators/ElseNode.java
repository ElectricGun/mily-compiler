package src.evaluators;

import src.constants.*;
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

    public ElseNode(Token token, int depth) {
        super(token, depth);
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree, boolean debugMode) throws Exception {
        String indent = " ".repeat(depth);

        if (debugMode)
            System.out.printf(indent + "Parsing else block %s:%n", token);

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();

            if (debugMode)
                System.out.printf(indent + "else\t:\t%s\t:\t%s%n", this.token, token);

            if (isWhiteSpace(token)) {
                continue;

            } else if (keyEquals(KEY_CURLY_OPEN, token)) {
                ScopeNode scopeNode = new ScopeNode(this.token, depth + 1, true);
                members.add(scopeNode.evaluate(tokenList, evaluatorTree, debugMode));
                return this;

            } else if (keyEquals(KEY_CONDITIONAL_IF, token)) {
                IfStatementNode ifStatementEvaluatorNode = new IfStatementNode(this.token, depth + 1);
                members.add(ifStatementEvaluatorNode.evaluate(tokenList, evaluatorTree, debugMode));
                return this;

            } else {
                throw new Exception();
            }
        }
        return throwSyntaxError("Unexpected end of file", token);
    }

    @Override
    public String toString() {
        return "else";
    }
}
