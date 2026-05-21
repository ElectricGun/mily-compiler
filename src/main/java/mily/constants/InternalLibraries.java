package mily.constants;

import java.util.*;

public class InternalLibraries {
    public static HashMap<String, String> internalLibraryMap = new HashMap<>();
    static {
        internalLibraryMap.put("std/bulb.mily", "/std/bulb.mily");
        internalLibraryMap.put("std/control.mily", "/std/control.mily");
        internalLibraryMap.put("std/flow.mily", "/std/flow.mily");
        internalLibraryMap.put("std/math.mily", "/std/math.mily");
        internalLibraryMap.put("std/mem.mily", "/std/mem.mily");
        internalLibraryMap.put("std/prints.mily", "/std/prints.mily");

        internalLibraryMap.put("/std/bulb.mily", "/std/bulb.mily");
        internalLibraryMap.put("/std/control.mily", "/std/control.mily");
        internalLibraryMap.put("/std/flow.mily", "/std/flow.mily");
        internalLibraryMap.put("/std/math.mily", "/std/math.mily");
        internalLibraryMap.put("/std/mem.mily", "/std/mem.mily");
        internalLibraryMap.put("/std/prints.mily", "/std/prints.mily");
    }
}
