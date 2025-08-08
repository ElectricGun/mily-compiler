package src.processing;

import java.util.*;

import src.parsing.*;
import src.tokens.FunctionCallToken;

import static src.constants.Functions.*;
import static src.constants.Keywords.*;
import static src.constants.Maps.*;
import static src.constants.Ansi.*;

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

            boolean isMultipleErrors = evaluatorNode.throwablesCount() > 1;

            System.out.print(ANSI_RED);
            if (isMultipleErrors) {
                System.out.println(String.format("Multiple errors on line %s, token: \"%s\":", evaluatorNode.nameToken.line, evaluatorNode.nameToken));
            }
            for (int i = 0; i < evaluatorNode.throwablesCount(); i++) {
                System.out.println((isMultipleErrors ? "\t" : "") + evaluatorNode.getThrowable(i).getErrorMessage());
            }

            Stack<EvaluatorNode> newStack = new Stack<>();
            newStack.addAll(traceStack);

            while (!newStack.isEmpty()) {
                EvaluatorNode trace = newStack.pop();
                System.out.printf((isMultipleErrors ? "\t" : "") + "\tat %s, line %s%n", trace, trace.nameToken.line);
            }
            System.out.print(ANSI_RESET);
        }

        for (int i = 0; i < evaluatorNode.memberCount(); i ++) {
            Stack<EvaluatorNode> newStack = new Stack<>();
            newStack.addAll(traceStack);

            checkThrowablesHelper(evaluatorNode.getMember(i), newStack, errored, debugMode);
        }
    }

    /**
     * Validates variable declarations and assigns types to variable references. <br>
     * VARIABLE REFERENCE TYPES ARE ASSIGNED HERE
     * @param evaluatorTree Abstract syntax tree
     * @throws Exception When declaring a variable that is already declared
     * @throws Exception When referencing a variable that does not exist
     */
    public static void validateDeclarations(EvaluatorTree evaluatorTree, boolean doAssignTypes, boolean debugMode) throws Exception {
        List<String> declaredVariablesNames = new ArrayList<>();
        List<String> variableTypes = new ArrayList<>();

        validateDeclarationsHelper(evaluatorTree.mainBlock, declaredVariablesNames, variableTypes, doAssignTypes, debugMode);
    }

    private static void validateDeclarationsHelper(EvaluatorNode evaluatorNode, List<String> declaredVariablesNames, List<String> variableTypes, boolean doAssignTypes, boolean debugMode) throws Exception {

        if (debugMode)
                System.out.println("Node: " + evaluatorNode + "\nVariables: " + declaredVariablesNames + "\nTypes: " + variableTypes);

        for (int i = 0; i < evaluatorNode.memberCount(); i++) {
            EvaluatorNode member = evaluatorNode.getMember(i);

            // TODO not optimal implementation
            String alreadyDeclaredMessage = "Variable \"%s\" is already declared within scope";
            String undeclaredMessage = "Cannot find variable \"%s\" within scope";

            if (member instanceof FunctionArgNode functionArgNode) {
                String declaredVar = functionArgNode.getVariableName();
                if (declaredVariablesNames.contains(declaredVar)) {
                    member.throwSemanticError(String.format(alreadyDeclaredMessage, declaredVar), member.nameToken);
                }
                declaredVariablesNames.add(declaredVar);
                variableTypes.add(functionArgNode.getType());

            } else if (member instanceof DeclarationNode memberDeclaration) {
                String declaredVar = memberDeclaration.getVariableName();
                if (declaredVariablesNames.contains(declaredVar)) {
                    member.throwSemanticError(String.format(alreadyDeclaredMessage, declaredVar), member.nameToken);
                }
                declaredVariablesNames.add(declaredVar);
                variableTypes.add(memberDeclaration.getType());

            } else if (member instanceof AssignmentNode memberAssignment) {
                String assignedVar = memberAssignment.getVariableName();
                if (!declaredVariablesNames.contains(assignedVar)) {
                    member.throwSemanticError(String.format(undeclaredMessage, assignedVar), member.nameToken);

                } else if (memberAssignment.getType().equals(KEY_DATA_UNKNOWN)) {
                    int varIndex = declaredVariablesNames.indexOf(assignedVar);
                    String type = variableTypes.get(varIndex);
                    if (doAssignTypes)
                        memberAssignment.setType(type);
                }
            } else if (member instanceof OperationNode memberOp && isVariableName(memberOp.constantToken)) {
                String assignedVar = memberOp.constantToken.string;
                // THE SAME #1
                if (memberOp.constantToken instanceof FunctionCallToken functionCallToken) {
                    FunctionCallNode functionCallNode = functionCallToken.getNode();
                    validateDeclarationsHelper(functionCallNode, declaredVariablesNames, variableTypes, doAssignTypes, debugMode);

                } else if (!declaredVariablesNames.contains(assignedVar)) {
                    member.throwSemanticError(String.format(undeclaredMessage, assignedVar), member.nameToken);

                } else if (memberOp.constantToken.getType().equals(KEY_DATA_UNKNOWN)) {
                    int varIndex = declaredVariablesNames.indexOf(assignedVar);
                    String type = variableTypes.get(varIndex);
                    if (doAssignTypes) {
                        memberOp.constantToken.setType(type);
                    }
                    // THE SAME #2
                    if (memberOp.constantToken instanceof FunctionCallToken functionCallToken) {
                        FunctionCallNode functionCallNode = functionCallToken.getNode();
                        validateDeclarationsHelper(functionCallNode, declaredVariablesNames, variableTypes, doAssignTypes, debugMode);
                    }
                }
            }
            // moved to validateFunctionsCalls
//            else if (member instanceof FunctionCallNode functionDeclareMember) {
//                String assignedVar = functionDeclareMember.nameToken.string;
//                if (!declaredVariablesNames.contains(assignedVar)) {
//                    member.throwSemanticError(String.format(undeclaredMessage, assignedVar), member.nameToken);
//                }
//            }
            List<String> newDeclares = new ArrayList<>(declaredVariablesNames);
            List<String> newTypes = new ArrayList<>(variableTypes);

            if (member instanceof DeclarationNode dec && dec.memberCount() > 0) {
                newDeclares.removeLast();
                newTypes.removeLast();
            }
            validateDeclarationsHelper(member, newDeclares, newTypes, doAssignTypes, debugMode);
        }
    }

    /**
     * Validates type consistency <br>
     * Requires all operations within the AST to be binary
     * @param evaluatorTree Abstract syntax tree
     */
    public static void validateTypes (EvaluatorTree evaluatorTree, boolean debugMode)  {
        validateTypesHelper(evaluatorTree.mainBlock, true, debugMode);
    }

    public static boolean canImplicitCast(String type, String type2) {
        return operationMap.isOperationValid(KEY_OP_CAST_IMPLICIT, type, type2);
    }

    public static String getOperationType(OperationNode operationNode, boolean debugMode) {
        return validateTypesHelper(operationNode, false, debugMode);
    }

    private static String validateTypesHelper(EvaluatorNode evaluatorNode, boolean throwErrors, boolean debugMode) {
        String type = KEY_DATA_UNKNOWN;

        if (debugMode)
            System.out.println("Depth: " + evaluatorNode.depth  + " Node: " + evaluatorNode );

        // validate binary operations
        if (evaluatorNode instanceof OperationNode operationNode) {
            if (operationNode.isBinary()) {
                String leftType = KEY_DATA_UNKNOWN;
                String rightType = KEY_DATA_UNKNOWN;

                if (operationNode.getLeftSide() != null) {
                    leftType = validateTypesHelper(operationNode.getLeftSide(), throwErrors, debugMode);
                }
                if (operationNode.getRightSide() != null) {
                    rightType = validateTypesHelper(operationNode.getRightSide(), throwErrors, debugMode);
                }

                try {
                    type = operationMap.getCastTo(operationNode.getOperator(), leftType, rightType);

                } catch (IllegalArgumentException e) {
                    if (throwErrors)
                        operationNode.throwSemanticError(e.getMessage(), operationNode.nameToken);
                    if (debugMode)
                        System.out.println("ERROR " + type + "  " + evaluatorNode);
                    return type;
                }

            }
//            else if (operationNode.isUnary()) {
//
//                type = validateTypesHelper(operationNode.getMember(0), evaluatorNode, debugMode);
//
//            }
            else if (operationNode.isConstant()) {
                type = operationNode.constantToken.getType();

                type = type == null ? KEY_DATA_UNKNOWN : type;
            }

            // TODO remove redundancies
        } else if (evaluatorNode instanceof AssignmentNode assignmentNode) {
            if (debugMode)
                System.out.println("Assignment found");
            String compare = validateTypesHelper(evaluatorNode.getMember(0), false, debugMode);

            if (!assignmentNode.getType().equals(compare) && !canImplicitCast(compare, assignmentNode.getType())) {
                if (throwErrors)
                    assignmentNode.throwSemanticError(String.format("Cannot cast \"%s\" into \"%s\"", compare, assignmentNode.getType()), evaluatorNode.nameToken);
                if (debugMode)
                    System.out.println(type + "  " + evaluatorNode);
                return type;
            }

        } else if (evaluatorNode instanceof DeclarationNode declarationNode) {

            if (declarationNode.memberCount() == 0) {
                System.out.println("Null declaration found");
            }
            if (debugMode)
                System.out.println("Declaration found");

            if (evaluatorNode.getMember(0) instanceof OperationNode innerMember) {
                String compare = validateTypesHelper(innerMember, throwErrors, debugMode);

                if (!declarationNode.getType().equals(compare) && !canImplicitCast(compare, declarationNode.getType())) {
                    if (throwErrors)
                        declarationNode.throwSemanticError(String.format("Cannot cast \"%s\" into \"%s\"", compare, declarationNode.getType()), evaluatorNode.nameToken);
                    if (debugMode)
                        System.out.println(type + "  " + evaluatorNode);
                    return type;
                }
            }
        } else if (evaluatorNode instanceof FunctionDeclareNode declareNode) {
            for (int i = 0; i < declareNode.getScope().memberCount(); i++) {
                validateTypesHelper(declareNode.getScope().getMember(i), throwErrors, debugMode);
            }
        } else {
            for (int i = 0; i < evaluatorNode.memberCount(); i++) {
                validateTypesHelper(evaluatorNode.getMember(i), throwErrors, debugMode);
            }
        }

        if (debugMode)
            System.out.println(type + "  " + evaluatorNode);

        return type;
    }

    // TODO: why are there so many methods doing just one thing. Fix this later
    /**
     * Checks if a function's return type is consistent with its returns
     * @see FunctionDeclareNode
     */
    public static void validateFunctionDeclares(EvaluatorTree evaluatorTree, boolean debugMode) throws Exception {
        validateFunctionDeclaresHelper(evaluatorTree.mainBlock, new ArrayList<>(), debugMode);
    }

    private static void validateFunctionDeclaresHelper(EvaluatorNode evaluatorNode, List<FunctionDeclareNode> functionDeclares, boolean debugMode) throws Exception {
        if (evaluatorNode instanceof FunctionDeclareNode func) {
            for (FunctionDeclareNode f : functionDeclares) {
                if (func.isOverload(f.getName(), f.getArgumentTypesArr())) {
                    func.throwSemanticError(String.format("Redeclaration of function %s with argument types %s", func.getName(), func.getArgumentTypes()), func.nameToken);
                }
            }
            validateFunctionBlockReturnType(func, debugMode);
            functionDeclares.add(func);
        }

        for (int i = 0; i < evaluatorNode.memberCount(); i ++) {
            validateFunctionDeclaresHelper(evaluatorNode.getMember(i), functionDeclares, debugMode);
        }
    }

    private static void validateFunctionBlockReturnType(FunctionDeclareNode func, boolean debugMode) throws Exception {
        String returnType = func.getType();
        EvaluatorNode scope = func.getScope();

        boolean isReturningSomething = validateReturns(scope, returnType, debugMode);

        // todo can be simplified
        if (!isReturningSomething && !keyEquals(KEY_DATA_VOID, returnType)) {
            func.throwSemanticError("Not all paths return a value", func.nameToken);
        }
    }

    private static boolean validateReturns(EvaluatorNode evaluatorNode, String returnType, boolean debugMode) throws Exception {
        // get the returns on the first layer
        for (int i = 0; i < evaluatorNode.memberCount(); i ++) {
            EvaluatorNode member = evaluatorNode.getMember(i);
            if (member instanceof OperationNode op && op.isReturnOperation()) {
                String opType = validateTypesHelper(op, false, debugMode);

                if (!returnType.equals(opType))
                    op.throwSemanticError("Invalid return type " + opType, op.nameToken);

                if (debugMode)
                    System.out.println("Return found on " + evaluatorNode);

                return true;
            }

            if (member instanceof IfStatementNode ifStatementNode) {
                List<Boolean> returnPaths = new ArrayList<>();
                boolean[] hasElse = {false};
                validateBranchReturns(ifStatementNode, returnType, returnPaths, hasElse, debugMode);

                if (hasElse[0] && !returnPaths.contains(false)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void validateBranchReturns(IfStatementNode ifStatementNode, String returnType, List<Boolean> returnPaths, boolean[] hasElse, boolean debugMode) throws Exception {
        ScopeNode ifBlock = ifStatementNode.getScope();

        if (ifStatementNode.getElseNode() != null) {
            ElseNode elseNode = ifStatementNode.getElseNode();

            if (elseNode.getIfStatementNode() != null) {
                if (debugMode)
                    System.out.println("Else if found");

                IfStatementNode elseIfNode = elseNode.getIfStatementNode();
                validateBranchReturns(elseIfNode, returnType, returnPaths, hasElse, debugMode);

            } else if (elseNode.getScope() != null) {
                if (debugMode)
                    System.out.println("Else block found");

                ScopeNode elseScope = elseNode.getScope();
                returnPaths.add(validateReturns(elseScope, returnType, debugMode));
                hasElse[0] = true;

            } else {
                throw new Exception("Scope not found in else block");
            }
        }
        returnPaths.add(validateReturns(ifBlock, returnType, debugMode));
    }

    public static void validateFunctionsCalls(EvaluatorTree evaluatorTree, boolean doAssignTypes, boolean debugMode) {
        validateFunctionsCallsHelper(evaluatorTree.mainBlock, new ArrayList<>(), doAssignTypes, debugMode);
    }

    private static void validateFunctionsCallsHelper(EvaluatorNode evaluatorNode, List<FunctionDeclareNode> functionDeclares, boolean doAssignTypes, boolean debugMode) {
        for (int i = 0; i < evaluatorNode.memberCount(); i++) {
            EvaluatorNode member = evaluatorNode.getMember(i);

            if (member instanceof OperationNode operationNode) {
                if (operationNode.constantToken instanceof FunctionCallToken functionCallToken) {
                    String[] callTypes = getCallTypes(functionCallToken.getNode(), debugMode);

                    if (!isFunctionCallValid(functionCallToken.getNode(), functionDeclares, callTypes, doAssignTypes, debugMode)) {
                        member.throwSemanticError(String.format("No overload for function %s with arguments of types %s", functionCallToken.getNode().getName(), Arrays.toString(callTypes)), member.nameToken);
                    }
                }
            } else if (member instanceof FunctionDeclareNode functionDeclareNode) {
                    functionDeclares.add(functionDeclareNode);

            } else if (member instanceof FunctionCallNode functionCallNode) {
                String[] callTypes = getCallTypes(functionCallNode, debugMode);

                if (!isFunctionCallValid(functionCallNode, functionDeclares, callTypes, doAssignTypes, debugMode)) {
                    functionCallNode.throwSemanticError(String.format("No overload for function %s with arguments of types %s", functionCallNode.getName(), Arrays.toString(callTypes)), functionCallNode.nameToken);
                }
            }
            validateFunctionsCallsHelper(member, new ArrayList<>(functionDeclares), doAssignTypes, debugMode);
        }
    }

    private static String[] getCallTypes(FunctionCallNode functionCallNode, boolean debugMode) {
        String[] callTypes = new String[functionCallNode.getArgCount()];
        for (int a = 0; a < callTypes.length; a++) {
//            functionCallNode.getArg(a).printRecursive();
//            System.out.println(getOperationType(functionCallNode.getArg(a), debugMode));
            callTypes[a] = validateTypesHelper(functionCallNode.getArg(a), false, debugMode);
        }

        return callTypes;
    }

    private static boolean isFunctionCallValid(FunctionCallNode functionCallNode, List<FunctionDeclareNode> functionDeclares, String[] callTypes, boolean doAssignTypes, boolean debugMode) {
        boolean valid = false;

        for (FunctionDeclareNode fn : functionDeclares) {
            if (fn.isOverload(functionCallNode.getName(), callTypes)) {
                valid = true;
                if (doAssignTypes)
                    functionCallNode.setType(fn.getType());
            }
        }

        return valid;
    }

    public static void validateConditionals(EvaluatorTree evaluatorTree, boolean debugMode) {
        validateConditionalsHelper(evaluatorTree.mainBlock, debugMode);
    }

    private static void validateConditionalsHelper(EvaluatorNode evaluatorNode, boolean debugMode) {
        if (evaluatorNode instanceof ConditionalNode conditionalNode) {
            OperationNode expression = conditionalNode.getExpression();
            String type = validateTypesHelper(expression, false, debugMode);

            if (!keyEquals(KEY_DATA_BOOLEAN, type)) {
                evaluatorNode.throwSemanticError("Expression in conditional must result in a value of type boolean, is instead of type " + type, evaluatorNode.nameToken);
            }
        }

        for (int i = 0; i < evaluatorNode.memberCount(); i++) {
            validateConditionalsHelper(evaluatorNode.getMember(i), debugMode);
        }
    }
}
