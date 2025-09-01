package mily.parsing;

import mily.tokens.*;

import java.util.*;

import static mily.constants.Keywords.*;

/**
 * <h1> Class MacroScope </h1>
 * The scope of a macro
 *
 * @author ElectricGun
 */

public class MacroScope extends EvaluatorNode {

    protected List<String> content = new ArrayList<>();
    protected List<String> args;

    public MacroScope(Token nameToken, List<String> args, int depth) {
        super(nameToken, depth);

        this.args = args;
    }

    @Override
    public String errorName() {
        return "macro";
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree) {

        boolean isEvaluatingBlock = false;
        int bracketAmount = 0;
        while (!tokenList.isEmpty()) {
            Token token = tokenList.remove(0);
            String str = token.string;

            if (evaluatorTree.debugMode)
                System.out.printf(indent() + "macro scope %s: %s%n", this.nameToken, token);

            if (!isEvaluatingBlock && token.equalsKey(KEY_CURLY_OPEN)) {
                isEvaluatingBlock = true;
                continue;

            } else if (isEvaluatingBlock) {
                if (bracketAmount == 0 && token.equalsKey(KEY_CURLY_CLOSE)) {
                    return this;

                } else if (token.equalsKey(KEY_CURLY_OPEN)) {
                    bracketAmount++;

                } else if (token.equalsKey(KEY_CURLY_CLOSE)) {
                    bracketAmount--;
                }
            } else if (!token.isWhiteSpace()) {
                return throwSyntaxError("Unexpected token in macro", token);
            }

            if (isEvaluatingBlock) {
                content.add(str);
            }
        }
        return throwSyntaxError("Unexpected end of file", nameToken);
    }

    public String asFormatted(List<String> replacers) {
        StringBuilder out = new StringBuilder();
        for (String token : content) {
            int tokenIndex = args.indexOf(token);
            if (tokenIndex != -1 && !replacers.isEmpty()) {
                out.append(replacers.get(tokenIndex));
            } else {
                out.append(token);
            }
        }
        return out.toString();
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder("${");
        for (String s : content) {
            if (!s.equals(KEY_NEWLINE)) {
                out.append(s);

            } else {
                out.append(KEY_NEWLINE).append(indent(depth + 2));
            }
        }
        out.append("}");
        return out.toString();
    }
}
