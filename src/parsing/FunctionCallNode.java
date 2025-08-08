package src.parsing;

import src.interfaces.*;
import src.processing.Validation;
import src.tokens.*;
import java.util.*;

import static src.constants.Functions.*;
import static src.constants.Keywords.*;

/**
 * <h1> Class FunctionCallNode </h1>
 * Function Calls
 * Purpose: Parses function calls, such as f(), f(x), and f(x, y) <br>
 * Conditionals / Routes:
 * <ul>
 *     <li> Token ")" on first iteration             -> return this </li>
 *     <li> Token ")" when not expecting a parameter -> return this </li>
 * </ul>
 * @author ElectricGun
 */

public class FunctionCallNode extends EvaluatorNode implements Named, HasFunctionKey, Typed {

    protected List<OperationNode> arguments = new ArrayList<>();
    protected String type;

    private boolean expectingArgument = true;
    private boolean isInitialized = false;

    public FunctionCallNode(Token token, int depth) {
        super(token, depth);
    }

    public OperationNode getArg(int i) {
        return arguments.get(i);
    }

    public int getArgCount() {
        return arguments.size();
    }

    // todo probably give this a name var
    public String getName() {
        return nameToken.string;
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree, boolean debugMode) throws Exception {
        String indent = " ".repeat(depth);

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();

            if (debugMode)
                System.out.printf(indent + "function call %s: %s:%n", this.nameToken, token);

            if (isWhiteSpace(token)) {
                continue;

            } else if (keyEquals(KEY_BRACKET_CLOSE, token) && (!expectingArgument || !isInitialized)) {
                return this;

            } else if (keyEquals(KEY_COMMA, token) && isInitialized) {
                expectingArgument = true;

            } else if (expectingArgument) {
                List<Token> operationTokens = new ArrayList<>();
                operationTokens.add(token);

                int bracketCount = 0;

                while (true) {
                    Token currToken = tokenList.removeFirst();

                    if (keyEquals(KEY_BRACKET_OPEN, currToken)) {
                        bracketCount ++;

                    } else if (keyEquals(KEY_BRACKET_CLOSE, currToken)) {
                        if (bracketCount > 0) {
                            bracketCount --;
                        } else {
                            // return the final token
                            tokenList.addFirst(currToken);
                            break;
                        }
                     } else if (keyEquals(KEY_COMMA, currToken)) {
                        // return the final token
                        tokenList.addFirst(currToken);
                        break;
                    }
                    operationTokens.add(currToken);
                }
                expectingArgument = false;
                operationTokens.add(new Token(KEY_SEMICOLON, operationTokens.getLast().line));

                OperationNode newOp = new OperationNode(token, depth + 1);
                newOp.evaluate(operationTokens, evaluatorTree, debugMode);
                members.add(newOp);
                arguments.add(newOp);

            } else if (isInitialized) {
                return throwSyntaxError("Unexpected token in function call", token);
            }
            isInitialized = true;
        }
        return throwSyntaxError("Unexpected end of file", nameToken);
    }

    @Override
    public String toString() {
        StringBuilder arguments = new StringBuilder();
        int i = 0;

        for (OperationNode operationNode : this.arguments) {
            arguments.append(i > 0 ? ", " : "").append(operationNode);
            i++;
        }
        return "call " + nameToken.string + " | args: (" + arguments + ")";
    }

    @Override
    public String getFnKey() {
        StringBuilder fnKey = new StringBuilder(this.getName() + "_");

        int argCount = this.getArgCount();
        for (int a = 0; a < argCount; a++) {
            fnKey.append(Validation.getOperationType(this.getArg(a), false));
            if  (a < argCount - 1) {
                fnKey.append("_");
            }
        }
        return fnKey.toString();
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }
}
