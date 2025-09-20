package mily.parsing;

import mily.interfaces.*;
import mily.tokens.*;

public class SpaceNode extends EvaluatorNode implements Named {

    protected String name;
    protected ScopeNode scopeNode;

    public SpaceNode(String name, Token nameToken, int depth) {
        super(nameToken, depth);
        this.name = name;
    }

    @Override
    public String errorName() {
        return "space " + "\"" + getName() + "\"";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
