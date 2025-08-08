package mily.codegen.lines;

import static mily.codegen.Mlogs.*;

public class BinaryOp extends VariableLine {

    String op;
    String left;
    String right;

    public BinaryOp(String varName, String op, String left, String right, int indent) {
        super(varName, "", indent);

        this.left = left;
        this.right = right;
        this.op = op;
    }

    @Override
    public String asMlog() {
        return indent() + "op " + opAsMlog(op) + " " + varName + " " + left + " " + right;
    }

    @Override
    public String toString() {
        return varName + ": " + left + " " + op + " " + right;
    }

    public String getOp() {
        return op;
    }

    public String getLeft() {
        return left;
    }

    public String getRight() {
        return right;
    }
}
