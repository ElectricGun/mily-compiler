package src.codegen;

import src.codegen.blocks.IRBlock;

import java.util.*;

public class IRCode {

    public List<IRBlock> irBlocks = new ArrayList<>();

    public void printMlog() {
        for (IRBlock irBlock : irBlocks) {
            System.out.println(irBlock.asMlog());
        }
    }
}
