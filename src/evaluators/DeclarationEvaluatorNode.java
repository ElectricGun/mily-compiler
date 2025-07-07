package src.evaluators;

import src.tokens.Token;

import java.util.List;

import static src.Vars.*;
import static src.Vars.OP_ASSIGN;

public class DeclarationEvaluatorNode extends EvaluatorNode {

    public DeclarationEvaluatorNode(Token name, int depth) {
        super(name, depth);
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, Evaluator evaluator) throws Exception {
        String indent = " ".repeat(depth);

        System.out.printf("Parsing Variable Declaration %s:%n", token);

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();

            System.out.printf(indent + "declaration :  %s : %s%n", this.token, token);

            // evaluate punctuations
            if (token.length() == 1 && isPunctuation(token.charAt(0))) {
                char c = token.charAt(0);
                if (isWhiteSpace(c)) {
                    continue;
                }

                throw new Exception("Unexpected punctuation on variable declaration %s: \"%s\"".formatted(this.token, c));
            }
            // evaluate operators
            else if (isOperator(token)) {
                // check for equal sign
                if (token.string.equals(OP_ASSIGN)) {
                    OperationEvaluatorNode operationEvaluatorNode = new OperationEvaluatorNode(new Token("op_"+ this.token, this.token.line), depth + 1);
                    operationEvaluatorNode.evaluate(tokenList, evaluator);
                    members.add(operationEvaluatorNode);
                    return this;
                }
                else {
                    throw new Exception("Missing '=' sign %s: \"%s\"".formatted(this.token, token));
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
        return "declare %s".formatted(token);
    }
}