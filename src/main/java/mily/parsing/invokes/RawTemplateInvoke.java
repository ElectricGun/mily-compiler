package mily.parsing.invokes;

import mily.abstracts.*;
import mily.parsing.*;
import mily.processing.Validation;
import mily.tokens.*;

import java.util.*;

import static mily.constants.Keywords.*;

/**
 * <h1> Class RawTemplateInvoke </h1>
 * For invoking macros evaluated during code generation time
 *
 * @author ElectricGun
 */

public class RawTemplateInvoke extends EvaluatorNode implements Caller {

    String name;
    protected String type = KEY_DATA_UNKNOWN;

    List<OperationNode> args = new ArrayList<>();

    public RawTemplateInvoke(String name, Token nameToken, int depth) {
        super(nameToken, depth);
        this.name = name;
    }

    public List<OperationNode> getArgs() {
        return new ArrayList<>(args);
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree, boolean debugMode) throws Exception {
        String indent = " ".repeat(depth);

        StringBuilder argBufferString = new StringBuilder();
        boolean done = false;

        while (!tokenList.isEmpty()) {
            Token token = tokenList.remove(0);
// Token token = tokenList.removeFirst();
            if (debugMode)
                System.out.printf(indent + "raw template %s: %s%n", this.nameToken, token);

            if (token.equalsKey(KEY_BRACKET_OPEN)) {
                evaluateArgs(tokenList, evaluatorTree, debugMode);
                done = true;

            } else if (done && token.equalsKey(KEY_SEMICOLON)) {
                return this;

            } else {
                return throwSyntaxError("Unexpected token on raw template invoke \"" + token.string + "\"", token);
            }
//            if (keyEquals(KEY_SEMICOLON, token)) {
//
//                if (!argBufferString.isEmpty()) {
//                    args = List.of(argBufferString.toString().split(KEY_COMMA));
//                }
//                return this;
//
//            } else {
//                argBufferString.append(token.string);
//            }
        }

        return throwSyntaxError("Unexpected end of file", nameToken);
    }

    protected EvaluatorNode evaluateArgs(List<Token> tokenList, EvaluatorTree evaluatorTree, boolean debugMode) {
        String indent = " ".repeat(depth);

        int bracketCount = 0;
        int argCount = 0;
        boolean expectingArgument = false;
        List<Token> opTokens = new ArrayList<>();

        while (!tokenList.isEmpty()) {
            Token token = tokenList.remove(0);

            Token argName = new Token("arg_" + getName() + "_" + argCount, token.source, token.line);

            if (debugMode)
                System.out.println(indent + "arg: " + token);

            if (token.isWhiteSpace()) {
                continue;

            } else if (bracketCount == 0 && !expectingArgument && token.equalsKey(KEY_BRACKET_CLOSE)) {
                if (!opTokens.isEmpty()) {
                    opTokens.add(new Token(KEY_SEMICOLON, token.source, token.line));
                    EvaluatorNode operationNode = new OperationNode(argName, depth + 1).evaluate(opTokens, evaluatorTree, debugMode);
                    members.add(operationNode);
                    args.add((OperationNode) operationNode);
                    argCount ++;
                }

                return this;
            } else if (!expectingArgument && token.equalsKey(KEY_COMMA)){
                expectingArgument = true;

            } else if (token.equalsKey(KEY_COMMA)) {
                if (opTokens.isEmpty()) {
                    this.throwSyntaxError("Empty argument found", token);

                } else {
                    opTokens.add(new Token(KEY_SEMICOLON, token.source, token.line));
                    EvaluatorNode operationNode = new OperationNode(argName, depth + 1).evaluate(opTokens, evaluatorTree, debugMode);
                    members.add(operationNode);
                    args.add((OperationNode) operationNode);
                    argCount++;
                }
            } else {
                if (token.equalsKey(KEY_BRACKET_OPEN)) {
                    bracketCount ++;

                } else if (token.equalsKey(KEY_BRACKET_CLOSE) && bracketCount > 0) {
                    bracketCount --;

                } else if (token.equalsKey(KEY_BRACKET_CLOSE) && bracketCount == 0) {
                    this.throwSyntaxError("Unexpected close bracket", token);
                }

                opTokens.add(token);
            }
        }

        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "invoke template: " + name + args;
    }

    @Override
    public String getFnKey() {
        StringBuilder fnKey = new StringBuilder(this.getName() + "_");

        int argCount = this.args.size();
        for (int a = 0; a < argCount; a++) {
            fnKey.append(Validation.getOperationType(this.args.get(a), false));
            if (a < argCount - 1) {
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

    @Override
    public OperationNode getArg(int i) {
        return args.get(i);
    }

    @Override
    public int getArgCount() {
        return args.size();
    }
}
