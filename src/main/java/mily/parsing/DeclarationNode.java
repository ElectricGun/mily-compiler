package mily.parsing;

import mily.parsing.callables.*;
import mily.structures.dataobjects.*;
import mily.tokens.*;

import java.util.*;

import static mily.constants.Functions.*;
import static mily.constants.Keywords.*;

/**
 * <h1> Class DeclarationNode </h1>
 * Variable and Function Declaration
 *
 * @author ElectricGun
 */

public class DeclarationNode extends VariableNode {

    public DeclarationNode(Type type, Token token, int depth) {
        super(type, token, depth);
    }

    @Override
    public String errorName() {
        return "declare " + "\"" + getName() + "\"";
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree) {
        String indent = " ".repeat(depth);

        if (evaluatorTree.debugMode)
            System.out.printf(indent + "Parsing Variable Declaration %s:%n", nameToken);

        while (!tokenList.isEmpty()) {
            Token token = tokenList.remove(0);
            if (evaluatorTree.debugMode)
                System.out.printf(indent + "declaration :  %s : %s%n", this.nameToken, token);

            // evaluate punctuations
            if (isPunctuation(token)) {
                if (isWhiteSpace(token)) {
                    continue;
                }

                if (keyEquals(KEY_BRACKET_OPEN, token) && isDeclared()) {
                    // FUNCTION DECLARATION
                    if (evaluatorTree.debugMode)
                        System.out.printf(indent + "Creating new function \"%s\"%n", this.nameToken);

                    return new FunctionDeclareNode(variableName, this.getType(), new Token(variableName, nameToken.source, token.line), depth + 1).evaluate(tokenList, evaluatorTree);

                } else {
                    return throwSyntaxError("Unexpected punctuation on variable declaration", token);
                }
            }
            // evaluate operators
            else if (isOperator(token)) {

                if (evaluatorTree.debugMode)
                    System.out.println(variableName);
                // check for equal sign
                if (keyEquals(KEY_OP_ASSIGN, token) && isDeclared()) {
                    OperationNode operationNode = new OperationNode(new Token("op_" + this.nameToken, this.nameToken.source, this.nameToken.line), depth + 1);
                    members.add(operationNode.evaluate(tokenList, evaluatorTree));
                    return this;

                } else {
                    return throwSyntaxError("Missing '=' sign", token);
                }
            }
            // evaluate strings
            else if (!isKeyWord(token)) {
                if (!isDeclared()) {
                    if (evaluatorTree.debugMode)
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
        return String.format("declare %s %s :=", type, variableName);
    }
}