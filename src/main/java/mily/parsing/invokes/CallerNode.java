package mily.parsing.invokes;

import mily.abstracts.*;
import mily.parsing.*;
import mily.tokens.*;

import java.util.*;

import static mily.constants.Keywords.*;
import static mily.constants.Keywords.KEY_BRACKET_CLOSE;
import static mily.constants.Keywords.KEY_BRACKET_OPEN;
import static mily.constants.Keywords.KEY_COMMA;
import static mily.constants.Keywords.KEY_SEMICOLON;

// TOOD implement this
public abstract class CallerNode extends EvaluatorNode implements Caller {

    protected List<OperationNode> arguments = new ArrayList<>();
    protected String type = KEY_DATA_UNKNOWN;

    public CallerNode(Token nameToken, int depth) {
        super(nameToken, depth);
    }

    @Override
    public OperationNode getArg(int i) {
        return arguments.get(i);
    }

    @Override
    public void setArg(int i, OperationNode operationNode) {
        arguments.set(i, operationNode);
    }

    @Override
    public int getArgCount() {
        return arguments.size();
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    protected void evaluateArgs(List<Token> tokenList, EvaluatorTree evaluatorTree, boolean debugMode) {
        String indent = " ".repeat(depth);

        int bracketCount = 0;
        int argCount = 0;
        boolean expectingArgument = false;
        List<Token> opTokens = new ArrayList<>();

        while (!tokenList.isEmpty()) {
            Token token = tokenList.remove(0);

            Token argName = new Token("arg_" + getName() + "_" + argCount, token.source, token.line);

            if (debugMode)
                System.out.println(indent + "arg: " + token + " expectingArg: " + expectingArgument);

            if (token.isWhiteSpace()) {
                continue;
            }

            if (bracketCount == 0 && token.equalsKey(KEY_BRACKET_CLOSE)) {
                if (!opTokens.isEmpty()) {
                    opTokens.add(new Token(KEY_SEMICOLON, token.source, token.line));
                    EvaluatorNode operationNode = new OperationNode(argName, depth + 1).evaluate(opTokens, evaluatorTree, debugMode);
                    members.add(operationNode);
                    arguments.add((OperationNode) operationNode);
                }
                return;
            } else if (token.equalsKey(KEY_COMMA) && bracketCount == 0) {
                if (opTokens.isEmpty()) {
                    this.throwSyntaxError("Empty argument found", token);

                } else {
                    opTokens.add(new Token(KEY_SEMICOLON, token.source, token.line));
                    EvaluatorNode operationNode = new OperationNode(argName, depth + 1).evaluate(opTokens, evaluatorTree, debugMode);
                    members.add(operationNode);
                    arguments.add((OperationNode) operationNode);
                    argCount++;
                }
                expectingArgument = true;
            } else {
                if (token.equalsKey(KEY_BRACKET_OPEN)) {
                    bracketCount++;

                } else if (token.equalsKey(KEY_BRACKET_CLOSE) && bracketCount > 0) {
                    bracketCount--;

                }
//                else if () {
//                    this.throwSyntaxError("Unexpected close bracket", token);
//                }
                opTokens.add(token);
            }
        }
    }
}
