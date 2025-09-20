package mily.parsing;

import mily.constants.*;
import mily.interfaces.*;
import mily.structures.dataobjects.*;
import mily.tokens.*;

import java.util.*;

import static mily.constants.Keywords.*;

public class DatatypeNode extends EvaluatorNode implements Typed {

    protected Type type;

    public DatatypeNode(String datatype, Token nameToken, int depth) {
        super(nameToken, depth);
        this.type = new Type(datatype);
    }

    public static Type processType(Token type, List<Token> tokenList, EvaluatorTree evaluatorTree) throws NullPointerException {
        DatatypeNode datatypeNode = new DatatypeNode(type.string, type, 0);
        DatatypeNode out = (DatatypeNode) datatypeNode.evaluate(tokenList, evaluatorTree);

        if (out == null) {
            throw new NullPointerException("DatatypeNode in processType() is null!");
        }

        return out.getType();
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree) {
        String indent = " ".repeat(depth);

        boolean parsingDiamond = false;
        boolean expectingType = false;

        while (!tokenList.isEmpty()) {
            Token token = tokenList.remove(0);

            if (token.isWhiteSpace()) {
                continue;
            }

            if (!parsingDiamond) {
                if (token.equalsKey(KEY_DIAMOND_OPEN)) {
                    parsingDiamond = true;
                    expectingType = true;

                } else {
                    tokenList.add(0, token);
                    return this;
                }
            } else {
                if (expectingType && Functions.isVariableOrDeclarator(token)) {
                    DatatypeNode datatypeNode = new DatatypeNode(token.string, token, depth + 1);
                    datatypeNode.evaluate(tokenList, evaluatorTree);
                    type.diamondTypes.add(datatypeNode.type);
                    expectingType = false;

                } else if (!expectingType && token.equalsKey(KEY_COMMA)) {
                    expectingType = true;

                } else if (token.equalsKey(KEY_DIAMOND_CLOSE)) {
                    return this;

                } else {
                    return throwSyntaxError("Unexpected token in datatype", token);
                }
            }
        }
        return throwSyntaxError("Unexpected end of file", nameToken);
    }

    @Override
    public String errorName() {
        return "";
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void setType(Type type) {
        this.type = type;
    }
}
