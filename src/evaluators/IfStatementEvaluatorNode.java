package src.evaluators;

import src.constants.Functions;
import src.tokens.*;
import java.util.*;

/**
 *
 * <h3> If statements </h3>
 * Conditionals / Routes:
 * <ul>
 *     <li> Token ")" if expression not null -> return this </li>
 *     <li> Token "{" if scope not null      -> {@link ScopeEvaluatorNode} </li>
 * </ul>
 * @author ElectricGun
 */

import static src.constants.Functions.*;
import static src.constants.Keywords.*;

public class IfStatementEvaluatorNode extends EvaluatorNode{

    OperationEvaluatorNode expression = null;
    ScopeEvaluatorNode scope = null;
    ElseEvaluatorNode elseNode = null;

    public IfStatementEvaluatorNode(Token token, int depth) {
        super(token, depth);
    }

    public OperationEvaluatorNode getExpression() {
        return expression;
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, Evaluator evaluator) throws Exception {
        String indent = " ".repeat(depth);
        System.out.printf(indent + "Parsing if statement %n");

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();

            if (isWhiteSpace(token)) {
                continue;

            } else if (Functions.equals(KEY_BRACKET_OPEN, token)) {
                List<Token> operationTokens = new ArrayList<>();
                int bracketCount = 1;

                // keep iterating until the full expression is obtained
                // before passing its tokens into an OperationEvaluatorNode
                while (true) {
                    Token expressionToken = tokenList.removeFirst();
                    System.out.printf(indent + "if statement : %s : %s%n", this.token.string, expressionToken.string);

                    if (Functions.equals(KEY_BRACKET_CLOSE, expressionToken)) {
                        bracketCount --;
                        operationTokens.add(expressionToken);

                    } else if (Functions.equals(KEY_BRACKET_OPEN, expressionToken)) {
                        bracketCount ++;
                        operationTokens.add(expressionToken);

                    } else if (bracketCount == 0) {
                        if (operationTokens.isEmpty()) {
                            throw new Exception("Expecting expression on if statement on line " + token.line);

                        } else {
                            // remove the last bracket )
                            operationTokens.removeLast();

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
            } else if (expression != null && scope == null) {
                if (Functions.equals(KEY_CURLY_OPEN, token)) {
                    ScopeEvaluatorNode scopeEvaluatorNode = new ScopeEvaluatorNode(this.token, depth + 1, true);
                    scopeEvaluatorNode.evaluate(tokenList, evaluator);
                    members.add(scopeEvaluatorNode);
                    scope = scopeEvaluatorNode;
                    // dont return yet, check for an else statement

                } else {
                    throw new Exception();
                }

            } else if (scope != null) {
                if (Functions.equals(KEY_CONDITIONAL_ELSE, token)) {
                    ElseEvaluatorNode elseEvaluatorNode = new ElseEvaluatorNode(this.token, depth + 1);
                    elseEvaluatorNode.evaluate(tokenList, evaluator);
                    members.add(elseEvaluatorNode);
                    elseNode = elseEvaluatorNode;
                    return this;

                } else {
                    // undo the token consumption
                    tokenList.add(0, token);
                    return this;
                    }
            } else {
                throw new Exception(("Unexpected token \"%s\" on if statement on line " + token.line).formatted(token));
            }
        }
        throw new Exception("Unexpected end of file");
    }

    @Override
    public String toString() {
        return "if statement";
    }
}
