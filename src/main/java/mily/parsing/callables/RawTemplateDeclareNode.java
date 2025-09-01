package mily.parsing.callables;

import mily.parsing.*;
import mily.structures.structs.Type;
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

        StringBuilder argBufferString = new StringBuilder();
        boolean isParsingArg = false;
        boolean doneParsingArgs = false;

        boolean expectingReturnPattern = !returnType.equals(KEY_DATA_VOID.create());

        while (!tokenList.isEmpty()) {
            Token token = tokenList.remove(0);
            if (evaluatorTree.debugMode)
                System.out.printf(indent + "raw template %s: %s%n", name, token);


            if (isWhiteSpace(token) && !isParsingArg) {
                continue;
            }

            if (!doneParsingArgs) {
                if (name == null && !isParsingArg && isVariableName(token)) {
                    name = token.string;

                } else if (returnType != null && name != null && keyEquals(KEY_BRACKET_OPEN, token) && !isParsingArg) {
                    if (evaluatorTree.debugMode)
                        System.out.printf(indent + "parsing raw template arguments", this.nameToken, token);
                    isParsingArg = true;

                } else if (isParsingArg && token.equalsKey(KEY_BRACKET_CLOSE)) {
                    argBufferString = new StringBuilder(argBufferString.toString().trim());

                    if (!argBufferString.isEmpty()) {
                        List<String> argStringsRaw = List.of(argBufferString.toString().split(KEY_COMMA));

                        for (String arg : argStringsRaw) {
                            String[] stringArgType = arg.trim().split(" ");
                            //TODO use DataTypeNode

                            if (isWhiteSpace(arg)) {
                                return throwSyntaxError("Empty token in template input arguments", nameToken);

                            } else if (stringArgType.length != 2) {
                                return throwSyntaxError("Invalid argument in template declaration \"" + arg + "\"", nameToken);
                            }

                            argumentTypes.add(new Type(stringArgType[0]));
                            argumentNames.add(stringArgType[1]);
                        }
                    }
                    isParsingArg = false;
                    doneParsingArgs = true;

                } else if (isParsingArg && (isWhiteSpace(token) || isVariableOrDeclarator(token) || isVariableName(token) || keyEquals(KEY_COMMA, token))) {
                    argBufferString.append(token.string);

                } else if (isParsingArg) {
                    return throwSyntaxError("Unexpected token in template input arguments", token);
                }
            } else if (!expectingReturnPattern && token.equalsKey(KEY_MACRO_LITERAL)) {
                MacroScope macroScope = new MacroScope(token, argumentNames, depth + 1);
                members.add(macroScope.evaluate(tokenList, evaluatorTree));
                scope = macroScope;
                return this;

            } else if (expectingReturnPattern && token.equalsKey(KEY_TEMPLATE_RETURNS)) {
                Token outputPatternToken = tokenList.remove(0);

                while (outputPatternToken.isWhiteSpace()) {
                    outputPatternToken = tokenList.remove(0);
                }

                if (!isVariableName(outputPatternToken)) {
                    return throwSyntaxError("Invalid return pattern", outputPatternToken);
                }
                returnVariableRaw = outputPatternToken.string;
                expectingReturnPattern = false;

            } else if (expectingReturnPattern) {
                return throwSyntaxError("Non-void template requires a raw return variable name", token);

            } else {
                return throwSyntaxError("Unexpected token after raw template input declaration", token);
            }
        }
        return throwSyntaxError("Unexpected end of file", nameToken);
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
