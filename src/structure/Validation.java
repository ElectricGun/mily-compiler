package src.structure;

import java.util.*;
import src.evaluators.*;

import static src.constants.Functions.*;

/**
 * <h1> Class Validation </h1>
 * Contains utilities for semantic validation, such as type and scope checking.
 * @author ElectricGun
 */

public class Validation {

    public static void validateDeclarations(EvaluatorTree evaluatorTree) throws Exception {
        List<String> declaredVariablesNames = new ArrayList<>();

        validateDeclarationsHelper(evaluatorTree, evaluatorTree.mainBlock, declaredVariablesNames);
    }

    private static void validateDeclarationsHelper(EvaluatorTree evaluatorTree,  EvaluatorNode evaluatorNode, List<String> declaredVariablesNames) throws Exception {
        System.out.println("Node: " + evaluatorNode + "\nVariables: " + declaredVariablesNames);

        for (int i = 0; i < evaluatorNode.memberCount(); i++) {
            EvaluatorNode member = evaluatorNode.getMember(i);

            // TODO not optimal implementation

            // types are hardcoded because type checking is not done in this function;
            // only declarations

            if (member instanceof FunctionArgNode functionArgNode) {
                String declaredVar = functionArgNode.getVariableName();
                if (declaredVariablesNames.contains(declaredVar)) {
                    throw new Exception("Variable \"%s\" on line %s is already declared in scope \"%s\"".formatted(functionArgNode.getVariableName(), evaluatorNode.token.line, evaluatorTree.name));
                }
                declaredVariablesNames.add(declaredVar);

            } else if (member instanceof DeclarationNode memberDeclaration) {
                String declaredVar = memberDeclaration.getVariableName();
                if (declaredVariablesNames.contains(declaredVar)) {
                    throw new Exception("Variable \"%s\" on line %s is already declared in scope \"%s\"".formatted(memberDeclaration.getVariableName(), evaluatorNode.token.line, evaluatorTree.name));
                }
                declaredVariablesNames.add(declaredVar);

            } else if (member instanceof AssignmentNode memberAssignment) {
                String assignedVar = memberAssignment.getVariableName();
                if (!declaredVariablesNames.contains(assignedVar)) {
                    throw new Exception("Variable \"%s\" on line %s is undeclared \"%s\"".formatted(memberAssignment.getVariableName(), memberAssignment.token.line, evaluatorTree.name));
                }
            } else if (member instanceof OperationNode memberOp && isVariableName(memberOp.constantToken)) {
                String assignedVar = memberOp.constantToken.string;
                if (!declaredVariablesNames.contains(assignedVar)) {
                    throw new Exception("Variable \"%s\" on line %s is undeclared \"%s\"".formatted(memberOp.constantToken.string, memberOp.token.line, evaluatorTree.name));
                }
            } else if (member instanceof FunctionCallNode functionDeclareMember) {
                String assignedVar = functionDeclareMember.token.string;
                if (!declaredVariablesNames.contains(assignedVar)) {
                    throw new Exception("Function \"%s\" on line %s is undeclared on file \"%s\"".formatted(functionDeclareMember.token.string, functionDeclareMember.token.line, evaluatorTree.name));
                }
            }
            validateDeclarationsHelper(evaluatorTree, member, new ArrayList<>(declaredVariablesNames));
        }
    }

    //TODO implement
    public static void validateTypes (EvaluatorTree evaluatorTree) throws Exception {

    }
}
