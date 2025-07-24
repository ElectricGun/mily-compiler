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

    public static boolean checkThrowables(EvaluatorTree evaluatorTree, boolean debugMode) {
        boolean[] errored = new boolean[] {false};

        Stack<EvaluatorNode> traceStack = new Stack<>();
        checkThrowablesHelper(evaluatorTree.mainBlock, traceStack, errored, debugMode);

        return errored[0];
    }

    public static void checkThrowablesHelper(EvaluatorNode evaluatorNode, Stack<EvaluatorNode> traceStack, boolean[] errored, boolean debugMode) {

        // TODO: not the most elegant solution

        traceStack.push(evaluatorNode);

        if (evaluatorNode.isErrored()) {
            errored[0] = true;

            System.out.print("\033[31m");
            System.out.println(evaluatorNode.getThrowable().getErrorMessage());

            Stack<EvaluatorNode> newStack = new Stack<>();
            newStack.addAll(traceStack);

            while (!newStack.isEmpty()) {
                EvaluatorNode trace = newStack.pop();
                System.out.printf("\tat %s, line %s%n", trace, trace.token.line);
            }
            System.out.print("\033[0m");
        }

        for (int i = 0; i < evaluatorNode.memberCount(); i ++) {
            Stack<EvaluatorNode> newStack = new Stack<>();
            newStack.addAll(traceStack);

            checkThrowablesHelper(evaluatorNode.getMember(i), newStack, errored, debugMode);
        }
    }

    /**
     * Validates variable declarations and assigns types to variable references.
     * @param evaluatorTree Abstract syntax tree
     * @throws Exception When declaring a variable that is already declared
     * @throws Exception When referencing a variable that does not exist
     */
    public static void validateDeclarations(EvaluatorTree evaluatorTree, boolean doAssignTypes, boolean debugMode) throws Exception {
        List<String> declaredVariablesNames = new ArrayList<>();
        List<String> variableTypes = new ArrayList<>();

        validateDeclarationsHelper(evaluatorTree, evaluatorTree.mainBlock, declaredVariablesNames, variableTypes, doAssignTypes, debugMode);
    }

    private static void validateDeclarationsHelper(EvaluatorTree evaluatorTree,  EvaluatorNode evaluatorNode, List<String> declaredVariablesNames, List<String> variableTypes, boolean doAssignTypes, boolean debugMode) throws Exception {

        if (debugMode)
                System.out.println("Node: " + evaluatorNode + "\nVariables: " + declaredVariablesNames + "\nTypes: " + variableTypes);

        for (int i = 0; i < evaluatorNode.memberCount(); i++) {
            EvaluatorNode member = evaluatorNode.getMember(i);

            // TODO not optimal implementation

            // types are hardcoded because type checking is not done in this function;
            // only declarations

            String alreadyDeclaredMessage = "Variable \"%s\" is already declared within scope";
            String undeclaredMessage = "Variable \"%s\" is undeclared";

            if (member instanceof FunctionArgNode functionArgNode) {
                String declaredVar = functionArgNode.getVariableName();
                if (declaredVariablesNames.contains(declaredVar)) {
                    member.throwSemanticError(String.format(alreadyDeclaredMessage, declaredVar), member.token);

//                    throw new Exception("Variable \"%s\" on line %s is already declared within scope, on file \"%s\"".formatted(functionArgNode.getVariableName(), member.token.line, evaluatorTree.name));
                }
                declaredVariablesNames.add(declaredVar);
                variableTypes.add(functionArgNode.getType());

            } else if (member instanceof DeclarationNode memberDeclaration) {
                String declaredVar = memberDeclaration.getVariableName();
                if (declaredVariablesNames.contains(declaredVar)) {
                    member.throwSemanticError(String.format(alreadyDeclaredMessage, declaredVar), member.token);

//                    throw new Exception("Variable \"%s\" on line %s is already declared within scope, file \"%s\"".formatted(memberDeclaration.getVariableName(), member.token.line, evaluatorTree.name));
                }
                declaredVariablesNames.add(declaredVar);
                variableTypes.add(memberDeclaration.getType());

            } else if (member instanceof AssignmentNode memberAssignment) {
                String assignedVar = memberAssignment.getVariableName();
                if (!declaredVariablesNames.contains(assignedVar)) {
                    member.throwSemanticError(String.format(undeclaredMessage, assignedVar), member.token);

//                    throw new Exception("Variable \"%s\" on line %s is undeclared on file \"%s\"".formatted(memberAssignment.getVariableName(), member.token.line, evaluatorTree.name));
                } else if (memberAssignment.getType().equals(KEY_DATA_UNKNOWN)) {

                    int varIndex = declaredVariablesNames.indexOf(assignedVar);
                    String type = variableTypes.get(varIndex);
                    if (doAssignTypes)
                        memberAssignment.setType(type);
                }
            } else if (member instanceof OperationNode memberOp && isVariableName(memberOp.constantToken)) {
                String assignedVar = memberOp.constantToken.string;
                if (!declaredVariablesNames.contains(assignedVar)) {
                    member.throwSemanticError(String.format(undeclaredMessage, assignedVar), member.token);

//                    throw new Exception("Variable \"%s\" on line %s is undeclared on file \"%s\"".formatted(memberOp.constantToken.string, member.token.line, evaluatorTree.name));
                } else if (memberOp.constantToken.getType().equals(KEY_DATA_UNKNOWN)) {
                    int varIndex = declaredVariablesNames.indexOf(assignedVar);
                    String type = variableTypes.get(varIndex);
                    if (doAssignTypes)
                        memberOp.constantToken.setType(type);
                }
            } else if (member instanceof FunctionCallNode functionDeclareMember) {
                String assignedVar = functionDeclareMember.token.string;
                if (!declaredVariablesNames.contains(assignedVar)) {
                    member.throwSemanticError(String.format(undeclaredMessage, assignedVar), member.token);

//                    throw new Exception("Function \"%s\" on line %s is undeclared on file \"%s\"".formatted(functionDeclareMember.token.string, member.token.line, evaluatorTree.name));
                }
            }
            validateDeclarationsHelper(evaluatorTree, member, new ArrayList<>(declaredVariablesNames), new ArrayList<>(variableTypes), doAssignTypes, debugMode);
        }
    }

    /**
     * Validates type consistency
     * @param evaluatorTree Abstract syntax tree
     */
    public static void validateTypes (EvaluatorTree evaluatorTree, boolean debugMode)  {
        validateTypesHelper(evaluatorTree.mainBlock, null, debugMode);
    }

    public static boolean canImplicitCast(String type, String type2) {
        return operationMap.isOperationValid(KEY_OP_CAST_IMPLICIT, type, type2);
    }

    private static String validateTypesHelper(EvaluatorNode evaluatorNode, EvaluatorNode parent, boolean debugMode) {
        String type = KEY_DATA_UNKNOWN;

        if (debugMode)
            System.out.println("Depth: " + evaluatorNode.depth  + " Node: " + evaluatorNode );

        // validate binary operations
        if (evaluatorNode instanceof OperationNode operationNode) {
            if (operationNode.isBinary()) {
                String leftType = KEY_DATA_UNKNOWN;
                String rightType = KEY_DATA_UNKNOWN;

                if (operationNode.getLeftSide() != null) {
                    leftType = validateTypesHelper(operationNode.getLeftSide(), operationNode, debugMode);
                }

                if (operationNode.getRightSide() != null) {
                    rightType = validateTypesHelper(operationNode.getRightSide(), operationNode, debugMode);
                }

                try {
                    type = operationMap.getCastTo(operationNode.getOperator(), leftType, rightType);

                } catch (IllegalArgumentException e) {
                    operationNode.throwSemanticError(e.getMessage(), operationNode.token);
                    return type;
                }

            } else if (operationNode.isUnary()) {

                // TODO check if the unary operator can apply
                type = validateTypesHelper(operationNode.getMember(0), evaluatorNode, debugMode);

            } else if (operationNode.isConstant()) {

                type = operationNode.constantToken.getType();
            }

            // TODO remove redundancies
        } else if (evaluatorNode instanceof AssignmentNode assignmentNode) {
            String compare = validateTypesHelper(evaluatorNode.getMember(0), evaluatorNode, debugMode);

            if (!assignmentNode.getType().equals(compare) && !KEY_DATA_LET.equals(assignmentNode.getType()) && !canImplicitCast(compare, assignmentNode.getType())) {
                assignmentNode.throwSemanticError(String.format("Cannot cast \"%s\" into \"%s\"", compare, assignmentNode.getType()), evaluatorNode.token);
                return type;

//                throw new Exception(String.format("Cannot cast \"%s\" into \"%s\" on line %s", compare, assignmentNode.getType(), evaluatorNode.token.line));
            }

        } else if (evaluatorNode instanceof DeclarationNode declarationNode) {
            EvaluatorNode innerMember = evaluatorNode.getMember(0);
            if (innerMember instanceof OperationNode op) {
                String compare = validateTypesHelper(innerMember, evaluatorNode, debugMode);

                if (!declarationNode.getType().equals(compare) && !KEY_DATA_LET.equals(declarationNode.getType()) && !canImplicitCast(compare, declarationNode.getType())) {
                    declarationNode.throwSemanticError(String.format("Cannot cast \"%s\" into \"%s\"", compare, declarationNode.getType()), evaluatorNode.token);
                    return type;

//                    throw new Exception(String.format("Cannot cast \"%s\" into \"%s\" on line %s", compare, declarationNode.getType(), evaluatorNode.token.line));
                }
            }
        } else {
            for (int i = 0; i < evaluatorNode.memberCount(); i++) {
                validateTypesHelper(evaluatorNode.getMember(i), evaluatorNode, debugMode);
            }
        }

        return type;
    }

    public static String getFunctionBlockReturnType(ScopeNode scopeNode) {
        // TODO implement
        return null;
    }
}
