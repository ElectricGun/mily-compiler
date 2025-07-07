package src.evaluators;

import java.util.*;
import src.constants.*;
import src.tokens.*;

import static src.constants.Vars.*;
import static src.constants.Keys.*;

public class ScopeEvaluatorNode extends EvaluatorNode {
    // if true, then the block is finalized after finding a '}'. Usually for functions
    public boolean needsClosing = false;
    // if the block is a function block, this is the parent function
    public FunctionEvaluatorNode functionEvaluatorNode = null;
    public ScopeEvaluatorNode(Token name, int depth) {
        super(name, depth);
    }
    public ScopeEvaluatorNode(Token name, int depth, boolean needsClosing, FunctionEvaluatorNode functionEvaluatorNode) {
        super(name, depth);
        this.needsClosing = needsClosing;
        this.functionEvaluatorNode = functionEvaluatorNode;
    }
    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, Evaluator evaluator) throws Exception {
        String indent = " ".repeat(depth);

        boolean isInitialized = false;
        Token previousElementToken = new Token("", -1);

        System.out.printf(indent + "Parsing Block %s:%n", token);

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();

            System.out.printf(indent + "scope\t:\t%s\t:\t%s%n", this.token, token);

            buffer += token;

            // evaluate punctuations
            if (token.length() == 1 && isPunctuation(token)) {
                char c = token.charAt(0);

                if (isWhiteSpace(c)) {
                    continue;
                }
                if (isPunctuation(c) && !isInitialized)
                    throw new Exception("Illegal punctuation on scope %s \"%s\" at line %s".formatted(this.token, c, token.line));

                // expect new function '(', or equals '='
                // FUNCTION DECLARATION
                if (Vars.equals(KEY_BRACKET_OPEN, token)) {
                    System.out.printf(indent + "Creating new function \"%s\"%n", previousElementToken);
                    EvaluatorNode node = new FunctionEvaluatorNode(previousElementToken, depth + 1).evaluate(tokenList, evaluator);
                    members.add(node);
                }
                else if (needsClosing && Vars.equals(KEY_CURLY_CLOSE, token)) {
                    System.out.printf("Created scope \"%s\"%n", this.token);
                    return this;
                }
                else {
                    throw new Exception("Unexpected token on scope %s: \"%s\" at line %s".formatted(this.token, c, token.line));
                }
            }
            // evaluate operators
            else if (isOperator(token)) {
                throw new Exception("Unexpected operator on scope %s: \"%s\" at line %s".formatted(this.token, token, token.line));
                // evaluate the rest
            } else {
                // RETURN STATEMENT FOR FUNCTIONS
                if (functionEvaluatorNode != null && Vars.equals(KEY_RETURN, token)) {
                    OperationEvaluatorNode returnOp = new ReturnOperationEvaluatorNode(new Token(this.token +"_return", this.token.line), depth + 1);
                    members.add(returnOp);
                    returnOp.evaluate(tokenList, evaluator);
                }
                // VARIABLE DECLARATION
                else if (Vars.equals(KEY_LET, previousElementToken)) {
                    EvaluatorNode node = new DeclarationEvaluatorNode(token, depth + 1).evaluate(tokenList, evaluator);
                    members.add(node);
                }
                previousElementToken = token;
                isInitialized = true;
            }
        }
        if (needsClosing) {
            throw new Exception("Scope \"%s\" is unclosed".formatted(token));
        }
        return this;
    }

    @Override
    public String toString() {
        return ((functionEvaluatorNode != null ? "function " : "")  + "scope : " + token);
    }
}
