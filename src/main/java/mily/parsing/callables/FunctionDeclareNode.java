package mily.parsing.callables;

import mily.abstracts.*;
import mily.parsing.*;
import mily.parsing.invokes.FunctionCallNode;
import mily.tokens.*;

import java.util.*;

import static mily.constants.Functions.*;
import static mily.constants.Keywords.*;

/**
 * <h1> Class FunctionDeclareNode </h1>
 * Function Declarations
 * Parses function declarations; not to be confused with the {@link FunctionCallNode}
 *
 * @author ElectricGun
 */

public class FunctionDeclareNode extends EvaluatorNode implements Callable {

    List<String> argumentNames = new ArrayList<>();
    List<String> argumentTypes = new ArrayList<>();
    ScopeNode scope;

    String returnType;

    public FunctionDeclareNode(String returnType, Token name, int depth) {
        super(name, depth);

        this.returnType = returnType;
    }

    @Override
    public List<String> getArgumentNames() {
        return new ArrayList<>(argumentNames);
    }

    @Override
    public List<String> getArgumentTypes() {
        return new ArrayList<>(argumentTypes);
    }

    @SuppressWarnings("unused")
    public String[] getArgumentNamesArr() {
        String[] out = new String[argumentNames.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = argumentNames.get(i);
        }
        return out;
    }

    public String[] getArgumentTypesArr() {
        String[] out = new String[argumentTypes.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = argumentTypes.get(i);
        }
        return out;
    }

    public ScopeNode getScope() {
        return scope;
    }

    // todo probably give this a name var
    public String getName() {
        return nameToken.string;
    }

    public int getArgCount() {
        return argumentNames.size();
    }

    public String getArg(int i) {
        return argumentNames.get(i);
    }

    public String getArgType(int i) {
        return argumentTypes.get(i);
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree, boolean debugMode) throws Exception {
        String indent = " ".repeat(depth);

        boolean isInitialized = false;
        boolean functionDeclared = false;
        boolean argumentWanted = false;

        if (debugMode)
            System.out.printf(indent + "Parsing Function %s:%n", this.nameToken);

        while (!tokenList.isEmpty()) {
            Token token = tokenList.remove(0);
// Token token = tokenList.removeFirst();
            if (debugMode)
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
                    if (debugMode)
                        System.out.printf(indent + "Function header \"%s(%s)\" created%n", this.nameToken, String.join(", ", argumentNames));

                    scope = new ScopeNode(this.nameToken, depth + 1, true, this);
                    members.add(scope.evaluate(tokenList, evaluatorTree, debugMode));
                    return this;

                } else {
                    return throwSyntaxError("Unexpected punctuation on function declaration", token);

                }
            } else if (isOperator(token)) {
                return throwSyntaxError("Unexpected operator on function declaration", token);

            } else if (isVariableOrDeclarator(token)) {
                argumentTypes.add(token.string);
//                Token variableName = tokenList.removeFirst();
                Token variableName = tokenList.remove(0);

                while (isWhiteSpace(variableName)) {
//                    variableName = tokenList.removeFirst();
                    variableName = tokenList.remove(0);
                }
                if (!isVariableName(variableName)) {
                    return throwSyntaxError("Not a variable name on function declaration", token);

                } else if (!isInitialized || argumentWanted) {
                    argumentNames.add(variableName.string);
                    argumentWanted = false;

                    FunctionArgNode functionArgNode = new FunctionArgNode(token.string, variableName, depth + 1);
                    functionArgNode.setVariableName(variableName.string);
                    members.add(functionArgNode);

                    if (debugMode)
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
        return String.format("declare function : %s | args: %s | arg_types: %s", nameToken, String.join(", ", argumentNames), String.join(", ", argumentTypes));
    }

    @Override
    public boolean isOverload(Caller caller, String name, String... types) {
        if (!(caller instanceof FunctionCallNode)) {
            return false;
        }

        return isOverload(name, types);
    }

    @Override
    public boolean isOverload(Callable callable, String name, String... types) {
        if (!(callable instanceof FunctionDeclareNode)) {
            return false;
        }

        return isOverload(name, types);
    }

    @Override
    public boolean isOverload(String name, String... types) {
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

    @Override
    public String getFnKey() {
        StringBuilder fnKey = new StringBuilder(this.getName() + "_");

        int argCount = this.getArgCount();
        for (int a = 0; a < argCount; a++) {
            fnKey.append(this.getArgType(a));
            if (a < argCount - 1) {
                fnKey.append("_");
            }
        }
        return fnKey.toString();
    }

    @Override
    public String getType() {
        return returnType;
    }

    @Override
    public void setType(String type) {
        returnType = type;
    }
}