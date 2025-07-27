package src.evaluators;

import java.util.*;

import src.interfaces.*;
import src.structures.*;
import src.tokens.*;

/**
 * <h1> Class EvaluatorNode </h1>
 * Base AST Node
 * @author ElectricGun
 */

public class EvaluatorNode {

    protected List<MilyThrowable> milyThrowables = new ArrayList<>();

    public int depth;
    public Token token;
    // can be used to for storing assembly line numbers, or some other general information
    public Map<String, String> flags = new HashMap<>();
    protected List<EvaluatorNode> members = new ArrayList<>();

    public EvaluatorNode(Token token, int depth) {
        this.token = token;
        this.depth = depth;
    }

    public boolean isErrored() {
        return !milyThrowables.isEmpty();
    }

    public MilyThrowable getThrowable(int index) {
        return milyThrowables.get(index);
    }

    public int throwablesCount() {
        return milyThrowables.size();
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

    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree, boolean debugMode) throws Exception {
        throw new UnsupportedOperationException("This method is not yet implemented.");
    }

    public final EvaluatorNode evaluate(List<Token> tokenList, EvaluatorTree evaluatorTree, boolean debugMode) {
        try {
            EvaluatorNode newEvaluator = evaluator(tokenList, evaluatorTree, debugMode);

            // TODO: maybe for future optimisation
//            for (int i = 0; i < newEvaluator.memberCount(); i++) {
//                if (newEvaluator.getMember(i).errored) {
//                    this.errored = true;
//                }
//            }

            return newEvaluator;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
            return null;
        }
    }

    public EvaluatorNode throwSyntaxError(String message, Token token) {
        String errorMessage = String.format("Syntax error on line: %s, token: \"%s\": ", token.line, token) + message;
        this.milyThrowables.add(new MilySyntaxError(errorMessage));

        return this;
    }

    public EvaluatorNode throwSemanticError(String message, Token token) {
        String errorMessage = String.format("Semantic error on line: %s, token: \"%s\": ", token.line, token) + message;
        this.milyThrowables.add(new MilySemanticError(errorMessage));

        return this;
    }

    public EvaluatorNode throwThrowable(MilyThrowable throwable) {
        this.milyThrowables.add(throwable);

        return this;
    }

    public void printRecursive() {
        EvaluatorTree.printRecursive(this);
    }
}