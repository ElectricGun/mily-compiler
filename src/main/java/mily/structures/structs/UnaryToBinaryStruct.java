package mily.structures.structs;

import mily.parsing.*;

public class UnaryToBinaryStruct {

    protected final OperationNode oldOp;
    protected final OperationNode newOp;
    protected final OperationNode child;
    protected final OperationNode factor;

    public UnaryToBinaryStruct(OperationNode oldOp, OperationNode newOp, OperationNode child, OperationNode factor) {
        this.oldOp = oldOp;
        this.newOp = newOp;
        this.child = child;
        this.factor = factor;
    }

    public OperationNode getOldOp() {
        return oldOp;
    }

    public OperationNode getNewOp() {
        return newOp;
    }

    @SuppressWarnings("unused")
    public OperationNode getChild() {
        return child;
    }

    public OperationNode getFactor() {
        return factor;
    }
}
