package src.codegen.blocks;

import src.codegen.lines.*;

import java.util.*;

public class IRBlock {

    public final List<Line> lineList = new ArrayList<>();

    public void addLine(Line line) {
        lineList.add(line);
    }

    public String asMlog() {
        StringBuilder out = new StringBuilder();

        for (int i = 0; i < lineList.size(); i++) {
            Line line = lineList.get(i);
            out.append(line.asMlog());

            if (i < lineList.size() - 1) {
                out.append("\n");
            }
        }

        return out.toString();
    }
}
