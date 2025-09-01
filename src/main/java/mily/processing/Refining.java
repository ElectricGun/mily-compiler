package mily.processing;

import mily.parsing.*;
import mily.parsing.callables.*;
import mily.tokens.*;
import mily.utils.HashCodeSimplifier;

import java.util.*;

public class Refining {

    public static void renameVars(EvaluatorTree evaluatorTree) {
        renameVarsRecursive(evaluatorTree.mainBlock, new HashMap<>());
    }

    private static void renameVarsRecursive(EvaluatorNode evaluatorNode, Map<String, String> renameMap) {
        for (int i = 0; i < evaluatorNode.memberCount(); i++) {
            EvaluatorNode member = evaluatorNode.getMember(i);

            _renameVars(member, renameMap);
        }
    }

    private static void _renameVars(EvaluatorNode member, Map<String, String> renameMap) {
        if (member instanceof FunctionDeclareNode fnDeclare) {
            for (int m = 0; m < fnDeclare.memberCount(); m++) {
                EvaluatorNode fnMember = fnDeclare.getMember(m);

                if (fnMember instanceof DeclarationNode fnArgDec) {
                    String newName = "arg_" + fnArgDec.getName();
                    renameMap.put(fnArgDec.getName(), newName);
                    fnArgDec.setName(newName);
                }
            }
            for (int a = 0; a < fnDeclare.getArgCount(); a++) {
                fnDeclare.setArgName(a, renameMap.get(fnDeclare.getArg(a)));
            }

            renameVarsRecursive(fnDeclare.getScope(), new HashMap<>(renameMap));
            return;

        } else if (member instanceof DeclarationNode dec) {
            String newName = "user_" + dec.getName();
            renameMap.put(dec.getName(), newName);
            dec.setName(newName);

        } else if (member instanceof AssignmentNode assign) {
            assign.setName(renameMap.get(assign.getName()));

        } else if (member instanceof OperationNode op) {
            if (op.getConstantToken() != null) {
                TypedToken ct = op.getConstantToken();

                if (ct instanceof CallerNodeToken callerNodeToken) {
                    _renameVars(callerNodeToken.getNode(), renameMap);

                } else if (ct.isVariableRef()) {
                    ct.setName(renameMap.get(ct.getName()));
                }
            }
        }

        renameVarsRecursive(member, new HashMap<>(renameMap));
    }

    public static void renameByScope(EvaluatorTree evaluatorTree) {
        renameByScopeRecursive(evaluatorTree.mainBlock, new HashMap<>(), new HashCodeSimplifier());
    }

    private static void renameByScopeRecursive(EvaluatorNode evaluatorNode, Map<String, DeclarationNode> declarationMap, HashCodeSimplifier hcs) {
        for (int i = 0; i < evaluatorNode.memberCount(); i++) {
            EvaluatorNode member = evaluatorNode.getMember(i);

            _renameByScope(member, declarationMap, hcs);
        }
    }

    private static void _renameByScope(EvaluatorNode evaluatorNode, Map<String, DeclarationNode> declarationMap, HashCodeSimplifier hcs) {
        if (evaluatorNode instanceof DeclarationNode dec) {
            declarationMap.put(dec.getName(), dec);
            dec.setName(dec.getName() + "_" + hcs.simplifyHash(dec.hashCode()));

        } else if (evaluatorNode instanceof AssignmentNode assign) {
            assign.setName(assign.getName() + "_" + hcs.simplifyHash(declarationMap.get(assign.getName()).hashCode()));

        } else if (evaluatorNode instanceof OperationNode op) {
            if (op.getConstantToken() != null) {
                TypedToken constantToken = op.getConstantToken();

                if (constantToken instanceof CallerNodeToken callerNodeToken) {
                    _renameByScope(callerNodeToken.getNode(), new HashMap<>(declarationMap), hcs);

                } else if (constantToken.isVariableRef()) {
                    constantToken.setName(constantToken.getName() + "_" + hcs.simplifyHash(declarationMap.get(constantToken.getName()).hashCode()));
                }
            }
        } else if (evaluatorNode instanceof FunctionDeclareNode fnDeclare) {
            for (int m = 0; m < fnDeclare.memberCount(); m++) {
                EvaluatorNode fnMember = fnDeclare.getMember(m);

                if (fnMember instanceof DeclarationNode fnArgDec) {
                    declarationMap.put(fnArgDec.getName(), fnArgDec);
                    fnArgDec.setName(fnArgDec.getName() + "_" + hcs.simplifyHash(fnArgDec.hashCode()));
                }
            }
            for (int a = 0; a < fnDeclare.getArgCount(); a++) {
                fnDeclare.setArgName(a, fnDeclare.getArg(a) + "_" + hcs.simplifyHash(declarationMap.get(fnDeclare.getArg(a)).hashCode()));
            }

            renameByScopeRecursive(fnDeclare.getScope(), new HashMap<>(declarationMap), hcs);
            return;
        }

        renameByScopeRecursive(evaluatorNode, new HashMap<>(declarationMap), hcs);
    }

    public static void addVoidReturns(EvaluatorTree evaluatorTree) {
        addVoidReturnsRecursive(evaluatorTree.mainBlock);
    }

    private static void addVoidReturnsRecursive(EvaluatorNode evaluatorNode) {
        if (evaluatorNode instanceof FunctionDeclareNode fn) {
            ScopeNode sc = fn.getScope();
            if (!(sc.memberCount() > 0 && sc.getMember(sc.memberCount() - 1) instanceof OperationNode op && op.isReturnOperation())) {
                EvaluatorNode lastMember = sc.getMember(sc.memberCount() - 1);
                OperationNode returnOperation = new OperationNode(new Token("return", lastMember.nameToken.source, lastMember.nameToken.line), lastMember.depth);
                returnOperation.setReturnOperation(true);
                returnOperation.setConstantToken(new VoidToken(lastMember.nameToken.source, lastMember.nameToken.line));
                sc.appendMember(returnOperation);
            }
        }

        for (int i = 0; i < evaluatorNode.memberCount(); i++) {
            addVoidReturnsRecursive(evaluatorNode.getMember(i));
        }
    }
}
