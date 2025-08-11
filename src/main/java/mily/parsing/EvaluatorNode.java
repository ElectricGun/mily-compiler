package mily.parsing;

import java.util.*;

import mily.abstracts.*;
import mily.structures.errors.*;
import mily.tokens.*;

/**
 * <h1> Class EvaluatorNode </h1>
 * Base AST Node
 *
 * @author ElectricGun
 */

public class EvaluatorNode {

    private final String errorTemplate = "%s on file \"%s\", line: %s, token: \"%s\": ";
    public int depth;
    public Token nameToken;
    // for storing general information
    protected Map<String, String> flags = new HashMap<>();
    protected List<MilyThrowable> throwables = new ArrayList<>();
    protected List<EvaluatorNode> members = new ArrayList<>();

    public EvaluatorNode(Token nameToken, int depth) {
        this.nameToken = nameToken;
        this.depth = depth;
    }

    public void putFlag(String flag, String value) {
        flags.put(flag, value);
    }

    public String getFlagValue(String flag) {
        return flags.get(flag);
    }

    public boolean isErrored() {
        return !throwables.isEmpty();
    }

    public MilyThrowable getThrowable(int index) {
        return throwables.get(index);
    }

    public int throwablesCount() {
        return throwables.size();
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

    @SuppressWarnings("CallToPrintStackTrace")
    public final EvaluatorNode evaluate(List<Token> tokenList, EvaluatorTree evaluatorTree, boolean debugMode) {
        try {
            return evaluator(tokenList, evaluatorTree, debugMode);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
            return null;
        }
    }

    // TODO: use polymorphism instead
    public EvaluatorNode throwSyntaxError(String message, Token token) {
        String errorMessage = String.format(errorTemplate, "Syntax error", token.source, token.line, token) + message;
        this.throwables.add(new MilySyntaxError(errorMessage));

        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public EvaluatorNode throwSemanticError(String message, Token token) {
        String errorMessage = String.format(errorTemplate, "Semantic error", token.source, token.line, token) + message;
        this.throwables.add(new MilySemanticError(errorMessage));

        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public EvaluatorNode throwTypeError(String message, Token token) {
        String errorMessage = String.format(errorTemplate, "Type error", token.source, token.line, token) + message;
        this.throwables.add(new MilyTypeError(errorMessage));

        return this;
    }

    @SuppressWarnings("unused")
    public EvaluatorNode throwThrowable(MilyThrowable throwable) {
        this.throwables.add(throwable);

        return this;
    }

    @SuppressWarnings("unused")
    public void printRecursive() {
        EvaluatorTree.printRecursive(this);
    }

    public String indent() {
        return indent(depth);
    }

    public String indent(int depth) {
        return " ".repeat(depth);
    }
}