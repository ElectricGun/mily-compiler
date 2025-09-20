package mily.structures.dataobjects;

import mily.parsing.*;

public record UnaryToBinaryStruct(OperationNode oldOp, OperationNode newOp, OperationNode child, OperationNode factor) {

    @Override
    @SuppressWarnings("unused")
    public OperationNode child() {
        return child;
    }
}
