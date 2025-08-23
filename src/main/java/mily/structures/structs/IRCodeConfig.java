package mily.structures.structs;

import mily.codegen.*;
import mily.codegen.blocks.*;
import mily.parsing.callables.*;
import mily.utils.*;

import java.util.*;

public class IRCodeConfig {

    public IRCode irCode;
    public Map<String, IRFunction> irFunctionMap;
    public Map<String, RawTemplateDeclareNode> templateNodeMap;
    public HashCodeSimplifier hashCodeSimplifier;
    public boolean generateComments;
    public boolean debugMode;
}
