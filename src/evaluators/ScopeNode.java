package src.evaluators;

import java.util.*;
import src.constants.*;
import src.tokens.*;
import static src.constants.Functions.*;
import static src.constants.Keywords.*;

/**
 * <h3> Parses code blocks </h3>
 * Purpose: finds variable and function declarations, function calls and return statements (if is child of a function) <br>
 * Conditionals / Routes:
 *  <ul>
 *      <li> Token "let"                  -> {@link DeclarationNode}</li>
 *      <li> Token "return"               -> {@link OperationNode}</li>
 *      <li> Any token + "("              -> {@link FunctionCallNode}</li>
 *      <li> Token "if"                   -> {@link IfStatementNode}</li>
 *      <li> Token "while"                -> {@link WhileLoopNode}</li>
 *      <li> Token "for"                  -> {@link ForLoopNode}</li>
 *      <li> Token "}" when needs closing -> return this</li>
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
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree) throws Exception {
        String indent = " ".repeat(depth);

        System.out.printf(indent + "Parsing Block %s:%n", token);

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();

            System.out.printf(indent + "scope\t:\t%s\t:\t%s%n", this.token, token);

            if (isWhiteSpace(token)) {
                continue;

            } else if (expectingSemicolon) {
                if (!Functions.equals(KEY_SEMICOLON, token)) {
                    throw new Exception("Missing semicolon on line %s".formatted(token.line));
                    }
                expectingSemicolon = false;


            } else if (isVariableName(previousToken)) {
                if (Functions.equals(KEY_BRACKET_OPEN, token)) {
                    FunctionCallNode functionCallNode = new FunctionCallNode(previousToken, depth + 1);
                    members.add(functionCallNode.evaluate(tokenList, evaluatorTree));
                    expectingSemicolon = true;

                } else if (Functions.equals(KEY_OP_ASSIGN, token)) {
                    AssignmentNode assignmentNode = new AssignmentNode(previousToken, depth + 1);
                    members.add(assignmentNode.evaluate(tokenList, evaluatorTree));

                } else {
                    throw new Exception("Cannot resolve symbol \"%s\" at line %s".formatted(previousToken, previousToken.line));
                }
                // clear previous token otherwise it won't be true to reality
                // as the evaluators below will consume newer tokens
                previousToken = null;

                continue;

            } else if (isPunctuation(token)) {
                if (needsClosing && Functions.equals(KEY_CURLY_CLOSE, token)) {
                    System.out.printf(indent + "Created scope \"%s\"%n", this.token);
                    return this;

                } else {
                    throw new Exception("Illegal punctuation on scope %s \"%s\" at line %s".formatted(this.token, token, token.line));
                }

            } else if (isOperator(token)) {
                throw new Exception("Unexpected operator on scope %s: \"%s\" at line %s".formatted(this.token, token, token.line));

            } else if (Functions.equals(KEY_CONDITIONAL_IF, token)) {
                System.out.printf(indent + "Creating if statement loop %n");
                IfStatementNode ifStatementEvaluatorNode = new IfStatementNode(token, depth+1);
                members.add(ifStatementEvaluatorNode.evaluate(tokenList, evaluatorTree));

            } else if (Functions.equals(KEY_LOOPING_WHILE, token)) {
                System.out.printf(indent + "Creating while loop %n");
                WhileLoopNode whileLoopEvaluatorNode = new WhileLoopNode(token, depth+1);
                members.add(whileLoopEvaluatorNode.evaluate(tokenList, evaluatorTree));

            } else if (Functions.equals(KEY_LOOPING_FOR, token)) {
                System.out.printf(indent + "Creating for loop %n");
                ForLoopNode forLoopNode = new ForLoopNode(token, depth+1);
                members.add(forLoopNode.evaluate(tokenList, evaluatorTree));

            } else {
                // RETURN STATEMENT FOR FUNCTIONS
                if (functionDeclareNode != null && Functions.equals(KEY_RETURN, token)) {
                    OperationNode returnOp = new OperationNode(new Token(this.token +"_return", this.token.line), depth + 1, true);
                    members.add(returnOp.evaluate(tokenList, evaluatorTree));

                } else if (Functions.equals(KEY_LET, token)) {
                    // VARIABLE DECLARATION
                    EvaluatorNode node = new DeclarationNode(token, depth + 1).evaluate(tokenList, evaluatorTree);
                    members.add(node);
                }
            }
            previousToken = token;
        } if (needsClosing) {
            // after running out of tokens
            throw new Exception("Scope \"%s\" is unclosed".formatted(token));
        }
        return this;
    }

    @Override
    public String toString() {
        return ( "scope : " + token);
    }
}
