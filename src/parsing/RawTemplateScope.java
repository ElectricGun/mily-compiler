package src.parsing;

import src.tokens.*;
import java.util.*;

import static src.constants.Keywords.*;
import static src.constants.Functions.*;

public class RawTemplateScope extends EvaluatorNode{

    List<String> tokens = new ArrayList<>();
    Map<String, List<String>> replacedTokens = new HashMap<>();
    List<String> args;

    public RawTemplateScope(Token nameToken, List<String> args, int depth) {
        super(nameToken, depth);

        this.args = args;
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree, boolean debugMode) throws Exception {

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();
            String str = token.string;

            if (debugMode)
                System.out.printf(indent() + "macro scope %s: %s%n", this.nameToken, token);

            if (keyEquals(KEY_DOLLAR, token)) {
                return this;

            } else if (args.contains(str)) {
                if (replacedTokens.containsKey(str)) {
                    replacedTokens.get(str).add(str);

                } else {
                    List<String> arr = new ArrayList<>();
                    replacedTokens.put(str, arr);
                }
            }
            tokens.add(str);
        }

        return throwSyntaxError("Unexpected end of file", nameToken);
    }

    public String asFormatted(List<String> replacers) {
        StringBuilder out = new StringBuilder();
        for (String token : tokens) {
            int tokenIndex = args.indexOf(token);
            if (tokenIndex != -1) {
                out.append(replacers.get(tokenIndex));
            } else {
                out.append(token);
            }
        }
        return out.toString();
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder("$");
        for (String s : tokens) {
            if (!s.equals("\n"))
                out.append(s);
            else
                out.append("\n").append(indent(depth + 2));
        }
        out.append("$");
        return out.toString();
    }
}
