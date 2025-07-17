package src.structure;

import java.util.*;
import src.evaluators.*;

import static src.constants.Functions.*;

public class Validation {

    public static void validateDeclarations(EvaluatorTree evaluatorTree) throws Exception {
        List<String> declaredVariablesNames = new ArrayList<>();

        validateDeclarationsHelper(evaluatorTree.mainBlock, declaredVariablesNames);
    }

    private static void validateDeclarationsHelper(EvaluatorNode evaluatorNode, List<String> declaredVariablesNames) throws Exception {
        System.out.println("Node: " + evaluatorNode + "\nVariables: " + declaredVariablesNames);

        for (int i = 0; i < evaluatorNode.memberCount(); i++) {
            EvaluatorNode member = evaluatorNode.getMember(i);

            // TODO not optimal implementation


            // types are hardcoded because type checking is not done in this function;
            // only declarations

            if (member instanceof FunctionArgNode functionArgNode) {
                String declaredVar = functionArgNode.getVariableName();
                if (declaredVariablesNames.contains(declaredVar)) {
                    throw new Exception("Variable \"%s\" on line %s is already declared in scope".formatted(functionArgNode.getVariableName(), evaluatorNode.token.line));
                }
                declaredVariablesNames.add(declaredVar);

            } else if (member instanceof DeclarationNode memberDeclaration) {
                String declaredVar = memberDeclaration.getVariableName();
                if (declaredVariablesNames.contains(declaredVar)) {
                    throw new Exception("Variable \"%s\" on line %s is already declared in scope".formatted(memberDeclaration.getVariableName(), evaluatorNode.token.line));
                }
                declaredVariablesNames.add(declaredVar);

            } else if (member instanceof AssignmentNode memberAssignment) {
                String assignedVar = memberAssignment.getVariableName();
                if (!declaredVariablesNames.contains(assignedVar)) {
                    throw new Exception("Variable \"%s\" on line %s is undeclared".formatted(memberAssignment.getVariableName(), memberAssignment.token.line));
                }
            } else if (member instanceof OperationNode memberOp && isVariableName(memberOp.constantToken)) {
                String assignedVar = memberOp.constantToken.string;
                if (!declaredVariablesNames.contains(assignedVar)) {
                    throw new Exception("Variable \"%s\" on line %s is undeclared".formatted(memberOp.constantToken.string, memberOp.token.line));
                }
            }
            validateDeclarationsHelper(member, new ArrayList<>(declaredVariablesNames));
        }
    }
}
