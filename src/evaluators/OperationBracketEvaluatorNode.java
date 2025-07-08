package src.evaluators;

import java.util.*;
import src.tokens.*;
import src.constants.*;
import static src.constants.Keywords.*;

public class OperationBracketEvaluatorNode extends EvaluatorNode {

    protected List<Token> operationTokens = new ArrayList<>();
    public int operatorIndex;

    public OperationBracketEvaluatorNode(Token token, int depth, int operatorIndex) {
        super(token, depth);
        this.operatorIndex = operatorIndex;
    }

    protected EvaluatorNode evaluator(List<Token> tokenList, List<Integer> orders, Evaluator evaluator) throws Exception {

        String indent = " ".repeat(depth);
        System.out.printf(indent + "Parsing Brackets %s:%n", token);

        // remove the start bracket
        tokenList.remove(operatorIndex);
        orders.remove(operatorIndex);

        int bracketCounter = 0;

        while (!tokenList.isEmpty()) {
            Token token = tokenList.remove(operatorIndex);

            System.out.printf(indent + "brackets : %s : %s%n", this.token, token);
            if (token.length() == 1 && Functions.equals(KEY_BRACKET_OPEN, token)) {
                bracketCounter ++;
            } else
            if (token.length() == 1 && Functions.equals(KEY_BRACKET_CLOSE, token)) {
                // replace closing bracket with a constant indicator
                bracketCounter --;
            }

            if (bracketCounter < 0) {
                OperationEvaluatorNode operationEvaluatorNode = new OperationEvaluatorNode(new Token(this.token.string, this.token.line), depth + 1);
                operationTokens.add(new Token(";", this.token.line));
                operationEvaluatorNode.evaluate(operationTokens, evaluator);

                // substitute the operator removed with a BracketToken
                // an integer is not added to orders because we are not removing one on this final loop
                tokenList.add(operatorIndex, new BracketToken("BRACKET", this.token.line, operationEvaluatorNode));
                members.add(operationEvaluatorNode);
                return this;
            }

            orders.remove(operatorIndex);
            operationTokens.add(token);
        }
        return null;
    }

    public EvaluatorNode evaluate(List<Token> tokenList, List<Integer> orders, Evaluator evaluator) {
        try {
            return evaluator(tokenList, orders, evaluator);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
            return null;
        }
    }
}