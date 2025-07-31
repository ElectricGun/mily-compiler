package src.codegen.lines;

import static src.codegen.Mlogs.*;

public class BinaryOp extends Line {

    String op;
    String left;
    String right;

    public BinaryOp(String name, String op, String left, String right, int indent) {
        super(name, "", indent);

        this.left = left;
        this.right = right;
        this.name = name;
        this.op = op;
    }

    @Override
    public String asMlog() {
        return indent() + "op " + opAsMlog(op) + " " + name +  " " + left + " " + right;
    }

    @Override
    public String toString() {
        return name + ": " + left + " " +  op  + " " + right;
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
