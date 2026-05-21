package mily.parsing;

import mily.tokens.*;

public class DereferenceOperationNode extends OperationNode {
    public DereferenceOperationNode(Token token, int depth) {
        super(token, depth);
    }
}
