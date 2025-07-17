package src.structure;

import src.evaluators.*;
import java.util.*;
import static src.constants.Functions.*;
import static src.constants.Keywords.*;

public class Validation {

    public static void validateDeclarations(EvaluatorTree evaluatorTree) throws Exception {
        List<Variable> declaredVariables = new ArrayList<>();

        validateDeclarationsHelper(evaluatorTree.mainBlock, declaredVariables);
    }

    private static void validateDeclarationsHelper(EvaluatorNode evaluatorNode, List<Variable> declaredVariables) throws Exception {
        System.out.println("Node: " + evaluatorNode + "\nVariables: " + declaredVariables);

        for (int i = 0; i < evaluatorNode.memberCount(); i++) {
            EvaluatorNode member = evaluatorNode.getMember(i);

            // TODO not optimal implementation


            // types are hardcoded because type checking is not done in this function;
            // only declarations

            if (member instanceof FunctionArgNode functionArgNode) {
                Variable declaredVar = new Variable(KEY_LET, functionArgNode.getVariableName());
                if (declaredVariables.contains(declaredVar)) {
                    throw new Exception("Variable \"%s\" on line %s is already declared in scope".formatted(functionArgNode.getVariableName(), evaluatorNode.token.line));
                }
                declaredVariables.add(declaredVar);

            } else if (member instanceof DeclarationNode memberDeclaration) {
                Variable declaredVar = new Variable(KEY_LET, memberDeclaration.getVariableName());
                if (declaredVariables.contains(declaredVar)) {
                    throw new Exception("Variable \"%s\" on line %s is already declared in scope".formatted(memberDeclaration.getVariableName(), evaluatorNode.token.line));
                }
                declaredVariables.add(declaredVar);

            } else if (member instanceof AssignmentNode memberAssignment) {
                Variable assignedVar = new Variable(KEY_LET, memberAssignment.getVariableName());
                if (!declaredVariables.contains(assignedVar)) {
                    throw new Exception("Variable \"%s\" on line %s is undeclared".formatted(memberAssignment.getVariableName(), memberAssignment.token.line));
                }
            } else if (member instanceof OperationNode memberOp && isVariableName(memberOp.constantToken)) {
                Variable assignedVar = new Variable(KEY_LET, memberOp.constantToken.string);
                if (!declaredVariables.contains(assignedVar)) {
                    throw new Exception("Variable \"%s\" on line %s is undeclared".formatted(memberOp.constantToken.string, memberOp.token.line));
                }
            }
            validateDeclarationsHelper(member, new ArrayList<>(declaredVariables));
        }
    }
}
