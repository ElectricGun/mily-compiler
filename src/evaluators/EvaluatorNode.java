package src.evaluators;

import java.util.*;
import src.tokens.Token;

public class EvaluatorNode {
    public int depth;
    public Token token;
    public String buffer = "";
    public List<EvaluatorNode> members = new ArrayList<>();

    public EvaluatorNode(Token token, int depth) {
        this.token = token;
        this.depth = depth;
    }
    protected EvaluatorNode evaluator(List<Token> tokenList, Evaluator evaluator) throws Exception {
        throw new UnsupportedOperationException("This method is not yet implemented.");
    }

    public final EvaluatorNode evaluate(List<Token> tokenList, Evaluator evaluator) {
        try {
            return evaluator(tokenList, evaluator);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
            return null;
        }
    }

    public void printRecursive() {
        Evaluator.printRecursive(this);
    }
}