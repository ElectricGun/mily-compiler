package mily.parsing;

import mily.tokens.*;

public class SpaceNode extends EvaluatorNode {

    ScopeNode scopeNode;

    public SpaceNode(Token nameToken, int depth) {
        super(nameToken, depth);
    }
}
