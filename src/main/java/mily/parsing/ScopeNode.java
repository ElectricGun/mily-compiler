package mily.parsing;

import java.util.*;

import mily.parsing.callables.*;
import mily.parsing.invokes.*;
import mily.structures.errors.JavaMilySyntaxException;
import mily.structures.structs.Type;
import mily.tokens.*;

import static mily.constants.Functions.*;
import static mily.constants.Keywords.*;

/**
 * <h1> Class ScopeNode </h1>
 * Code Block Parser
 * Purpose: finds variable and function declarations, function calls and return statements (if is child of a function) <br>
 * Routes:
 *  <ul>
 *      <li> {@link DeclarationNode}</li>
 *      <li> {@link OperationNode}</li>
 *      <li> {@link IfStatementNode}</li>
 *      <li> {@link WhileLoopNode}</li>
 *      <li> {@link ForLoopNode}</li>
 * </ul>
 *
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

    public static EvaluatorNode processDeclarationDatatype(Token token, Token previousToken, List<Token> tokenList, EvaluatorTree evaluatorTree, int depth) throws JavaMilySyntaxException {
        // add the token back because it has been consumed
        tokenList.add(0, token);
        Type type = DatatypeNode.processType(previousToken, tokenList, evaluatorTree);

        Token varName = EvaluatorNode.fetchNextNonWhitespaceToken(tokenList);

        return new DeclarationNode(type, varName, depth + 1).evaluate(tokenList, evaluatorTree);
    }

    @Override
    public String errorName() {
        return "scope " + "\"" + nameToken.string + "\"";
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree) {
        String indent = " ".repeat(depth);

        if (evaluatorTree.debugMode)
            System.out.printf(indent + "Parsing Block %s:%n", nameToken);

        while (!tokenList.isEmpty()) {
            Token token = tokenList.remove(0);
            if (evaluatorTree.debugMode)
                System.out.printf(indent + "block : %s%n", token);


            if (isWhiteSpace(token)) {
                continue;

            } else if (expectingSemicolon) {
                if (!keyEquals(KEY_SEMICOLON, token)) {

                    return throwSyntaxError("Missing semicolon", token);
                }
                expectingSemicolon = false;

            } else if (isVariableOrDeclarator(previousToken)) {
                if (isVariableName(previousToken) && keyEquals(KEY_BRACKET_OPEN, token)) {
                    // FUNCTION CALL
                    CallerNode functionCallNode = new CallerNode(previousToken.string, previousToken, depth + 1);
                    members.add(functionCallNode.evaluate(tokenList, evaluatorTree));
                    expectingSemicolon = true;

                } else if (isVariableName(previousToken) && keyEquals(KEY_OP_ASSIGN, token)) {
                    // ASSIGNMENT
                    AssignmentNode assignmentNode = new AssignmentNode(previousToken, depth + 1);
                    members.add(assignmentNode.evaluate(tokenList, evaluatorTree));

                } else {
                    // VARIABLE DECLARATION
                    try {
                        members.add(processDeclarationDatatype(token, previousToken, tokenList, evaluatorTree, depth + 1));

                    } catch (JavaMilySyntaxException e) {
                        return throwSyntaxError("Unexpected end of file", token);
                    }
                }
                // clear previous token otherwise it won't be true to reality
                // as the evaluators below will consume newer tokens
                previousToken = null;
                continue;

            } else if (isPunctuation(token)) {
                if (needsClosing && keyEquals(KEY_CURLY_CLOSE, token)) {
                    if (evaluatorTree.debugMode)
                        System.out.printf(indent + "Created scope \"%s\"%n", this.nameToken);
                    return this;

                } else {
                    return throwSyntaxError("Illegal punctuation in scope", token);
                }

            } else if (isOperator(token)) {
                return throwSyntaxError("Unexpected operator in scope", token);

            } else if (keyEquals(KEY_CONDITIONAL_IF, token)) {
                if (evaluatorTree.debugMode)
                    System.out.printf(indent + "Creating if statement loop %n");
                IfStatementNode ifStatementEvaluatorNode = new IfStatementNode(token, depth + 1);
                members.add(ifStatementEvaluatorNode.evaluate(tokenList, evaluatorTree));

            } else if (keyEquals(KEY_LOOPING_WHILE, token)) {
                if (evaluatorTree.debugMode)
                    System.out.printf(indent + "Creating while loop %n");
                WhileLoopNode whileLoopEvaluatorNode = new WhileLoopNode(token, depth + 1);
                members.add(whileLoopEvaluatorNode.evaluate(tokenList, evaluatorTree));

            } else if (keyEquals(KEY_LOOPING_FOR, token)) {
                if (evaluatorTree.debugMode)
                    System.out.printf(indent + "Creating for loop %n");
                ForLoopNode forLoopNode = new ForLoopNode(token, depth + 1);
                members.add(forLoopNode.evaluate(tokenList, evaluatorTree));

            } else if (keyEquals(KEY_RAW, token)) {
                if (evaluatorTree.debugMode)
                    System.out.printf(indent + "Creating for raw template %n");

                Token returnType = tokenList.remove(0);

                while (returnType.isWhiteSpace()) {
                    returnType = tokenList.remove(0);
                }
                Type type = DatatypeNode.processType(returnType, tokenList, evaluatorTree);

                Token templateName = tokenList.remove(0);

                while (templateName.isWhiteSpace()) {
                    templateName = tokenList.remove(0);
                }

                if (!isVariableName(templateName)) {
                    return throwSyntaxError("Expecting template name", templateName);
                }

                RawTemplateDeclareNode rawTemplateDeclareNode = new RawTemplateDeclareNode(templateName.string, type, token, depth + 1);
                members.add(rawTemplateDeclareNode.evaluate(tokenList, evaluatorTree));

            } else if (/*functionDeclareNode != null &&*/ keyEquals(KEY_RETURN, token)) {
                // FUNCTION RETURN
                OperationNode returnOp = new OperationNode(new Token(this.nameToken + "_return", token.source, token.line), depth + 1, true);
                members.add(returnOp.evaluate(tokenList, evaluatorTree));
            }
            previousToken = token;
        }

        // after running out of tokens
        if (needsClosing) {
            return throwSyntaxError("Scoped is undeclared", nameToken);
        }

        if (isVariableOrDeclarator(previousToken)) {
            return throwSyntaxError("Unexpected end of file", nameToken);
        }

        return this;
    }

    @Override
    public String toString() {
        return ("scope : " + nameToken + "     #" + hashCode());
    }
}
