package src.evaluators;

import src.constants.*;
import src.tokens.*;
import java.util.*;
import static src.constants.Functions.*;
import static src.constants.Keywords.*;

/**
 * <h3> Variable and Function Declaration </h3>
 *  @author ElectricGun
 */

public class DeclarationEvaluatorNode extends EvaluatorNode {

    String variableName = "";

    public DeclarationEvaluatorNode(Token name, int depth) {
        super(name, depth);
    }

    public String getVariableName() {
        return variableName;
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, Evaluator evaluator) throws Exception {
        String indent = " ".repeat(depth);
        System.out.printf(indent + "Parsing Variable Declaration %s:%n", token);

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();
            System.out.printf(indent + "declaration :  %s : %s%n", this.token, token);

            // evaluate punctuations
            if (isPunctuation(token)) {
                if (isWhiteSpace(token)) {
                    continue;

                }
                // FUNCTION DECLARATION
                else if (Functions.equals(KEY_BRACKET_OPEN, token) && isDeclared()) {
                    System.out.printf(indent + "Creating new function \"%s\"%n", this.token);
                    EvaluatorNode node = new FunctionDeclareEvaluatorNode(new Token(variableName, token.line), depth + 1).evaluate(tokenList, evaluator);
                    members.add(node);
                    return this;

                } else {
                    throw new Exception("Unexpected punctuation on variable declaration %s: \"%s\"".formatted(this.token, token));
                }
            }
            // evaluate operators
            else if (isOperator(token)) {
                // check for equal sign
                if (Functions.equals(KEY_OP_ASSIGN, token) && isDeclared()) {
                    OperationEvaluatorNode operationEvaluatorNode = new OperationEvaluatorNode(new Token("op_"+ this.token, this.token.line), depth + 1);
                    operationEvaluatorNode.evaluate(tokenList, evaluator);
                    members.add(operationEvaluatorNode);
                    return this;

                } else if (!isDeclared()) {
                    throw new Exception("Missing variable name : \"%s\"".formatted(token));

                } else {
                    throw new Exception("Missing '=' sign %s : \"%s\"".formatted(this.token, token));
                }
            }
            // evaluate strings
            else if (!isKeyWord(token)) {
                if (!isDeclared()) {
                    System.out.printf(indent + "Declaring variable name : %s", token.string);
                    variableName = token.string;

                } else {
                    throw new Exception("Unexpected token on variable declaration %s: \"%s\"".formatted(this.token, token));
                }
            }
            // evaluate the rest
            else {
                throw new Exception("Unexpected token on variable declaration %s: \"%s\"".formatted(this.token, token));
            }
        }
        throw new Exception("Unexpected end of file");
    }

    public boolean isDeclared() {
        return !variableName.isEmpty();
    }

    @Override
    public String toString() {
        return "declare %s :=".formatted(variableName);
    }
}