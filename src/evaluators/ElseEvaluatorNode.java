package src.evaluators;

import src.constants.Functions;
import src.tokens.Token;

import java.util.List;

import static src.constants.Functions.*;
import static src.constants.Keywords.*;


public class ElseEvaluatorNode extends EvaluatorNode {

    public ElseEvaluatorNode(Token token, int depth) {
        super(token, depth);
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, Evaluator evaluator) throws Exception {
        String indent = " ".repeat(depth);

        System.out.printf(indent + "Parsing else block %s:%n", token);

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();

            System.out.printf(indent + "else\t:\t%s\t:\t%s%n", this.token, token);

            if (isWhiteSpace(token)) {
                continue;

            } else if (Functions.equals(KEY_CURLY_OPEN, token)) {
                ScopeEvaluatorNode scopeEvaluatorNode = new ScopeEvaluatorNode(this.token, depth + 1, true);
                scopeEvaluatorNode.evaluate(tokenList, evaluator);
                members.add(scopeEvaluatorNode);
                return this;

            } else if (Functions.equals(KEY_CONDITIONAL_IF, token)) {
                IfStatementEvaluatorNode ifStatementEvaluatorNode = new IfStatementEvaluatorNode(this.token, depth + 1);
                ifStatementEvaluatorNode.evaluate(tokenList, evaluator);
                members.add(ifStatementEvaluatorNode);
                return this;

            } else {
                throw new Exception();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "else";
    }
}
