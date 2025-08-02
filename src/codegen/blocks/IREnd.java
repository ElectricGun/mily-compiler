package src.codegen.blocks;

import src.codegen.lines.*;

// todo this doesn't seem good
public class IREnd extends IRBlock {

    public IREnd() {
        lineList.add(new Line("end", 0));
    }

    @Override
    public String asMlog() {
        return "end";
    }
}
