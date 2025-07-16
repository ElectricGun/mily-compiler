package src.evaluators;

import src.constants.*;
import src.tokens.*;
import java.util.*;
import static src.constants.Functions.*;
import static src.constants.Keywords.*;

/**
 *
 * <h3> Else statements </h3>
 * Conditionals / Routes:
 * <ul>
 *     <li> Token "{"   -> {@link ScopeNode} and return this </li>
 *     <li> Token "if"  -> {@link IfStatementNode} and return this </li>
 * </ul>
 * @author ElectricGun
 */

public class ElseNode extends EvaluatorNode {

    public ElseNode(Token token, int depth) {
        super(token, depth);
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree) throws Exception {
        String indent = " ".repeat(depth);

        System.out.printf(indent + "Parsing else block %s:%n", token);

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();

            System.out.printf(indent + "else\t:\t%s\t:\t%s%n", this.token, token);

            if (isWhiteSpace(token)) {
                continue;

            } else if (Functions.equals(KEY_CURLY_OPEN, token)) {
                ScopeNode scopeNode = new ScopeNode(this.token, depth + 1, true);
                members.add(scopeNode.evaluate(tokenList, evaluatorTree));
                return this;

            } else if (Functions.equals(KEY_CONDITIONAL_IF, token)) {
                IfStatementNode ifStatementEvaluatorNode = new IfStatementNode(this.token, depth + 1);
                members.add(ifStatementEvaluatorNode.evaluate(tokenList, evaluatorTree));
                return this;

            } else {
                throw new Exception();
            }
        }
        throw new Exception("Unexpected end of file");
    }

    @Override
    public String toString() {
        return "else #" + hashCode();
    }
}
