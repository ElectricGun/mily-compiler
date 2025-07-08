package src.evaluators;

import java.util.*;
import src.constants.*;
import src.tokens.*;
import static src.constants.Functions.*;
import static src.constants.Keywords.*;

/**
 * @author ElectricGun
 * <h1> Parses code blocks </h1>
 * Purpose: finds variable and function declarations, function calls and return statements (if is child of a function)
 * Routes:
 * - keyword "let" -> DeclarationEvaluatorNode
 * - keyword "return" -> OperationEvaluatorNode
 * - any token + "(" -> FunctionCallEvaluatorNode
 */

public class ScopeEvaluatorNode extends EvaluatorNode {
    // if true, then the block is finalized after finding a '}'. Usually for functions
    public boolean needsClosing = false;
    public boolean expectingSemicolon = false;
    // if the block is a function block, this is the parent function
    public FunctionDeclareEvaluatorNode functionDeclareEvaluatorNode = null;
    public ScopeEvaluatorNode(Token name, int depth) {
        super(name, depth);
    }
    public ScopeEvaluatorNode(Token name, int depth, boolean needsClosing, FunctionDeclareEvaluatorNode functionDeclareEvaluatorNode) {
        super(name, depth);
        this.needsClosing = needsClosing;
        this.functionDeclareEvaluatorNode = functionDeclareEvaluatorNode;
    }

    private Token previousToken = null;
    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, Evaluator evaluator) throws Exception {
        String indent = " ".repeat(depth);

        System.out.printf(indent + "Parsing Block %s:%n", token);

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();

            System.out.printf(indent + "scope\t:\t%s\t:\t%s%n", this.token, token);

            buffer += token;


            if (isWhiteSpace(token)) {
                continue;
            }
            else if (expectingSemicolon) {
                if (!Functions.equals(KEY_SEMICOLON, token)) {
                    throw new Exception("Missing semicolon on line %s".formatted(token.line));
                    }
                expectingSemicolon = false;
            }
            else if (isPunctuation(token)) {
                if (isVariableName(previousToken) && Functions.equals(KEY_BRACKET_OPEN, token)) {
                    FunctionCallEvaluatorNode functionCallEvaluatorNode = new FunctionCallEvaluatorNode(previousToken, depth + 1);
                    functionCallEvaluatorNode.evaluate(tokenList, evaluator);
                    members.add(functionCallEvaluatorNode);
                    expectingSemicolon = true;
                    continue;
                }

                if (needsClosing && Functions.equals(KEY_CURLY_CLOSE, token)) {
                    System.out.printf("Created scope \"%s\"%n", this.token);
                    return this;
                }

                throw new Exception("Illegal punctuation on scope %s \"%s\" at line %s".formatted(this.token, token, token.line));

            }
            else if (isOperator(token)) {
                throw new Exception("Unexpected operator on scope %s: \"%s\" at line %s".formatted(this.token, token, token.line));
            } else {
                // RETURN STATEMENT FOR FUNCTIONS
                if (functionDeclareEvaluatorNode != null && Functions.equals(KEY_RETURN, token)) {
                    OperationEvaluatorNode returnOp = new OperationEvaluatorNode(new Token(this.token +"_return", this.token.line), depth + 1, true);
                    members.add(returnOp);
                    returnOp.evaluate(tokenList, evaluator);
                }
                // VARIABLE DECLARATION
                else if (Functions.equals(KEY_LET, token)) {
                    EvaluatorNode node = new DeclarationEvaluatorNode(token, depth + 1).evaluate(tokenList, evaluator);
                    members.add(node);
                }
            }

            previousToken = token;
        }
        if (needsClosing) {
            throw new Exception("Scope \"%s\" is unclosed".formatted(token));
        }
        return this;
    }

    @Override
    public String toString() {
        return ( "scope : " + token);
    }
}
