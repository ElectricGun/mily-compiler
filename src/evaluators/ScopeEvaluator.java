package src.evaluators;

import src.tokens.Token;

import java.util.*;

import static src.Vars.*;

public class ScopeEvaluator extends EvaluatorNode {
    // if true, then the block is finalized after finding a '}'. Usually for functions
    public boolean needsClosing = false;
    public FunctionEvaluator functionBlock = null;
    public ScopeEvaluator(Token name, int depth) {
        super(name, depth);
    }
    public ScopeEvaluator(Token name, int depth, boolean needsClosing, FunctionEvaluator functionBlock) {
        super(name, depth);
        this.needsClosing = needsClosing;
        this.functionBlock = functionBlock;
    }
    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, Evaluator evaluator) throws Exception {
        String indent = " ".repeat(depth);

        boolean isInitialized = false;
        Token previousElementToken = new Token("", -1);

        System.out.printf(indent + "Parsing Block %s:%n", name);

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();

            System.out.printf(indent + "scope\t:\t%s\t:\t%s%n",name, token);

            buffer += token;

            // evaluate punctuations
            if (token.length() == 1 && isPunctuation(token.charAt(0))) {
                char c = token.charAt(0);
                if (isWhiteSpace(c)) {
                    continue;
                }
                if (isPunctuation(c) && !isInitialized)
                    throw new Exception("Illegal punctuation on scope %s \"%s\" at line %s".formatted(name, c, token.line));

                // expect new function '(', or equals '='
                // FUNCTION DECLARATION
                if (CHAR_BRACKET_OPEN == c) {
                    System.out.printf(indent + "Creating new function \"%s\"%n", previousElementToken);
                    EvaluatorNode node = new FunctionEvaluator(previousElementToken, depth + 1).evaluate(tokenList, evaluator);
                    members.add(node);
                }
                else if (needsClosing && CHAR_CURLY_CLOSE == c) {
                    System.out.printf("Created scope \"%s\"%n", name);
                    return this;
                }
                else {
                    throw new Exception("Unexpected token on scope %s: \"%s\" at line %s".formatted(name, c, token.line));
                }
            }
            // evaluate operators
            else if (isOperator(token)) {
                throw new Exception("Unexpected operator on scope %s: \"%s\" at line %s".formatted(name, token, token.line));
                // evaluate the rest
            } else {
                // RETURN STATEMENT FOR FUNCTIONS
                if (functionBlock != null && token.string.equals(KEYWORD_RETURN)) {
                    OperationEvaluator returnOp = new ReturnOperationEvaluator(new Token(name+"_return", name.line), depth + 1);
                    members.add(returnOp);
                    returnOp.evaluate(tokenList, evaluator);
                }
                // VARIABLE DECLARATION
                else if (previousElementToken.string.equals(KEYWORD_LET)) {
                    EvaluatorNode node = new DeclarationEvaluator(token, depth + 1).evaluate(tokenList, evaluator);
                    members.add(node);
                }
                previousElementToken = token;
                isInitialized = true;
            }
        }
        if (needsClosing) {
            throw new Exception("Scope \"%s\" is unclosed".formatted(name));
        }
        return this;
    }

    @Override
    public String toString() {
        return ((functionBlock != null ? "function " : "")  + "scope : " + name);
    }
}
