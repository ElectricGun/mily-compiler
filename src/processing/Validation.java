package src.processing;

import java.util.*;
import src.evaluators.*;

import static src.constants.Functions.*;
import static src.constants.Keywords.*;
import static src.constants.Maps.*;

/**
 * <h1> Class Validation </h1>
 * Contains utilities for semantic validation, such as type and scope checking.
 * @author ElectricGun
 */

public class Validation {

    /**
     * Validates variable declarations and assigns types to variable references.
     * @param evaluatorTree Abstract syntax tree
     * @throws Exception When declaring a variable that is already declared
     * @throws Exception When referencing a variable that does not exist
     */
    public static void validateDeclarations(EvaluatorTree evaluatorTree, boolean doAssignTypes) throws Exception {
        List<String> declaredVariablesNames = new ArrayList<>();
        List<String> variableTypes = new ArrayList<>();

        validateDeclarationsHelper(evaluatorTree, evaluatorTree.mainBlock, declaredVariablesNames, variableTypes, doAssignTypes);
    }

    private static void validateDeclarationsHelper(EvaluatorTree evaluatorTree,  EvaluatorNode evaluatorNode, List<String> declaredVariablesNames, List<String> variableTypes, boolean doAssignTypes) throws Exception {
        System.out.println("Node: " + evaluatorNode + "\nVariables: " + declaredVariablesNames + "\nTypes: " + variableTypes);

        for (int i = 0; i < evaluatorNode.memberCount(); i++) {
            EvaluatorNode member = evaluatorNode.getMember(i);

            // TODO not optimal implementation

            // types are hardcoded because type checking is not done in this function;
            // only declarations

            if (member instanceof FunctionArgNode functionArgNode) {
                String declaredVar = functionArgNode.getVariableName();
                if (declaredVariablesNames.contains(declaredVar)) {
                    throw new Exception("Variable \"%s\" on line %s is already declared within scope, on file \"%s\"".formatted(functionArgNode.getVariableName(), evaluatorNode.token.line, evaluatorTree.name));
                }
                declaredVariablesNames.add(declaredVar);
                variableTypes.add(functionArgNode.getType());

            } else if (member instanceof DeclarationNode memberDeclaration) {
                String declaredVar = memberDeclaration.getVariableName();
                if (declaredVariablesNames.contains(declaredVar)) {
                    throw new Exception("Variable \"%s\" on line %s is already declared within scope, file \"%s\"".formatted(memberDeclaration.getVariableName(), evaluatorNode.token.line, evaluatorTree.name));
                }
                declaredVariablesNames.add(declaredVar);
                variableTypes.add(memberDeclaration.getType());

            } else if (member instanceof AssignmentNode memberAssignment) {
                String assignedVar = memberAssignment.getVariableName();
                if (!declaredVariablesNames.contains(assignedVar)) {
                    throw new Exception("Variable \"%s\" on line %s is undeclared on file \"%s\"".formatted(memberAssignment.getVariableName(), memberAssignment.token.line, evaluatorTree.name));
                }

                if (memberAssignment.getType().equals(KEY_DATA_UNKNOWN)) {

                    int varIndex = declaredVariablesNames.indexOf(assignedVar);
                    String type = variableTypes.get(varIndex);
                    if (doAssignTypes)
                        memberAssignment.setType(type);
                }
            } else if (member instanceof OperationNode memberOp && isVariableName(memberOp.constantToken)) {
                String assignedVar = memberOp.constantToken.string;
                if (!declaredVariablesNames.contains(assignedVar)) {
                    throw new Exception("Variable \"%s\" on line %s is undeclared on file \"%s\"".formatted(memberOp.constantToken.string, memberOp.token.line, evaluatorTree.name));
                }

                if (memberOp.constantToken.getType().equals(KEY_DATA_UNKNOWN)) {
                    int varIndex = declaredVariablesNames.indexOf(assignedVar);
                    String type = variableTypes.get(varIndex);
                    if (doAssignTypes)
                        memberOp.constantToken.setType(type);
                }
            } else if (member instanceof FunctionCallNode functionDeclareMember) {
                String assignedVar = functionDeclareMember.token.string;
                if (!declaredVariablesNames.contains(assignedVar)) {
                    throw new Exception("Function \"%s\" on line %s is undeclared on file \"%s\"".formatted(functionDeclareMember.token.string, functionDeclareMember.token.line, evaluatorTree.name));
                }
            }
            validateDeclarationsHelper(evaluatorTree, member, new ArrayList<>(declaredVariablesNames), new ArrayList<>(variableTypes), doAssignTypes);
        }
    }

