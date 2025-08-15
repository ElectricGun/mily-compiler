package mily.parsing;

import mily.tokens.*;

public class SpaceNode extends EvaluatorNode {

    protected ScopeNode scopeNode;

    public SpaceNode(Token nameToken, int depth) {
        super(nameToken, depth);
    }
}
