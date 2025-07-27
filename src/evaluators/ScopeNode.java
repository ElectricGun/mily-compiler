package src.evaluators;

import java.util.*;
import src.constants.*;
import src.tokens.*;

import static src.constants.Functions.*;
import static src.constants.Keywords.*;

/**
 * <h1> Class ScopeNode </h1>
 * Code Block Parser
 * Purpose: finds variable and function declarations, function calls and return statements (if is child of a function) <br>
 * Routes:
 *  <ul>
 *      <li> {@link DeclarationNode}</li>
 *      <li> {@link OperationNode}</li>
 *      <li> {@link FunctionCallNode}</li>
 *      <li> {@link IfStatementNode}</li>
 *      <li> {@link WhileLoopNode}</li>
 *      <li> {@link ForLoopNode}</li>
 * </ul>
 * @author ElectricGun
 */

public class ScopeNode extends EvaluatorNode {

    // if true, then the block is finalized after finding a '}'. Usually for functions
    public boolean needsClosing = false;
    public boolean expectingSemicolon = false;
    // if the block is a function block, this is the parent function
    public FunctionDeclareNode functionDeclareNode = null;

    private Token previousToken = null;

    public ScopeNode(Token name, int depth) {
        super(name, depth);
    }

    public ScopeNode(Token name, int depth, boolean needsClosing) {
        super(name, depth);
        this.needsClosing = needsClosing;
    }

    public ScopeNode(Token name, int depth, boolean needsClosing, FunctionDeclareNode functionDeclareNode) {
        super(name, depth);
        this.needsClosing = needsClosing;
        this.functionDeclareNode = functionDeclareNode;
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree, boolean debugMode) throws Exception {
        String indent = " ".repeat(depth);

        if (debugMode)
            System.out.printf(indent + "Parsing Block %s:%n", token);

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();

            if (debugMode)
                System.out.printf(indent + "block : %s%n", token);


            if (isWhiteSpace(token)) {
                continue;

            } else if (expectingSemicolon) {
                if (!keyEquals(KEY_SEMICOLON, token)) {

                    return throwSyntaxError("Missing semicolon", token);
                    }
                expectingSemicolon = false;

            } else if (isDeclaratorAmbiguous(previousToken)) {
                if (isVariableName(previousToken) && keyEquals(KEY_BRACKET_OPEN, token)) {
                    FunctionCallNode functionCallNode = new FunctionCallNode(previousToken, depth + 1);
                    members.add(functionCallNode.evaluate(tokenList, evaluatorTree, debugMode));
                    expectingSemicolon = true;

                } else if (isVariableName(previousToken) && keyEquals(KEY_OP_ASSIGN, token)) {
                    AssignmentNode assignmentNode = new AssignmentNode(previousToken, depth + 1);
                    members.add(assignmentNode.evaluate(tokenList, evaluatorTree, debugMode));

                } else if (isVariableName(token)) {
                    // VARIABLE DECLARATION
                    EvaluatorNode node = new DeclarationNode(previousToken.string, token, depth + 1).evaluate(tokenList, evaluatorTree, debugMode);
                    members.add(node);

                } else {
                    return throwSyntaxError("Invalid token", token);
                }
                // clear previous token otherwise it won't be true to reality
                // as the evaluators below will consume newer tokens
                previousToken = null;
                continue;

            } else if (isPunctuation(token)) {
                if (needsClosing && keyEquals(KEY_CURLY_CLOSE, token)) {
                    if (debugMode)
                        System.out.printf(indent + "Created scope \"%s\"%n", this.token);
                    return this;

                } else {
                    return throwSyntaxError("Illegal punctuation in scope", token);
                }

            } else if (isOperator(token)) {
                return throwSyntaxError("Unexpected operator in scope", token);

            } else if (keyEquals(KEY_CONDITIONAL_IF, token)) {
                if (debugMode)
                    System.out.printf(indent + "Creating if statement loop %n");
                IfStatementNode ifStatementEvaluatorNode = new IfStatementNode(token, depth+1);
                members.add(ifStatementEvaluatorNode.evaluate(tokenList, evaluatorTree, debugMode));

            } else if (keyEquals(KEY_LOOPING_WHILE, token)) {
                if (debugMode)
                    System.out.printf(indent + "Creating while loop %n");
                WhileLoopNode whileLoopEvaluatorNode = new WhileLoopNode(token, depth+1);
                members.add(whileLoopEvaluatorNode.evaluate(tokenList, evaluatorTree, debugMode));

            } else if (keyEquals(KEY_LOOPING_FOR, token)) {
                if (debugMode)
                    System.out.printf(indent + "Creating for loop %n");
                ForLoopNode forLoopNode = new ForLoopNode(token, depth+1);
                members.add(forLoopNode.evaluate(tokenList, evaluatorTree, debugMode));

            } else if (/*functionDeclareNode != null &&*/ keyEquals(KEY_RETURN, token)) {
                // FUNCTION RETURN
                OperationNode returnOp = new OperationNode(new Token(this.token + "_return", token.line), depth + 1, true);
                members.add(returnOp.evaluate(tokenList, evaluatorTree, debugMode));
            }
            previousToken = token;
        }

        // after running out of tokens
        if (needsClosing) {
            return throwSyntaxError("Scoped is undeclared", token);
        }

        if (isDeclaratorAmbiguous(previousToken)) {
            return throwSyntaxError("Unexpected end of file", token);
        }

        return this;
    }

    @Override
    public String toString() {
        return ( "scope : " + token + "     #" + hashCode());
    }
}
