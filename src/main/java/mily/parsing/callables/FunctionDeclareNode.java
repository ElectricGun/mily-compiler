package mily.parsing.callables;

import mily.parsing.*;
import mily.structures.structs.Type;
import mily.tokens.*;

import java.util.*;

import static mily.constants.Functions.*;
import static mily.constants.Keywords.*;

/**
 * <h1> Class FunctionDeclareNode </h1>
 * Function Declarations
 * Parses function declarations
 * @author ElectricGun
 */

public class FunctionDeclareNode extends CallableNode {

    protected ScopeNode scope;

    public FunctionDeclareNode(String name, Type returnType, Token nameToken, int depth) {
        super(name, nameToken, depth);

        this.returnType = returnType;
    }

    @Override
    public String errorName() {
        return "function " + "\"" + nameToken.string + "\"";
    }

    public ScopeNode getScope() {
        return scope;
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree) throws Exception {
        String indent = " ".repeat(depth);

        boolean isInitialized = false;
        boolean functionDeclared = false;
        boolean argumentWanted = false;

        if (evaluatorTree.debugMode)
            System.out.printf(indent + "Parsing Function %s:%n", this.nameToken);

        while (!tokenList.isEmpty()) {
            Token token = tokenList.remove(0);
            if (evaluatorTree.debugMode)
                System.out.printf(indent + "function\t:\t%s\t:\t%s%n", this.nameToken, token);

            if (isWhiteSpace(token)) {
                continue;

            } else if (isPunctuation(token) && !isWhiteSpace(token)) {
                if (argumentWanted) {
                    return throwSyntaxError("Expecting an argument on function declaration", token);

                } else if (keyEquals(KEY_BRACKET_CLOSE, token)) {
                    functionDeclared = true;

                } else if (keyEquals(KEY_COMMA, token)) {
                    argumentWanted = true;

                } else if (functionDeclared && keyEquals(KEY_CURLY_OPEN, token)) {
                    if (evaluatorTree.debugMode)
                        System.out.printf(indent + "Function header \"%s(%s)\" created%n", this.nameToken, String.join(", ", argumentNames));

                    scope = new ScopeNode(this.nameToken, depth + 1, true, this);
                    members.add(scope.evaluate(tokenList, evaluatorTree));
                    return this;

                } else {
                    return throwSyntaxError("Unexpected punctuation on function declaration", token);

                }
            } else if (isOperator(token)) {
                return throwSyntaxError("Unexpected operator on function declaration", token);

            } else if (isVariableOrDeclarator(token)) {
                //TODO use DataTypeNode
                Type type = new Type(token.string);
                argumentTypes.add(type);
                Token variableName = tokenList.remove(0);

                while (isWhiteSpace(variableName)) {
                    variableName = tokenList.remove(0);
                }
                if (!isVariableName(variableName)) {
                    return throwSyntaxError("Not a variable name on function declaration", token);

                } else if (!isInitialized || argumentWanted) {
                    argumentNames.add(variableName.string);
                    argumentWanted = false;

                    FunctionArgNode functionArgNode = new FunctionArgNode(type, variableName, depth + 1);
                    functionArgNode.setName(variableName.string);
                    members.add(functionArgNode);

                    if (evaluatorTree.debugMode)
                        System.out.printf("Added argument %s%n", variableName);

                } else {
                    return throwSyntaxError("Unexpected token on function declaration", token);

                }
            }
            isInitialized = true;
        }
        return throwSyntaxError("Unexpected end of file", nameToken);
    }

    @Override
    public String toString() {
        return String.format("declare function : %s | args: %s | arg_types: %s", nameToken, String.join(", ", argumentNames), argumentTypes);
    }

//    @Override
//    public boolean isOverload(Caller caller, String name, String... types) {
//        if (!(caller instanceof FunctionCallNode)) {
//            return false;
//        }

//        return isOverload(name, types);
//    }

//    @Override
//    public boolean isOverload(Callable callable, String name, String... types) {
//        if (!(callable instanceof FunctionDeclareNode)) {
//            return false;
//        }
//
//        return isOverload(name, types);
//    }

    @Override
    public boolean isOverload(String name, Type... types) {
        if (!this.getName().equals(name)) {
            return false;
        }
        if (types.length != getArgumentNames().size()) {
            return false;
        }
        for (int i = 0; i < types.length; i++) {
            if (!types[i].equals(argumentTypes.get(i))) {
                return false;
            }
        }
        return true;
    }

//    @Override
//    public String getFnKey() {
//        StringBuilder fnKey = new StringBuilder(this.getName() + "_");
//
//        int argCount = this.getArgCount();
//        for (int a = 0; a < argCount; a++) {
//            fnKey.append(this.getArgType(a));
//            if (a < argCount - 1) {
//                fnKey.append("_");
//            }
//        }
//        return fnKey.toString();
//    }

}