package src.codegen.blocks;

import src.codegen.lines.*;

public class IREnd extends IRBlock {

    public IREnd() {
        lineList.add(new Line("end", "end", 0));
    }

    @Override
    public String asMlog() {
        return "end";
    }
}
