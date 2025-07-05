package src.evaluation;

import java.util.List;

import static src.Vars.*;
import static src.Vars.OP_ASSIGN;

public class DeclarationEvaluator extends EvaluatorNode {

    public DeclarationEvaluator(Token name, int depth) {
        super(name, depth);
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, Evaluator evaluator) throws Exception {
        String indent = " ".repeat(depth);

        System.out.printf("Parsing Variable Declaration %s:%n", name);

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();

            System.out.printf(indent + "declaration :  %s : %s%n",name, token);

            // evaluate punctuations
            if (token.length() == 1 && isPunctuation(token.charAt(0))) {
                char c = token.charAt(0);
                if (isWhiteSpace(c)) {
                    continue;
                }

                throw new Exception("Unexpected punctuation on variable declaration %s: \"%s\"".formatted(name, c));
            }
            // evaluate operators
            else if (isOperator(token)) {
                // check for equal sign
                if (token.string.equals(OP_ASSIGN)) {
                    OperationEvaluator operationEvaluator = new OperationEvaluator(new Token("op_"+name, name.line), depth + 1);
                    operationEvaluator.evaluate(tokenList, evaluator);
                    members.add(operationEvaluator);
                    return this;
                }
                else {
                    throw new Exception("Missing '=' sign %s: \"%s\"".formatted(name, token));
                }
            }
            // evaluate the rest
            else {

            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "declare %s".formatted(name);
    }
}