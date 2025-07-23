package src.evaluators;

import java.util.*;

import src.interfaces.MilyThrowable;
import src.tokens.*;

/**
 * <h1> Class EvaluatorTree </h1>
 * Parsing Abstract Syntax Tree <br>
 * The container for the parsing AST
 * @author ElectricGun
 */

public class EvaluatorTree {

    public String name = "";
    public ScopeNode mainBlock = new ScopeNode(new Token("__MAIN__", 1), 0);

    protected final boolean debugMode;

    public EvaluatorTree(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public EvaluatorTree(String name, boolean debugMode) {
        this.name = name;
        this.debugMode = debugMode;
    }

    public EvaluatorNode begin(List<Token> tokenList) {
        mainBlock.evaluate(tokenList, this, debugMode);
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

