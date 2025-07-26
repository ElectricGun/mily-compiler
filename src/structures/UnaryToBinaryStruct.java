package src.structures;

import src.evaluators.*;

public class UnaryToBinaryStruct {
    OperationNode oldOp;
    OperationNode newOp;
    OperationNode child;
    OperationNode factor;

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

    public OperationNode getChild() {
        return child;
    }

    public OperationNode getFactor() {
        return factor;
    }
}
