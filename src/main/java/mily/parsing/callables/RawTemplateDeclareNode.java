package mily.parsing.callables;

import mily.parsing.*;
import mily.structures.errors.JavaMilySyntaxException;
import mily.structures.structs.Type;
import mily.tokens.*;

import java.util.*;

import static mily.constants.Keywords.*;

/**
 * <h1> Class RawTemplateNode </h1>
 * For macros that are evaluated during code generation
 *
 * @author ElectricGun
 */

public class RawTemplateDeclareNode extends CallableNode {

    protected MacroScope scope;
    protected String returnVariableRaw;

    public RawTemplateDeclareNode(String name, Type returnType, Token nameToken, int depth) {
        super(name, nameToken, depth);

        this.returnType = returnType;
    }

    @Override
    public String errorName() {
        return "template " + "\"" + nameToken.string + "\"";
    }

    @SuppressWarnings("unused")
    public MacroScope getScope() {
        return scope;
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
            if (!types[i].equals(argumentTypes.get(i)) && !argumentTypes.get(i).equals(KEY_DATA_ANY)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree) {
        String indent = " ".repeat(depth);

        boolean expectingReturnPattern = !returnType.equals(KEY_DATA_VOID);

        try {
            Token openingToken = fetchNextNonWhitespaceToken(tokenList);

            if (!openingToken.equalsKey(KEY_BRACKET_OPEN)) {
                return throwSyntaxError("Expected opening parenthesis", openingToken);
            }

            processArgs(tokenList, evaluatorTree);

            if (expectingReturnPattern) {
                Token nextToken = fetchNextNonWhitespaceToken(tokenList);

                if (nextToken.equalsKey(KEY_TEMPLATE_RETURN_ARROW)) {
                    Token returnPattern = fetchNextNonWhitespaceToken(tokenList);
                    returnVariableRaw = returnPattern.string;

                } else {
                    return throwSyntaxError("Non-void template requires a raw return variable name", nextToken);
                }
            }

            Token nextToken = fetchNextNonWhitespaceToken(tokenList);

            if (nextToken.equalsKey(KEY_MACRO_LITERAL)) {
                MacroScope macroScope = new MacroScope(nextToken, argumentNames, depth + 1);
                members.add(macroScope.evaluate(tokenList, evaluatorTree));
                scope = macroScope;
                return this;

            } else {
                return throwSyntaxError("Expected a macro after template declaration", nextToken);
            }

        } catch (JavaMilySyntaxException e) {
            return throwSyntaxError(e.getMessage(), e.getToken());
        }
    }

    @Override
    public String toString() {
        return "declare template: " + getName() + getArgumentNames() + " arg_types: " + getArgumentTypes() +
                ((returnVariableRaw != null && returnVariableRaw.isEmpty()) ? getType() : " -> " + returnVariableRaw + " : " + getType());
    }

    public String scopeAsFormatted(List<String> replacers, String outputVariable) {
        String out = scope.asFormatted(replacers);
        return out.replaceAll(returnVariableRaw, outputVariable);
    }

    public String scopeAsFormatted(List<String> replacers) {
        return scope.asFormatted(replacers);
    }
}
