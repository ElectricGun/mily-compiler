package src.parsing;

import java.util.*;

import src.tokens.*;

import static src.constants.Functions.*;
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

    public ScopeNode getScope() {
        return scope;
    }

    public void parseOperation(List<Token> tokenList, EvaluatorTree evaluatorTree, int depth, boolean debugMode) throws Exception {
        String indent = " ".repeat(depth);
        List<Token> operationTokens = new ArrayList<>();
        int bracketCount = 1;

        // keep iterating until the full expression is obtained
        // before passing its tokens into an OperationEvaluatorNode
        while (true) {
            Token expressionToken = tokenList.removeFirst();

            if (debugMode)
                System.out.printf(indent + "if statement : %s : %s%n", this.nameToken.string, expressionToken.string);

            if (keyEquals(KEY_BRACKET_CLOSE, expressionToken)) {
                bracketCount--;
                operationTokens.add(expressionToken);

            } else if (keyEquals(KEY_BRACKET_OPEN, expressionToken)) {
                bracketCount++;
                operationTokens.add(expressionToken);

            } else if (bracketCount == 0) {
                if (operationTokens.isEmpty()) {
                    // todo: why is this method void
                    throwSyntaxError("Expecting expression on if statement", nameToken);
                    return;

                } else {
                    // remove the last bracket )
                    operationTokens.removeLast();

                    if (operationTokens.isEmpty()) {
                        throwSyntaxError("Expecting expression on conditional", nameToken);
                        return;
                    }

                    operationTokens.add(new Token(";", nameToken.line));
                    OperationNode operationNode = new OperationNode(this.nameToken, depth + 1);
                    members.add(operationNode.evaluate(operationTokens, evaluatorTree, debugMode));
                    this.expression = operationNode;
                    break;
                }
            } else {
                operationTokens.add(expressionToken);
            }
        }
    }

    public void createBlock(List<Token> tokenList, EvaluatorTree evaluatorTree, boolean debugMode) {
        ScopeNode scopeNode = new ScopeNode(this.nameToken, depth + 1, true);
        scopeNode.evaluate(tokenList, evaluatorTree, debugMode);
        members.add(scopeNode);
        scope = scopeNode;
    }
}
