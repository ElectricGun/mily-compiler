package src.evaluators;

import src.tokens.Token;

import java.util.ArrayList;
import java.util.List;

public class EvaluatorNode {
    public int depth;
    public Token name;
    public String buffer = "";
    public List<EvaluatorNode> members = new ArrayList<>();

    public EvaluatorNode(Token name, int depth) {
        this.name = name;
        this.depth = depth;
    }
    protected EvaluatorNode evaluator(List<Token> tokenList, Evaluator evaluator) throws Exception {
        throw new UnsupportedOperationException("This method is not yet implemented.");
    }

    public EvaluatorNode evaluate(List<Token> tokenList, Evaluator evaluator) {
        try {
            return evaluator(tokenList, evaluator);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
            return null;
        }
    }
}