package mily.processing;

import mily.abstracts.*;
import mily.parsing.*;
import mily.parsing.callables.*;
import mily.tokens.*;

import java.util.*;

public class Refining {

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
                fnDeclare.setArgName(a, fnDeclare.getArg(a) + "_" + declarationMap.get(fnDeclare.getArg(a)).hashCode()
                );
            }

            renameByScopeRecursive(fnDeclare.getScope(), new HashMap<>(declarationMap));
            return;
        }

        renameByScopeRecursive(evaluatorNode, new HashMap<>(declarationMap));
    }

    private static void renameNamedByScope(Named named, Map<String, DeclarationNode> declarationMap) {
        DeclarationNode declarator = declarationMap.get(named.getName());
        String newName = named.getName() + "_" + declarator.hashCode();
        named.setName(newName);
    }
}
