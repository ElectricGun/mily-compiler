package src.structure;

import src.evaluators.*;
import java.util.*;
import src.constants.*;
import static src.constants.Functions.*;


public class Validation {

    public static void validateDeclarations(EvaluatorTree evaluatorTree) throws Exception {
        List<Variable> declaredVariables = new ArrayList<>();

        validateDeclarationsHelper(evaluatorTree.mainBlock, declaredVariables);
    }

    private static void validateDeclarationsHelper(EvaluatorNode evaluatorNode, List<Variable> declaredVariables) throws Exception {
        for (int i = 0; i < evaluatorNode.memberCount(); i++) {
            EvaluatorNode member = evaluatorNode.getMember(i);

            // TODO not optimal implementation

            if (member instanceof DeclarationNode memberDeclaration) {
                // TODO: type is hardcoded, remember to change after implementing datatypes
                Variable declaredVar = new Variable("var", memberDeclaration.getVariableName());
                if (declaredVariables.contains(declaredVar)) {
                    throw new Exception("Variable \"%s\" on line %s is already declared in scope".formatted(memberDeclaration.getVariableName(), evaluatorNode.token.line));
                }
                declaredVariables.add(declaredVar);

            } else if (member instanceof FunctionArgNode functionArgNode) {
                // TODO: type is hardcoded, remember to change after implementing datatypes
                Variable declaredVar = new Variable("var", functionArgNode.getVariableName());
                if (declaredVariables.contains(declaredVar)) {
                    throw new Exception("Variable \"%s\" on line %s is already declared in scope".formatted(functionArgNode.getVariableName(), evaluatorNode.token.line));
                }
                declaredVariables.add(declaredVar);

            } else if (member instanceof AssignmentNode memberAssignment) {
                // TODO: type is hardcoded, remember to change after implementing datatypes
                Variable assignedVar = new Variable("var", memberAssignment.getVariableName());
                if (!declaredVariables.contains(assignedVar)) {
                    throw new Exception("Variable \"%s\" on line %s is undeclared".formatted(memberAssignment.getVariableName(), memberAssignment.token.line));
                }
            } else if (member instanceof OperationNode memberOp && isVariableName(memberOp.constantToken)) {
                // TODO: type is hardcoded, remember to change after implementing datatypes
                Variable assignedVar = new Variable("var", memberOp.constantToken.string);
                if (!declaredVariables.contains(assignedVar)) {
                    throw new Exception("Variable \"%s\" on line %s is undeclared".formatted(memberOp.constantToken.string, memberOp.token.line));
                }
            }
            validateDeclarationsHelper(member, new ArrayList<>(declaredVariables));
        }
    }
}
