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

public class RawTemplateInvoke extends CallerNode implements Named {

    public RawTemplateInvoke(String name, Token nameToken, int depth) {
        super(name, nameToken, depth);
    }

    public List<OperationNode> getArgs() {
        return arguments;
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree) throws Exception {
        String indent = " ".repeat(depth);

        while (!tokenList.isEmpty()) {
            Token token = tokenList.remove(0);

            if (evaluatorTree.debugMode)
                System.out.printf(indent + "raw template invoke %s: %s%n", this.nameToken, token);

            if (!token.isWhiteSpace()) {

                if (token.equalsKey(KEY_BRACKET_OPEN)) {
                    evaluateArgs(tokenList, evaluatorTree, evaluatorTree.debugMode);
                    return this;

                } else {
                    return throwSyntaxError("Unexpected token on raw invoke", token);
                }
            }
        }
        System.out.println(arguments);
        return this;
    }


    @Override
    public String toString() {
        return "invoke template: " + name + arguments;
    }

    @Override
    public String getFnKey() {
        StringBuilder fnKey = new StringBuilder(this.getName() + "_");

        int argCount = getArgCount();
        for (int a = 0; a < argCount; a++) {
            fnKey.append(Validation.getOperationType(getArg(a), false));
            if (a < argCount - 1) {
                fnKey.append("_");
            }
        }
        return fnKey.toString();
    }
}
