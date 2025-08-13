package mily.parsing.invokes;

import mily.abstracts.*;
import mily.parsing.*;
import mily.processing.*;
import mily.tokens.*;

import java.util.*;

/**
 * <h1> Class FunctionCallNode </h1>
 * Function Calls
 * Purpose: Parses function calls, such as f(), f(x), and f(x, y) <br>
 * Conditionals / Routes:
 * <ul>
 *     <li> Token ")" on first iteration             -> return this </li>
 *     <li> Token ")" when not expecting a parameter -> return this </li>
 * </ul>
 *
 * @author ElectricGun
 */

public class FunctionCallNode extends CallerNode implements Named {

    public FunctionCallNode(Token token, int depth) {
        super(token, depth);
    }

    // todo probably give this a name var
    public String getName() {
        return nameToken.string;
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree) throws Exception {
        evaluateArgs(tokenList, evaluatorTree, evaluatorTree.debugMode);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder arguments = new StringBuilder();
        int i = 0;

        for (OperationNode operationNode : this.arguments) {
            arguments.append(i > 0 ? ", " : "").append(operationNode);
            i++;
        }
        return "call " + nameToken.string + " | args: (" + arguments + ")";
    }

    @Override
    public String getFnKey() {
        StringBuilder fnKey = new StringBuilder(this.getName() + "_");

        int argCount = this.getArgCount();
        for (int a = 0; a < argCount; a++) {
            fnKey.append(Validation.getOperationType(this.getArg(a), false));
            if (a < argCount - 1) {
                fnKey.append("_");
            }
        }
        return fnKey.toString();
    }
}
