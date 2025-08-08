package src.parsing;

import java.util.*;

import src.tokens.*;

import static src.constants.Functions.*;
import static src.constants.Keywords.*;

/**
 * <h1> Class OperationBracketNode </h1>
 * Operation Brackets
 * Helper node for parsing parenthesis within operations, such as (x + y) * z
 *
 * @author ElectricGun
 */

public class OperationBracketNode extends EvaluatorNode {

    public int operatorIndex;
    protected List<Token> operationTokens = new ArrayList<>();

    public OperationBracketNode(Token token, int depth, int operatorIndex) {
        super(token, depth);
        this.operatorIndex = operatorIndex;
    }

    protected EvaluatorNode evaluator(List<Token> tokenList, List<Integer> orders, EvaluatorTree evaluatorTree, boolean debugMode) throws Exception {

        String indent = " ".repeat(depth);

        if (debugMode)
            System.out.printf(indent + "Parsing Brackets %s:%n", nameToken);

        // remove the start bracket
        tokenList.remove(operatorIndex);
        orders.remove(operatorIndex);

        int bracketCounter = 0;

        while (!tokenList.isEmpty()) {
            Token token = tokenList.remove(operatorIndex);

            if (debugMode)
                System.out.printf(indent + "brackets : %s : %s%n", this.nameToken, token);

            if (token.length() == 1 && keyEquals(KEY_BRACKET_OPEN, token)) {
                bracketCounter++;

            } else if (token.length() == 1 && keyEquals(KEY_BRACKET_CLOSE, token)) {
                // replace closing bracket with a constant indicator
                bracketCounter--;
            }
            if (bracketCounter < 0) {
                OperationNode operationNode = new OperationNode(new Token(this.nameToken.string, this.nameToken.source, this.nameToken.line), depth + 1);
                operationTokens.add(new Token(";", this.nameToken.source, this.nameToken.line));
                OperationNode evaluated = (OperationNode) operationNode.evaluate(operationTokens, evaluatorTree, debugMode);

                // substitute the operator removed with a BracketToken
                // an integer is not added to orders because we are not removing one on this final loop
                tokenList.add(operatorIndex, new BracketToken("BRACKET", this.nameToken.source, this.nameToken.line, evaluated));
                members.add(evaluated);
                return this;
            }

            orders.remove(operatorIndex);
            operationTokens.add(token);
        }
        return throwSyntaxError("Unexpected end of file", nameToken);
    }

    // todo why did i write this???
    public EvaluatorNode evaluate(List<Token> tokenList, List<Integer> orders, EvaluatorTree evaluatorTree, boolean debugMode) {
        try {
            return evaluator(tokenList, orders, evaluatorTree, debugMode);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
            return null;
        }
    }
}