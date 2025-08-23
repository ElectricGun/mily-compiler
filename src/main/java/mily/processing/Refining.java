package mily.processing;

import mily.parsing.*;
import mily.parsing.callables.*;
import mily.tokens.*;

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
        renameByScopeRecursive(evaluatorTree.mainBlock, new HashMap<>());
    }

    private static void renameByScopeRecursive(EvaluatorNode evaluatorNode, Map<String, DeclarationNode> declarationMap) {
        for (int i = 0; i < evaluatorNode.memberCount(); i++) {
            EvaluatorNode member = evaluatorNode.getMember(i);

            _renameByScope(member, declarationMap);
        }
    }

    private static void _renameByScope(EvaluatorNode evaluatorNode, Map<String, DeclarationNode> declarationMap) {
        if (evaluatorNode instanceof DeclarationNode dec) {
            declarationMap.put(dec.getName(), dec);
            dec.setName(dec.getName() + "_" + dec.hashCode());

        } else if (evaluatorNode instanceof AssignmentNode assign) {
            assign.setName(assign.getName() + "_" + declarationMap.get(assign.getName()).hashCode());

        } else if (evaluatorNode instanceof OperationNode op) {
            if (op.getConstantToken() != null) {
                TypedToken constantToken = op.getConstantToken();

                if (constantToken instanceof CallerNodeToken callerNodeToken) {
                    _renameByScope(callerNodeToken.getNode(), new HashMap<>(declarationMap));

                } else if (constantToken.isVariableRef()) {
                    constantToken.setName(constantToken.getName() + "_" + declarationMap.get(constantToken.getName()).hashCode());
                }
            }
        } else if (evaluatorNode instanceof FunctionDeclareNode fnDeclare) {
            for (int m = 0; m < fnDeclare.memberCount(); m++) {
                EvaluatorNode fnMember = fnDeclare.getMember(m);

                if (fnMember instanceof DeclarationNode fnArgDec) {
                    declarationMap.put(fnArgDec.getName(), fnArgDec);
                    fnArgDec.setName(fnArgDec.getName() + "_" + fnArgDec.hashCode());
                }
            }
            for (int a = 0; a < fnDeclare.getArgCount(); a++) {
                fnDeclare.setArgName(a, fnDeclare.getArg(a) + "_" + declarationMap.get(fnDeclare.getArg(a)).hashCode());
            }

            renameByScopeRecursive(fnDeclare.getScope(), new HashMap<>(declarationMap));
            return;
        }

        renameByScopeRecursive(evaluatorNode, new HashMap<>(declarationMap));
    }

//    private static void renameNamedByScope(Named named, Map<String, DeclarationNode> declarationMap) {
//        DeclarationNode declarator = declarationMap.get(named.getName());
//        String newName = named.getName() + "_" + declarator.hashCode();
//        named.setName(newName);
//    }
}
