package mily.parsing;

import mily.abstracts.*;
import mily.structures.structs.*;
import mily.tokens.*;

import java.util.*;

public class DatatypeNode extends EvaluatorNode implements Typed {

    protected Type type;

    public DatatypeNode(String datatype, Token nameToken, int depth) {
        super(nameToken, depth);
        this.type = new Type(datatype);
    }

    @Override
    protected EvaluatorNode evaluator(List<Token> tokenList, EvaluatorTree evaluatorTree) throws Exception {

        System.out.println("[WARN] DatatypeNode does nothing!");
        return this;
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
