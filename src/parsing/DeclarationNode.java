package src.parsing;

import src.tokens.*;
import java.util.*;

import static src.constants.Functions.*;
import static src.constants.Keywords.*;

/**
 * <h1> Class DeclarationNode </h1>
 * Variable and Function Declaration
 *  @author ElectricGun
 */

public class DeclarationNode extends VariableNode {

    public DeclarationNode(String type, Token token, int depth) {
        super(type, token, depth);
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree, boolean debugMode) throws Exception {
        String indent = " ".repeat(depth);

        if (debugMode)
            System.out.printf(indent + "Parsing Variable Declaration %s:%n", nameToken);

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();

            if (debugMode)
                System.out.printf(indent + "declaration :  %s : %s%n", this.nameToken, token);

            // evaluate punctuations
            if (isPunctuation(token)) {
                if (isWhiteSpace(token)) {
                    continue;

                } else if (keyEquals(KEY_BRACKET_OPEN, token) && isDeclared()) {
                    // FUNCTION DECLARATION
                    if (debugMode)
                        System.out.printf(indent + "Creating new function \"%s\"%n", this.nameToken);

                    EvaluatorNode node = new FunctionDeclareNode(new Token(variableName, token.line), depth + 1).evaluate(tokenList, evaluatorTree, debugMode);
                    members.add(node);
                    return this;

                } else {
                    return throwSyntaxError("Unexpected punctuation on variable declaration", token);
                }
            }
            // evaluate operators
            else if (isOperator(token)) {

                if (debugMode)
                    System.out.println(variableName);
                // check for equal sign
                if (keyEquals(KEY_OP_ASSIGN, token) && isDeclared()) {
                    OperationNode operationNode = new OperationNode(new Token("op_"+ this.nameToken, this.nameToken.line), depth + 1);
                    members.add(operationNode.evaluate(tokenList, evaluatorTree, debugMode));
                    return this;

                } else {
                    return throwSyntaxError("Missing '=' sign", token);

                }
            }
            // evaluate strings
            else if (!isKeyWord(token)) {
                if (!isDeclared()) {
                    if (debugMode)
                        System.out.printf(indent + "Declaring variable name : %s%n", token.string);
                    variableName = token.string;

                } else {
                    return throwSyntaxError("Unexpected token on variable declaration", token);

                }
            }
            // evaluate the rest
            else {
                return throwSyntaxError("Unexpected token on variable declaration", token);

            }
        }
        return throwSyntaxError("Unexpected end of file", nameToken);

    }

    @Override
    public String toString() {
        return "declare %s %s :=".formatted(type, variableName);
    }
}