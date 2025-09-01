package mily.codegen;

import mily.codegen.blocks.*;
import mily.codegen.lines.*;

import java.util.*;

public class IRCode {

    public final List<IRBlock> irBlocks = new ArrayList<>();

    public String generateMlog() {
        StringBuilder out = new StringBuilder();

        for (int i = 0; i < irBlocks.size(); i++) {
            IRBlock irBlock = irBlocks.get(i);

            if (!irBlock.lineList.isEmpty()) {
                out.append(irBlock.asMlog());

                if (i < irBlocks.size() - 1) {
                    out.append("\n");
                }
            }
        }
        return out.toString();
    }

    public void printMlog() {
        System.out.println(generateMlog());
    }

    public void addSingleLineBlock(Line line) {
        IRBlock irBlock = new IRBlock();
        irBlock.lineList.add(line);
        irBlocks.add(irBlock);
    }
}
