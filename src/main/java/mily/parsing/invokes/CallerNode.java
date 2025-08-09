package mily.parsing.invokes;

import mily.abstracts.*;
import mily.parsing.EvaluatorNode;
import mily.parsing.OperationNode;
import mily.tokens.Token;

// TOOD implement this
public abstract class CallerNode extends EvaluatorNode implements Caller {

    public CallerNode(Token nameToken, int depth) {
        super(nameToken, depth);
    }

    @Override
    public OperationNode getArg(int i) {
        return null;
    }

    @Override
    public int getArgCount() {
        return 0;
    }

    @Override
    public String getFnKey() {
        return "";
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getType() {
        return "";
    }

    @Override
    public void setType(String type) {

    }
}
