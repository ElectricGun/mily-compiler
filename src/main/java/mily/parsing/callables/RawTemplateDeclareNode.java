package mily.parsing.callables;

import mily.abstracts.*;
import mily.parsing.*;
import mily.parsing.invokes.*;
import mily.tokens.*;

import java.util.*;

import static mily.constants.Functions.*;
import static mily.constants.Keywords.*;

/**
 * <h1> Class RawTemplateNode </h1>
 * For macros that are evaluated during code generation
 *
 * @author ElectricGun
 */

public class RawTemplateDeclareNode extends EvaluatorNode implements Callable {

    protected String returnType;
    protected String name;
    protected MacroScope scope;

    protected List<String> argStrings = new ArrayList<>();
    protected List<String> argTypes = new ArrayList<>();

    public RawTemplateDeclareNode(String returnType, Token nameToken, int depth) {
        super(nameToken, depth);

        this.returnType = returnType;
    }

    public MacroScope getScope() {
        return scope;
    }

    @Override
    public List<String> getArgumentNames() {
        return new ArrayList<>(argStrings);
    }

    @Override
    public List<String> getArgumentTypes() {
        return new ArrayList<>(argTypes);
    }

    @Override
    public boolean isOverload(Caller caller, String name, String... types) {
        if (!(caller instanceof RawTemplateInvoke)) {
            return false;
        }

        return isOverload(name, types);
    }

    @Override
    public boolean isOverload(Callable callable, String name, String... types) {
        if (!(callable instanceof RawTemplateDeclareNode)) {
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
            if (!types[i].equals(argTypes.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree, boolean debugMode) throws Exception {
        String indent = " ".repeat(depth);

        StringBuilder argBufferString = new StringBuilder();
        boolean isParsingArg = false;

        while (!tokenList.isEmpty()) {
            Token token = tokenList.remove(0);
            if (debugMode)
                System.out.printf(indent + "raw template %s: %s%n", name, token);

            if (isWhiteSpace(token) && !isParsingArg) {
                continue;
            }

            if (name == null && !isParsingArg && isVariableName(token)) {
                name = token.string;

            } else if (returnType != null && name != null && keyEquals(KEY_BRACKET_OPEN, token) && !isParsingArg) {
                if (debugMode)
                    System.out.printf(indent + "parsing raw template arguments", this.nameToken, token);
                isParsingArg = true;

            } else if (isParsingArg && token.equalsKey(KEY_BRACKET_CLOSE)) {
                isParsingArg = false;

            } else if (isParsingArg && (isWhiteSpace(token) || isVariableOrDeclarator(token) || isVariableName(token) || keyEquals(KEY_COMMA, token))) {
                argBufferString.append(token.string);

            } else if (!isParsingArg && keyEquals(KEY_MACRO_LITERAL, token) && argBufferString.isEmpty()) {
                MacroScope macroScope = new MacroScope(token, argStrings, depth + 1);
                members.add(macroScope.evaluate(tokenList, evaluatorTree, debugMode));
                scope = macroScope;
                return this;

            } else if (!isParsingArg && keyEquals(KEY_MACRO_LITERAL, token)) {
                argBufferString = new StringBuilder(argBufferString.toString().trim());
                List<String> argStringsRaw = List.of(argBufferString.toString().split(KEY_COMMA));

                for (String arg : argStringsRaw) {
                    String[] stringArgType = arg.trim().split(" ");

                    if (isWhiteSpace(arg)) {
                        return throwSyntaxError("Empty token in template input arguments", nameToken);

                    } else if (stringArgType.length != 2) {
                        return throwSyntaxError("Invalid argument in template declaration \"" + arg + "\"", nameToken);
                    }

                    argTypes.add(stringArgType[0]);
                    argStrings.add(stringArgType[1]);
                }

                MacroScope macroScope = new MacroScope(token, argStrings, depth + 1);
                members.add(macroScope.evaluate(tokenList, evaluatorTree, debugMode));
                scope = macroScope;
                return this;

            } else if (isParsingArg) {
                return throwSyntaxError("Unexpected token in template input arguments", token);
            }
        }
        return throwSyntaxError("Unexpected end of file", nameToken);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "template: " + getName() + argStrings + " arg_types: " + argTypes;
    }

    @Override
    public String getFnKey() {
        StringBuilder fnKey = new StringBuilder(this.getName() + "_");

        int argCount = getArgumentNames().size();
        for (int a = 0; a < argCount; a++) {
            fnKey.append(getArgumentTypes().get(a));
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
        this.returnType = type;
    }
}
