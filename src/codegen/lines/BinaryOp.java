package src.codegen.lines;

import static src.codegen.Mlogs.*;

public class BinaryOp extends Line {

    String op;
    String left;
    String right;

    public BinaryOp(String name, String op, String left, String right) {
        super(name, "");

        this.left = left;
        this.right = right;
        this.name = name;
        this.op = op;
    }

    @Override
    public String asMlog() {
        return "op " + opAsMlog(op) + " " + name +  " " + left + " " + right;
    }

    @Override
    public String toString() {
        return name + ": " + left + " " +  op  + " " + right;
    }
}
