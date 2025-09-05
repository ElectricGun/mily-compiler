package mily.structures.dataobjects;

import mily.codegen.*;
import mily.codegen.blocks.*;
import mily.parsing.*;
import mily.parsing.callables.*;
import mily.utils.*;

import java.util.Map;

public record IRScopeConfig(
        IRCode irCode,
        Map<FunctionDeclareNode, IRFunction> irFunctionMap,
        Map<String, DeclarationNode> declarationMap,
        Map<CallableSignature, CallableNode> callableNodeMap,
        HashCodeSimplifier hashCodeSimplifier,
        boolean generateComments,
        boolean debugMode
) {
    public IRScopeConfig copy() {
        return new IRScopeConfig(irCode, irFunctionMap, declarationMap, callableNodeMap, hashCodeSimplifier, generateComments, debugMode);
    }
}
