package src.parsing;

import src.interfaces.*;
import src.tokens.*;

import java.util.*;

import static src.constants.Functions.*;
import static src.constants.Keywords.*;

/**
 * <h1> Class RawTemplateNode </h1>
 * For macros that are evaluated during code generation
 *
 * @author ElectricGun
 */

public class RawTemplateNode extends EvaluatorNode implements Named {

    protected String name;
    protected MacroScope scope;

    protected List<String> argStrings = new ArrayList<>();

    public RawTemplateNode(Token nameToken, int depth) {
        super(nameToken, depth);
    }

    public MacroScope getScope() {
        return scope;
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree, boolean debugMode) throws Exception {
        String indent = " ".repeat(depth);

        String argBufferString = "";
        boolean isParsingArg = false;

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();

            if (debugMode)
                System.out.printf(indent + "raw template %s: %s%n", name, token);

            if (isWhiteSpace(token)) {
                continue;

            } else if (name == null && !isParsingArg && isVariableName(token)) {
                name = token.string;

            } else if (name != null && keyEquals(KEY_COLON, token) && !isParsingArg) {
                if (debugMode)
                    System.out.printf(indent + "parsing raw template arguments", this.nameToken, token);
                isParsingArg = true;

            } else if (!isParsingArg && keyEquals(KEY_DOLLAR, token)) {
                MacroScope macroScope = new MacroScope(token, argStrings, depth + 1);
                members.add(macroScope.evaluate(tokenList, evaluatorTree, debugMode));
                scope = macroScope;
                return this;

            } else if (isParsingArg) {
                if (keyEquals(KEY_DOLLAR, token)) {

                    argBufferString = argBufferString.trim();
                    argStrings = List.of(argBufferString.split(KEY_COMMA));

                    for (String arg : argStrings) {
                        if (isWhiteSpace(arg)) {
                            return throwSyntaxError("Empty token in template input arguments", nameToken);
                        }
                    }

                    MacroScope macroScope = new MacroScope(token, argStrings, depth + 1);
                    members.add(macroScope.evaluate(tokenList, evaluatorTree, debugMode));
                    scope = macroScope;
                    return this;

                } else if (isVariableName(token) || keyEquals(KEY_COMMA, token)) {
                    argBufferString += token.string;

                } else {
                    return throwSyntaxError("Unexpected token in template input arguments", token);
                }
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
        return "template: " + getName() + argStrings;
    }
}
