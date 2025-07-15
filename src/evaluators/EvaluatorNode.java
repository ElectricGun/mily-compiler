package src.evaluators;

import java.util.*;
import src.tokens.*;

/**
 * <h3> Base abstract syntax tree node </h3>
 * @author ElectricGun
 */

public class EvaluatorNode {

    public int depth;
    public Token token;
    public Map<String, String> flags = new HashMap<>();
    protected List<EvaluatorNode> members = new ArrayList<>();

    public EvaluatorNode(Token token, int depth) {
        this.token = token;
        this.depth = depth;
    }

    public int memberCount() {
        return members.size();
    }

    public void replaceMember(EvaluatorNode replaced, EvaluatorNode replacement) {
        members.set(members.indexOf(replaced), replacement);
    }

    public EvaluatorNode getMember(int i) {
        return members.get(i);
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