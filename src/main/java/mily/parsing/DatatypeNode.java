package mily.parsing;

import mily.abstracts.*;
import mily.constants.Functions;
import mily.structures.structs.*;
import mily.tokens.*;

import java.util.*;
import java.util.stream.StreamSupport;

import static mily.constants.Keywords.*;

public class DatatypeNode extends EvaluatorNode implements Typed {

    protected Type type;

    public DatatypeNode(String datatype, Token nameToken, int depth) {
        super(nameToken, depth);
        this.type = new Type(datatype);
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree) throws Exception {
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