    /**
     * Validates type consistency
     * @param evaluatorTree Abstract syntax tree
     */
    public static void validateTypes (EvaluatorTree evaluatorTree) throws Exception {
        validateTypesHelper(evaluatorTree.mainBlock, null);
    }

//    public static boolean checkTypeValidity(String type, String type2) throws Exception {
//        if (!validTypesMap.containsKey(type))
//            throw new Exception("Unknown type " + type);
//
//        if (type.equals(type2))
//            return true;
//
//        return validTypesMap.get(type).contains(type2);
//    }
//
    public static boolean canImplicitCast(String type, String type2) {
        return operationMap.isOperationValid(KEY_OP_CAST_IMPLICIT, type, type2);
    }
//
//    public static String getImplicitCastType(String type, String type2) throws Exception {
//        if (type.equals(type2))
//            return type;
//
//        if (implicitCastsMap.containsKey(type)) {
//            implicitCastsMap.get(type).contains(type2);
//            return type2;
//        }
//
//        if (implicitCastsMap.containsKey(type2)) {
//            implicitCastsMap.get(type2).contains(type);
//            return type;
//        }
//
//        throw new Exception("Illegal relation between types " + type + " and " + type2);
//    }

    private static String validateTypesHelper(EvaluatorNode evaluatorNode, EvaluatorNode parent) throws Exception {
        String type = KEY_DATA_UNKNOWN;

        System.out.println("Depth: " + evaluatorNode.depth  + " Node: " + evaluatorNode );

        // validate binary operations
        if (evaluatorNode instanceof OperationNode operationNode) {
            if (operationNode.isBinary()) {
                String leftType = KEY_DATA_UNKNOWN;
                String rightType = KEY_DATA_UNKNOWN;

                if (operationNode.getLeftSide() != null) {
                    leftType = validateTypesHelper(operationNode.getLeftSide(), operationNode);
                }

                if (operationNode.getRightSide() != null) {
                    rightType = validateTypesHelper(operationNode.getRightSide(), operationNode);
                }

                type = operationMap.getCastTo(operationNode.getOperator(), leftType, rightType);

            } else if (operationNode.isUnary()) {

                // TODO check if the operator can apply
                type = validateTypesHelper(operationNode.getMember(0), evaluatorNode);

            } else if (operationNode.isConstant()) {

                type = operationNode.constantToken.getType();
            }

            // TODO remove redundancies
        } else if (evaluatorNode instanceof AssignmentNode assignmentNode) {
            String compare = validateTypesHelper(evaluatorNode.getMember(0), evaluatorNode);

            if (!assignmentNode.getType().equals(compare) && !KEY_DATA_LET.equals(assignmentNode.getType()) && !canImplicitCast(compare, assignmentNode.getType())) {
                throw new Exception(String.format("Cannot cast \"%s\" into \"%s\" on line %s", compare, assignmentNode.getType(), evaluatorNode.token.line));
            }

        } else if (evaluatorNode instanceof DeclarationNode declarationNode) {
            EvaluatorNode innerMember = evaluatorNode.getMember(0);
            if (innerMember instanceof OperationNode op) {
                String compare = validateTypesHelper(innerMember, evaluatorNode);

                if (!declarationNode.getType().equals(compare) && !KEY_DATA_LET.equals(declarationNode.getType()) && !canImplicitCast(compare, declarationNode.getType())) {
                    throw new Exception(String.format("Cannot cast \"%s\" into \"%s\" on line %s", compare, declarationNode.getType(), evaluatorNode.token.line));
                }
            }
        } else {
            for (int i = 0; i < evaluatorNode.memberCount(); i++) {
                validateTypesHelper(evaluatorNode.getMember(i), evaluatorNode);
            }
        }

        return type;
    }

    public static String getFunctionBlockReturnType(ScopeNode scopeNode) {
        // TODO implement
        return null;
    }
}
