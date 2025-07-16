package src.evaluators;

import java.util.*;
import src.tokens.*;

/**
 * <h3> The container for the syntax tree </h3>
 * @author ElectricGun
 */

public class EvaluatorTree {

    public ScopeNode mainBlock = new ScopeNode(new Token("__MAIN__", 1), 0);

    public EvaluatorNode begin(List<Token> tokenList) {
        mainBlock.evaluate(tokenList, this);
        return mainBlock;
    }

    public void printRecursive() {
        printRecursive(mainBlock);
    }

    public static void printRecursive(EvaluatorNode node) {
        printRecursiveHelper(node, 0);
    }

    protected static void printRecursiveHelper(EvaluatorNode node, int depth) {
        if (node == null)
            return;

        String prefix = depth > 0 ? "| ".repeat(depth) : "";
        System.out.println(prefix + node);
        depth++;

        for (EvaluatorNode member : node.members) {
            printRecursiveHelper(member, depth);
        }
    }
}

