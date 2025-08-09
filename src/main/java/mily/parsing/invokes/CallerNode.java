package mily.parsing.invokes;

import mily.abstracts.*;
import mily.parsing.*;
import mily.tokens.*;

import java.util.*;

import static mily.constants.Keywords.KEY_DATA_UNKNOWN;

// TOOD implement this
public abstract class CallerNode extends EvaluatorNode implements Caller {

    protected List<OperationNode> arguments = new ArrayList<>();
    protected String type = KEY_DATA_UNKNOWN;

    public CallerNode(Token nameToken, int depth) {
        super(nameToken, depth);
    }

    @Override
    public OperationNode getArg(int i) {
        return arguments.get(i);
    }

    @Override
    public int getArgCount() {
        return arguments.size();
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }
}
