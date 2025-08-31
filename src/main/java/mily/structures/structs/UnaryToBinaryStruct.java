package mily.structures.structs;

import mily.parsing.*;

public class UnaryToBinaryStruct {

    protected OperationNode oldOp;
    protected OperationNode newOp;
    protected OperationNode child;
    protected OperationNode factor;

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
