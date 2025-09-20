package mily.parsing;

import mily.tokens.*;

import java.util.*;

import static mily.constants.Functions.*;
import static mily.constants.Keywords.*;

/**
 * <h1> Class ConditionalNode </h1>
 * Template for If and While Statements
 *
 * @author ElectricGun
 */

public abstract class ConditionalNode extends EvaluatorNode {

    protected ScopeNode scope = null;
    protected OperationNode expression = null;

    public ConditionalNode(Token nameToken, int depth) {
        super(nameToken, depth);
    }

    public OperationNode getExpression() {
        return expression;
    }

    public ScopeNode getScope() {
        return scope;
    }

    // what even is this method?
    public void parseOperation(List<Token> tokenList, EvaluatorTree evaluatorTree, int depth, boolean debugMode) {
        String indent = " ".repeat(depth);
        List<Token> operationTokens = new ArrayList<>();
        int bracketCount = 1;

        // keep iterating until the full expression is obtained
        // before passing its tokens into an OperationEvaluatorNode
        while (true) {
            Token expressionToken = tokenList.remove(0);

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
                    operationTokens.remove(operationTokens.size() - 1);

                    if (operationTokens.isEmpty()) {
                        throwSyntaxError("Expecting expression on conditional", nameToken);
                        return;
                    }

                    operationTokens.add(new Token(";", nameToken.source, nameToken.line));
                    OperationNode operationNode = new OperationNode(this.nameToken, depth + 1);
                    members.add(operationNode.evaluate(operationTokens, evaluatorTree));
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
        scopeNode.evaluate(tokenList, evaluatorTree);
        members.add(scopeNode);
        scope = scopeNode;
    }
}
