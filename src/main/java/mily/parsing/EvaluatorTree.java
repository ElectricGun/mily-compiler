package mily.parsing;

import mily.tokens.*;

import java.util.*;

/**
 * <h1> Class EvaluatorTree </h1>
 * Parsing Abstract Syntax Tree <br>
 * The container for the parsing AST
 *
 * @author ElectricGun
 */

public class EvaluatorTree {

    public final boolean debugMode;
    public final ScopeNode mainBlock = new ScopeNode(new Token("__MAIN__", "__MAIN__", 1), 0);
    public String name = "";

    public EvaluatorTree(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public EvaluatorTree(String name, boolean debugMode) {
        this.name = name;
        this.debugMode = debugMode;
    }

    public static void printRecursive(EvaluatorNode node) {
        printRecursiveHelper(node, 0);
    }

    protected static void printRecursiveHelper(EvaluatorNode node, int depth) {
        if (node == null)
            System.out.println("null");

        String prefix = depth > 0 ? "| ".repeat(depth) : "";
        System.out.println(prefix + node);
        depth++;

        assert node != null;
        for (EvaluatorNode member : node.members) {
            printRecursiveHelper(member, depth);
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public EvaluatorNode begin(List<Token> tokenList) {
        mainBlock.evaluate(tokenList, this);
        return mainBlock;
    }

    public void printRecursive() {
        printRecursive(mainBlock);
    }
}

