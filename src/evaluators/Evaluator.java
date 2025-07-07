package src.evaluators;

import src.tokens.Token;

import java.util.List;
public class Evaluator {
    public ScopeEvaluatorNode mainBlock = new ScopeEvaluatorNode(new Token("__MAIN__", 1), 0);

    public EvaluatorNode begin(List<Token> tokenList) {
        mainBlock.evaluate(tokenList, this);
        return mainBlock;
    }

    public static void printRecursive(EvaluatorNode node) {
        printRecursiveHelper(node, 0);
    }
    protected static void printRecursiveHelper(EvaluatorNode node, int depth) {
        String prefix = depth > 0 ? "| ".repeat(depth) : "";
        System.out.println(prefix + node);
        depth++;
        for (EvaluatorNode member : node.members) {
            printRecursiveHelper(member, depth);
        }
    }
}

