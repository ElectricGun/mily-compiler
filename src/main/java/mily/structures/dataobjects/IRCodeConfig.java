package mily.structures.dataobjects;

import mily.codegen.*;
import mily.codegen.blocks.*;
import mily.parsing.*;
import mily.parsing.callables.*;
import mily.utils.*;

import java.util.*;

public class IRCodeConfig {

    public IRCode irCode;
    public Map<FunctionDeclareNode, IRFunction> irFunctionMap;
    public Map<String, DeclarationNode> declarationMap;
    public Map<CallableSignature, CallableNode> callableNodeMap;
    public HashCodeSimplifier hashCodeSimplifier;
    public boolean generateComments;
    public boolean debugMode;
}
