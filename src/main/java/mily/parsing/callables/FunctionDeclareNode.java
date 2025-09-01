package mily.parsing.callables;

import mily.parsing.*;
import mily.structures.errors.*;
import mily.structures.structs.*;
import mily.tokens.*;

import java.util.*;

import static mily.constants.Keywords.*;

/**
 * <h1> Class FunctionDeclareNode </h1>
 * Function Declarations
 * Parses function declarations
 *
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
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree) {
        String indent = " ".repeat(depth);

        try {
            processArgs(tokenList, evaluatorTree);

            if (evaluatorTree.debugMode)
                System.out.printf(indent + "Function header \"%s(%s)\" created%n", this.nameToken, String.join(", ", argumentNames));

            Token nextTOken = fetchNextNonWhitespaceToken(tokenList);
            if (nextTOken.equalsKey(KEY_CURLY_OPEN)) {
                scope = new ScopeNode(this.nameToken, depth + 1, true, this);
                members.add(scope.evaluate(tokenList, evaluatorTree));
                return this;

            } else {
                return throwSyntaxError("Unexpected token on function declare", nextTOken);
            }

        } catch (JavaMilySyntaxException e) {
            return throwSyntaxError(e.getMessage(), e.getToken());
        }
    }

    @Override
    public String toString() {
        return String.format("declare function : %s | args: %s | arg_types: %s", nameToken, String.join(", ", argumentNames), argumentTypes);
    }

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

}