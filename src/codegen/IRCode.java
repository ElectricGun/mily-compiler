package src.codegen;

import src.codegen.blocks.*;
import src.codegen.lines.*;

import java.util.*;

public class IRCode {

    public List<IRBlock> irBlocks = new ArrayList<>();

    public void printMlog() {
        for (IRBlock irBlock : irBlocks) {
            if (!irBlock.lineList.isEmpty())
                System.out.println(irBlock.asMlog());
        }
    }

    public void addSingleLineBlock(Line line) {
        IRBlock irBlock = new IRBlock();
        irBlock.lineList.add(line);
        irBlocks.add(irBlock);
    }
}
