package mily.parsing.callables;

import mily.abstracts.*;
import mily.parsing.*;
import mily.structures.errors.JavaMilySyntaxException;
import mily.structures.structs.CallableSignature;
import mily.structures.structs.Type;
import mily.tokens.*;

import java.util.*;

import static mily.constants.Functions.*;
import static mily.constants.Keywords.*;

public abstract class CallableNode extends EvaluatorNode implements Callable {

    protected final List<String> argumentNames = new ArrayList<>();
    protected final List<Type> argumentTypes = new ArrayList<>();
    protected String name;
    protected Type returnType;

    public CallableNode(String name, Token nameToken, int depth) {
        super(nameToken, depth);

        this.name = name;
    }

    public CallableSignature signature() {
        return new CallableSignature(name, argumentTypes);
    }

    @Override
    public List<Type> getArgumentTypes() {
        return argumentTypes;
    }

    @Override
    public List<String> getArgumentNames() {
        return argumentNames;
    }

    @Override
    public Type getType() {
        return returnType;
    }

    @Override
    public void setType(Type type) {
        this.returnType = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public void setArgName(int i, String newArg) {
        argumentNames.set(i, newArg);
    }

    public String[] getArgumentNamesArr() {
        String[] out = new String[argumentNames.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = argumentNames.get(i);
        }
        return out;
    }

    public Type[] getArgumentTypesArr() {
        Type[] out = new Type[argumentTypes.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = argumentTypes.get(i);
        }
        return out;
    }

    public int getArgCount() {
        return argumentNames.size();
    }

    public String getArg(int i) {
        return argumentNames.get(i);
    }

    public Type getArgType(int i) {
        return argumentTypes.get(i);
    }

    protected void processArgs(List<Token> tokenList, EvaluatorTree evaluatorTree) throws JavaMilySyntaxException {
        String indent = " ".repeat(depth);

        boolean isInitialized = false;
        boolean argumentWanted = false;

        if (evaluatorTree.debugMode)
            System.out.printf(indent + "Parsing Function %s:%n", this.nameToken);

        while (!tokenList.isEmpty()) {
            Token token = tokenList.remove(0);
            if (evaluatorTree.debugMode)
                System.out.printf(indent + "function\t:\t%s\t:\t%s%n", this.nameToken, token);

            if (isWhiteSpace(token)) {
                continue;

            } else if (isPunctuation(token) && !isWhiteSpace(token)) {
                if (argumentWanted) {
                    throw new JavaMilySyntaxException("Expecting an argument on function declaration", token);

                } else if (keyEquals(KEY_BRACKET_CLOSE, token)) {
                    return;

                } else if (keyEquals(KEY_COMMA, token)) {
                    argumentWanted = true;

                } else {
                    throw new JavaMilySyntaxException("Unexpected punctuation on function declaration", token);

                }
            } else if (isOperator(token)) {
                throw new JavaMilySyntaxException("Unexpected operator on function declaration", token);

            } else if (isVariableOrDeclarator(token)) {
                Type type = DatatypeNode.processType(token, tokenList, evaluatorTree);
                argumentTypes.add(type);
                Token variableName = EvaluatorNode.fetchNextNonWhitespaceToken(tokenList);

                if (!isVariableName(variableName)) {
                    throw new JavaMilySyntaxException("Not a variable name on function declaration", token);

                } else if (!isInitialized || argumentWanted) {
                    argumentNames.add(variableName.string);
                    argumentWanted = false;

                    FunctionArgNode functionArgNode = new FunctionArgNode(type, variableName, depth + 1);
                    functionArgNode.setName(variableName.string);
                    members.add(functionArgNode);

                    if (evaluatorTree.debugMode)
                        System.out.printf("Added argument %s%n", variableName);

                } else {
                    throw new JavaMilySyntaxException("Unexpected token on function declaration", token);

                }
            }
            isInitialized = true;
        }
        throw new JavaMilySyntaxException("Unexpected end of file", nameToken);
    }
}
