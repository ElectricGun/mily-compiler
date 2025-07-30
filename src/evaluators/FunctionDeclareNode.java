package src.evaluators;

import src.tokens.*;
import java.util.*;

import static src.constants.Functions.*;
import static src.constants.Keywords.*;

/**
 * <h1> Class FunctionDeclareNode </h1>
 * Function Declarations
 * Parses function declarations; not to be confused with the {@link FunctionCallNode}
 * @author ElectricGun
 */

public class FunctionDeclareNode extends EvaluatorNode {

    public FunctionDeclareNode(Token name, int depth) {
        super(name, depth);
    }

    List<String> argumentNames = new ArrayList<>();
    ScopeNode scope;

    private boolean isInitialized = false;
    private boolean functionDeclared = false;
    private boolean argumentWanted = false;

    public ScopeNode getScope() {
        return scope;
    }

    // todo probably give this a name var
    public String getName() {
        return nameToken.string;
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree, boolean debugMode) throws Exception {
        String indent = " ".repeat(depth);

        if (debugMode)
            System.out.printf(indent + "Parsing Function %s:%n", this.nameToken);

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();

            if (debugMode)
                System.out.printf(indent + "function\t:\t%s\t:\t%s%n", this.nameToken, token);

            if (isWhiteSpace(token)) {
                continue;

            } else if (isPunctuation(token) && !isWhiteSpace(token)) {
                 if (argumentWanted) {
                     return throwSyntaxError("Expecting an argument on function declaration", token);

                } else if (keyEquals(KEY_BRACKET_CLOSE, token)) {
                    functionDeclared = true;

                } else if (keyEquals(KEY_COMMA, token)) {
                    argumentWanted = true;

                } else if (functionDeclared && keyEquals(KEY_CURLY_OPEN, token)) {
                     if (debugMode)
                         System.out.printf(indent + "Function header \"%s(%s)\" created%n", this.nameToken, String.join(", ", argumentNames));

                     scope = new ScopeNode(this.nameToken, depth + 1, true, this);
                     members.add(scope.evaluate(tokenList, evaluatorTree, debugMode));
                     return this;

                } else {
                     return throwSyntaxError("Unexpected punctuation on function declaration", token);

                }
            } else if (isOperator(token)) {
                return throwSyntaxError("Unexpected operator on function declaration", token);

            } else if (isDeclaratorAmbiguous(token)) {
                Token variableName = tokenList.removeFirst();

                while (isWhiteSpace(variableName)) {
                    variableName = tokenList.removeFirst();
                }
                if (!isVariableName(variableName)) {
                    return throwSyntaxError("Not a variable name on function declaration", token);

                } else if (!isInitialized || argumentWanted) {
                    argumentNames.add(variableName.string);
                    argumentWanted = false;

                    FunctionArgNode functionArgNode = new FunctionArgNode(token.string, variableName, depth + 1);
                    functionArgNode.variableName = variableName.string;
                    members.add(functionArgNode);

                    if (debugMode)
                        System.out.printf("Added argument %s%n", variableName);

                } else {
                    return throwSyntaxError("Unexpected token on function declaration", token);

                }
            }
            isInitialized = true;
        }
        return throwSyntaxError("Unexpected end of file", nameToken);
    }

    @Override
    public String toString() {
        return String.format("declare function : %s | args: %s", nameToken, String.join(", ", argumentNames));
    }

    public boolean isOverload(String ... types) {
        if (types.length != argumentNames.size()) {
            return false;
        }
        for (int i = 0 ; i < types.length; i++) {
            DeclarationNode argDeclare = (DeclarationNode) this.getMember(i);
            if (!types[i].equals(argDeclare.getType())) {
                return false;
            }
        }
        return true;
    }
}