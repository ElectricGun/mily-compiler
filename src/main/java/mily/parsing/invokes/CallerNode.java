package mily.parsing.invokes;

import mily.interfaces.*;
import mily.parsing.*;
import mily.processing.*;
import mily.structures.dataobjects.*;
import mily.tokens.*;

import java.util.*;

import static mily.constants.Keywords.*;

public class CallerNode extends EvaluatorNode implements Caller {

    protected final List<OperationNode> arguments = new ArrayList<>();
    protected String name;
    protected Type type = KEY_DATA_UNKNOWN.create();

    public CallerNode(String name, Token nameToken, int depth) {
        super(nameToken, depth);

        this.name = name;
    }

    @Override
    public String errorName() {
        return "function call " + "\"" + getName() + "\"";
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree) {
        evaluateArgs(tokenList, evaluatorTree, evaluatorTree.debugMode);
        return this;
    }

    @Override
    public String toString() {
        return "call function: " + getName() + getArgs();
    }

    @Override
    public CallableSignature signature() {
        int argCount = this.getArgCount();

        Type[] argTypes = new Type[argCount];
        for (int a = 0; a < argCount; a++) {
            argTypes[a] = Validation.getOperationType(this.getArg(a), false);
        }

        return new CallableSignature(this.getName(), argTypes);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
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
    public Type getType() {
        return type;
    }

    @Override
    public void setType(Type type) {
        this.type = type;
    }

    public List<OperationNode> getArgs() {
        return arguments;
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

            if (bracketCount == 0 && token.equalsKey(KEY_BRACKET_CLOSE) && !token.isWhiteSpace()) {
                if (!opTokens.isEmpty()) {
                    opTokens.add(new Token(KEY_SEMICOLON, token.source, token.line));
                    EvaluatorNode operationNode = new OperationNode(argName, depth + 1).evaluate(opTokens, evaluatorTree);
                    members.add(operationNode);
                    arguments.add((OperationNode) operationNode);
                }
                return;
            } else if (token.equalsKey(KEY_COMMA) && bracketCount == 0 && !token.isWhiteSpace()) {
                if (opTokens.isEmpty()) {
                    this.throwSyntaxError("Empty argument found", token);

                } else {
                    opTokens.add(new Token(KEY_SEMICOLON, token.source, token.line));
                    EvaluatorNode operationNode = new OperationNode(argName, depth + 1).evaluate(opTokens, evaluatorTree);
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
                opTokens.add(token);
            }
        }
    }
}
