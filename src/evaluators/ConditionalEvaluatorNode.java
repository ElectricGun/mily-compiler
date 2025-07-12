package src.evaluators;

import src.constants.*;
import src.tokens.*;
import java.util.*;
import static src.constants.Keywords.*;

/**
 * <h3> Template for If and While Statements </h3>
 *  @author ElectricGun
 */

public abstract class ConditionalEvaluatorNode extends EvaluatorNode {

    ScopeEvaluatorNode scope = null;
    OperationEvaluatorNode expression = null;

    public ConditionalEvaluatorNode(Token token, int depth) {
        super(token, depth);
    }

    public OperationEvaluatorNode getExpression() {
        return expression;
    }

    public void parseOperation(List<Token> tokenList, Evaluator evaluator, int depth) throws Exception {
        String indent = " ".repeat(depth);
        List<Token> operationTokens = new ArrayList<>();
        int bracketCount = 1;

        // keep iterating until the full expression is obtained
        // before passing its tokens into an OperationEvaluatorNode
        while (true) {
            Token expressionToken = tokenList.removeFirst();
            System.out.printf(indent + "if statement : %s : %s%n", this.token.string, expressionToken.string);

            if (Functions.equals(KEY_BRACKET_CLOSE, expressionToken)) {
                bracketCount--;
                operationTokens.add(expressionToken);

            } else if (Functions.equals(KEY_BRACKET_OPEN, expressionToken)) {
                bracketCount++;
                operationTokens.add(expressionToken);

            } else if (bracketCount == 0) {
                if (operationTokens.isEmpty()) {
                    throw new Exception("Expecting expression on if statement on line " + token.line);

                } else {
                    // remove the last bracket )
                    operationTokens.removeLast();

                    if (operationTokens.isEmpty()) {
                        throw new Exception("Expecting expression on conditional at line " + token.line);
                    }

                    operationTokens.add(new Token(";", token.line));
                    OperationEvaluatorNode operationEvaluatorNode = new OperationEvaluatorNode(this.token, depth + 1);
                    operationEvaluatorNode.evaluate(operationTokens, evaluator);
                    members.add(operationEvaluatorNode);
                    this.expression = operationEvaluatorNode;
                    break;
                }
            } else {
                operationTokens.add(expressionToken);
            }
        }
    }

    public void createBlock(List<Token> tokenList, Evaluator evaluator) {
        ScopeEvaluatorNode scopeEvaluatorNode = new ScopeEvaluatorNode(this.token, depth + 1, true);
        scopeEvaluatorNode.evaluate(tokenList, evaluator);
        members.add(scopeEvaluatorNode);
        scope = scopeEvaluatorNode;
    }
}
