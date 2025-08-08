package src.parsing;

import src.interfaces.*;
import src.tokens.Token;

import java.util.*;

import static src.constants.Keywords.*;
import static src.constants.Functions.*;

/**
 * <h1> Class RawTemplateInvoke </h1>
 * For invoking macros evaluated during code generation time
 *
 * @author ElectricGun
 */

public class RawTemplateInvoke extends EvaluatorNode implements Named {

    String name;

    List<String> args = new ArrayList<>();

    public RawTemplateInvoke(String name, Token nameToken, int depth) {
        super(nameToken, depth);
        this.name = name;
    }

    public List<String> getArgs() {
        return args;
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree, boolean debugMode) throws Exception {
        String indent = " ".repeat(depth);

        StringBuilder argBufferString = new StringBuilder();

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();

            if (debugMode)
                System.out.printf(indent + "raw template %s: %s%n", this.nameToken, token);

            if (keyEquals(KEY_SEMICOLON, token)) {

                if (!argBufferString.isEmpty()) {
                    args = List.of(argBufferString.toString().split(KEY_COMMA));
                }
                return this;

            } else {
                argBufferString.append(token.string);
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
        return "invoke template: " + name + args;
    }
}
