package src.evaluators;

import src.constants.*;
import src.tokens.*;
import java.util.*;

import static src.constants.Keywords.*;

/**
 * <h1> Class ConditionalNode </h1>
 * Template for If and While Statements
 * @author ElectricGun
 */

public abstract class ConditionalNode extends EvaluatorNode {

    ScopeNode scope = null;
    OperationNode expression = null;

    public ConditionalNode(Token token, int depth) {
        super(token, depth);
    }

    public OperationNode getExpression() {
        return expression;
    }

    public void parseOperation(List<Token> tokenList, EvaluatorTree evaluatorTree, int depth) throws Exception {
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
                    OperationNode operationNode = new OperationNode(this.token, depth + 1);
                    members.add(operationNode.evaluate(operationTokens, evaluatorTree));
                    this.expression = operationNode;
                    break;
                }
            } else {
                operationTokens.add(expressionToken);
            }
        }
    }

    public void createBlock(List<Token> tokenList, EvaluatorTree evaluatorTree) {
        ScopeNode scopeNode = new ScopeNode(this.token, depth + 1, true);
        scopeNode.evaluate(tokenList, evaluatorTree);
        members.add(scopeNode);
        scope = scopeNode;
    }
}
