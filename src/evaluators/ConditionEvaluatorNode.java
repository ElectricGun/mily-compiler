package src.evaluators;

import src.tokens.Token;

import java.util.List;

public class ConditionEvaluatorNode extends EvaluatorNode{

    String conditionalType;

    public ConditionEvaluatorNode(Token token, int depth, String conditionalType) {
        super(token, depth);

        this.conditionalType = conditionalType;
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, Evaluator evaluator) throws Exception {

        String indent = " ".repeat(depth);
        System.out.printf(indent + "Parsing %s Conditional %s:%n", conditionalType, token);

        while (!tokenList.isEmpty()) {

        }
        return super.evaluator(tokenList, evaluator);
    }
}
