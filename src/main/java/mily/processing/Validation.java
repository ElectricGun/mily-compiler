package mily.processing;

import mily.abstracts.*;
import mily.parsing.*;
import mily.parsing.callables.*;
import mily.parsing.invokes.*;
import mily.structures.dataobjects.*;
import mily.tokens.*;

import java.util.*;

import static mily.constants.Ansi.*;
import static mily.constants.Functions.*;
import static mily.constants.Keywords.*;
import static mily.constants.Maps.*;

/**
 * <h1> Class Validation </h1>
 * Contains utilities for semantic validation, such as type and scope checking.
 *
 * @author ElectricGun
 */

public class Validation {

    public static boolean checkThrowables(EvaluatorTree evaluatorTree) {
        boolean[] errored = new boolean[]{false};

        Stack<EvaluatorNode> traceStack = new Stack<>();
        checkThrowablesHelper(evaluatorTree.mainBlock, traceStack, errored);

        return errored[0];
    }

    public static void checkThrowablesHelper(EvaluatorNode evaluatorNode, Stack<EvaluatorNode> traceStack, boolean[] errored) {

        // TODO: not the most elegant solution

        traceStack.push(evaluatorNode);

        if (evaluatorNode.isErrored()) {
            errored[0] = true;

            boolean isMultipleErrors = evaluatorNode.throwablesCount() > 1;

            System.out.print(ANSI_ERROR);
            if (isMultipleErrors) {
                System.out.printf("Multiple errors on file \"%s\" line %s, token: \"%s\":%n", evaluatorNode.nameToken.source, evaluatorNode.nameToken.line, evaluatorNode.nameToken);
            }
            for (int i = 0; i < evaluatorNode.throwablesCount(); i++) {
                System.out.println((isMultipleErrors ? "\t" : "") + evaluatorNode.getThrowable(i).getErrorMessage());
            }

            Stack<EvaluatorNode> newStack = new Stack<>();
            newStack.addAll(traceStack);

            // > 1 to avoid unnecessarily printing the __MAIN__ node
            while (newStack.size() > 1) {
                EvaluatorNode trace = newStack.pop();
                System.out.printf((isMultipleErrors ? "\t" : "") + "    under %s in file: \"%s\", line: %s%n", trace.errorName(), trace.nameToken.source, trace.nameToken.line);
            }
            System.out.print(ANSI_RESET);
        }

        for (int i = 0; i < evaluatorNode.memberCount(); i++) {
            Stack<EvaluatorNode> newStack = new Stack<>();
            newStack.addAll(traceStack);

            checkThrowablesHelper(evaluatorNode.getMember(i), newStack, errored);
        }
    }

    /**
     * Validates variable declarations and assigns types to variable references. <br>
     * VARIABLE REFERENCE TYPES ARE ASSIGNED HERE
     *
     * @param evaluatorTree Abstract syntax tree
     */
    public static void validateDeclarations(EvaluatorTree evaluatorTree, boolean doAssignTypes, boolean debugMode) {
        List<String> declaredVariablesNames = new ArrayList<>();
        List<Type> variableTypes = new ArrayList<>();

        validateDeclarationsHelper(evaluatorTree.mainBlock, declaredVariablesNames, variableTypes, doAssignTypes, debugMode);
    }

    private static void validateDeclarationsHelper(EvaluatorNode evaluatorNode, List<String> declaredVariablesNames, List<Type> variableTypes, boolean doAssignTypes, boolean debugMode) {

        if (debugMode)
            System.out.println("Node: " + evaluatorNode + "\nVariables: " + declaredVariablesNames + "\nTypes: " + variableTypes);

        String alreadyDeclaredMessage = "Variable \"%s\" is already declared within scope";
        String undeclaredMessage = "Cannot find variable \"%s\" within scope";

        for (int i = 0; i < evaluatorNode.memberCount(); i++) {
            EvaluatorNode member = evaluatorNode.getMember(i);

            // TODO not optimal implementation

            if (member instanceof FunctionArgNode functionArgNode) {
                String declaredVar = functionArgNode.getName();
                if (declaredVariablesNames.contains(declaredVar)) {
                    member.throwSemanticError(String.format(alreadyDeclaredMessage, declaredVar), member.nameToken);
                }
                declaredVariablesNames.add(declaredVar);
                variableTypes.add(functionArgNode.getType());

            } else if (member instanceof DeclarationNode memberDeclaration) {
                String declaredVar = memberDeclaration.getName();
                if (declaredVariablesNames.contains(declaredVar)) {
                    member.throwSemanticError(String.format(alreadyDeclaredMessage, declaredVar), member.nameToken);
                }
                declaredVariablesNames.add(declaredVar);
                variableTypes.add(memberDeclaration.getType());

            } else if (member instanceof AssignmentNode memberAssignment) {
                String assignedVar = memberAssignment.getName();
                if (!declaredVariablesNames.contains(assignedVar)) {
                    member.throwSemanticError(String.format(undeclaredMessage, assignedVar), member.nameToken);

                } else if (memberAssignment.getType().equals(KEY_DATA_UNKNOWN)) {
                    int varIndex = declaredVariablesNames.indexOf(assignedVar);
                    Type type = variableTypes.get(varIndex);
                    if (doAssignTypes)
                        memberAssignment.setType(type);
                }
            } else if (member instanceof OperationNode memberOp &&
                    memberOp.getConstantToken() != null &&
                    memberOp.getConstantToken().getType().equals(KEY_DATA_UNKNOWN) &&
                    isVariableName(memberOp.getConstantToken())) {

                String assignedVar = memberOp.getConstantToken().string;
                // THE SAME #1
                if (memberOp.getConstantToken() instanceof CallerNodeToken callerNodeToken) {
                    CallerNode callerNode = callerNodeToken.getNode();
                    validateDeclarationsHelper(callerNode, declaredVariablesNames, variableTypes, doAssignTypes, debugMode);

                } else if (!declaredVariablesNames.contains(assignedVar)) {
                    member.throwSemanticError(String.format(undeclaredMessage, assignedVar), member.nameToken);

                } else if (memberOp.getConstantToken().getType().equals(KEY_DATA_UNKNOWN)) {
                    int varIndex = declaredVariablesNames.indexOf(assignedVar);
                    Type type = variableTypes.get(varIndex);
                    if (doAssignTypes) {
                        memberOp.getConstantToken().setType(type);
                    }
                    // THE SAME #2
                    if (memberOp.getConstantToken() instanceof CallerNodeToken callerNodeToken) {
                        CallerNode callerNode = callerNodeToken.getNode();
                        validateDeclarationsHelper(callerNode, declaredVariablesNames, variableTypes, doAssignTypes, debugMode);
                    }
                }
            }
            List<String> newDeclares = new ArrayList<>(declaredVariablesNames);
            List<Type> newTypes = new ArrayList<>(variableTypes);

            if (member instanceof DeclarationNode dec && dec.memberCount() > 0) {
                newDeclares.remove(newDeclares.size() - 1);
                newTypes.remove(newTypes.size() - 1);
            }
            validateDeclarationsHelper(member, newDeclares, newTypes, doAssignTypes, debugMode);
        }
    }

    /**
     * Validates type consistency <br>
     * Requires all operations within the AST to be binary
     *
     * @param evaluatorTree Abstract syntax tree
     */
    public static void validateTypes(EvaluatorTree evaluatorTree, boolean debugMode) {
        validateTypesHelper(evaluatorTree.mainBlock, true, debugMode);
    }

    public static boolean cannotImplicitCast(Type type, Type type2) {
        return !operationMap.isOperationValid(KEY_OP_CAST_IMPLICIT, type, type2);
    }

    public static Type getOperationType(OperationNode operationNode, boolean debugMode) {
        return validateTypesHelper(operationNode, false, debugMode);
    }

    private static Type validateTypesHelper(EvaluatorNode evaluatorNode, boolean throwErrors, boolean debugMode) {
        Type type = KEY_DATA_UNKNOWN.create();

        if (debugMode)
            System.out.println("Depth: " + evaluatorNode.depth + " Node: " + evaluatorNode);

        // validate binary operations
        if (evaluatorNode instanceof OperationNode operationNode) {
            if (operationNode.isBinary()) {
                Type leftType = KEY_DATA_UNKNOWN.create();
                Type rightType = KEY_DATA_UNKNOWN.create();

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
                        operationNode.throwTypeError(e.getMessage(), operationNode.nameToken);
                    if (debugMode)
                        System.out.println("ERROR " + type + "  " + evaluatorNode);
                    return type;
                }

            } else if (operationNode.isConstant()) {
                type = operationNode.getConstantToken().getType();
                type = type == null ? KEY_DATA_UNKNOWN.create() : type;
            }

            // TODO remove redundancies
        } else if (evaluatorNode instanceof AssignmentNode assignmentNode) {
            if (debugMode)
                System.out.println("Assignment found");
            Type compare = validateTypesHelper(evaluatorNode.getMember(0), false, debugMode);

            if (!assignmentNode.getType().equals(compare) && cannotImplicitCast(compare, assignmentNode.getType())) {
                if (throwErrors)
                    assignmentNode.throwTypeError(String.format("Cannot cast \"%s\" into \"%s\"", compare, assignmentNode.getType()), evaluatorNode.nameToken);
                if (debugMode)
                    System.out.println(type + "  " + evaluatorNode);
                return type;
            }

        } else if (evaluatorNode instanceof DeclarationNode declarationNode && !(declarationNode instanceof FunctionArgNode)) {

            if (declarationNode.memberCount() == 0) {
                System.out.println("Null declaration found");
            }
            if (debugMode)
                System.out.println("Declaration found");

            if (evaluatorNode.getMember(0) instanceof OperationNode innerMember) {
                Type compare = validateTypesHelper(innerMember, throwErrors, debugMode);

                if (!declarationNode.getType().equals(compare) && cannotImplicitCast(compare, declarationNode.getType())) {
                    if (throwErrors)
                        declarationNode.throwTypeError(String.format("Cannot cast \"%s\" into \"%s\"", compare, declarationNode.getType()), evaluatorNode.nameToken);
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

    /**
     * Checks if a function's return type is consistent with its returns
     *
     * @see FunctionDeclareNode
     */
    public static void validateFunctionDeclares(EvaluatorTree evaluatorTree, boolean debugMode) throws Exception {
        validateFunctionDeclaresHelper(evaluatorTree.mainBlock, new ArrayList<>(), debugMode);
    }

    private static void validateFunctionDeclaresHelper(EvaluatorNode evaluatorNode, List<CallableNode> functionDeclares, boolean debugMode) throws Exception {
        if (evaluatorNode instanceof CallableNode callable) {
            for (CallableNode f : functionDeclares) {
                if (callable.isOverload(f.getName(), f.getArgumentTypesArr())) {
                    callable.throwSemanticError(String.format("Redeclaration of function %s with argument types %s and types %s", callable.getName(), callable.getArgumentTypes(), f.getArgumentTypes()), callable.nameToken);
                }
            }
            if (callable instanceof FunctionDeclareNode functionDeclareNode) {
                validateFunctionBlockReturnType(functionDeclareNode, debugMode);
            }

            functionDeclares.add(callable);
        }

        for (int i = 0; i < evaluatorNode.memberCount(); i++) {
            validateFunctionDeclaresHelper(evaluatorNode.getMember(i), functionDeclares, debugMode);
        }
    }

    private static void validateFunctionBlockReturnType(FunctionDeclareNode func, boolean debugMode) throws Exception {
        Type returnType = func.getType();
        EvaluatorNode scope = func.getScope();

        boolean isReturningSomething = validateReturns(scope, returnType, debugMode);

        // todo can be simplified
        if (!isReturningSomething && !KEY_DATA_VOID.equals(returnType)) {
            func.throwSemanticError("Not all paths return a value", func.nameToken);
        }
    }

    private static boolean validateReturns(EvaluatorNode evaluatorNode, Type returnType, boolean debugMode) throws Exception {
        // get the returns on the first layer
        for (int i = 0; i < evaluatorNode.memberCount(); i++) {
            EvaluatorNode member = evaluatorNode.getMember(i);
            if (member instanceof OperationNode op && op.isReturnOperation()) {

                Type opType = validateTypesHelper(op, false, debugMode);
                if (!returnType.equals(opType))
                    op.throwTypeError("Invalid return type " + opType + ", expected " + returnType, op.nameToken);

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

    private static void validateBranchReturns(IfStatementNode ifStatementNode, Type returnType, List<Boolean> returnPaths, boolean[] hasElse, boolean debugMode) throws Exception {
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

    public static void validateCallers(EvaluatorTree evaluatorTree, boolean doAssignTypes, boolean debugMode) {
        validateCallersHelper(evaluatorTree.mainBlock, new ArrayList<>(), doAssignTypes, debugMode);
    }

    private static void validateCallersHelper(EvaluatorNode evaluatorNode, List<Callable> callables, boolean doAssignTypes, boolean debugMode) {
        for (int i = 0; i < evaluatorNode.memberCount(); i++) {
            EvaluatorNode member = evaluatorNode.getMember(i);

            if (member instanceof OperationNode operationNode) {
                validateCallersHelper(operationNode, new ArrayList<>(callables), doAssignTypes, debugMode);

                if (operationNode.getConstantToken() instanceof CallerNodeToken callerNodeToken) {
                    Caller subCaller = callerNodeToken.getNode();
                    if (subCaller instanceof CallerNode callerNode) {
                        if (!validateCaller(subCaller, callables, doAssignTypes, debugMode)) {
                            member.throwSemanticError(String.format("No overload for caller \"%s\" with arguments of types %s", subCaller.getName(), Arrays.toString(getCallTypes(subCaller, debugMode))), callerNode.nameToken);
                        }

                        for (int a = 0; a < subCaller.getArgCount(); a++) {
                            OperationNode arg = subCaller.getArg(a);
                            validateCallersHelper(arg, new ArrayList<>(callables), doAssignTypes, debugMode);
                        }
                    }
                }
            } else if (member instanceof Callable functionDeclareNode) {
                callables.add(functionDeclareNode);

            } else if (member instanceof Caller caller) {
                if (!validateCaller(caller, callables, doAssignTypes, debugMode)) {
                    member.throwSemanticError(String.format("No overload for caller \"%s\" with arguments of types %s", caller.getName(), Arrays.toString(getCallTypes(caller, debugMode))), member.nameToken);
                }
            }
            validateCallersHelper(member, new ArrayList<>(callables), doAssignTypes, debugMode);
        }
    }

    private static Type[] getCallTypes(Caller caller, boolean debugMode) {
        Type[] callTypes = new Type[caller.getArgCount()];
        for (int a = 0; a < callTypes.length; a++) {
            callTypes[a] = validateTypesHelper(caller.getArg(a), false, debugMode);
        }
        return callTypes;
    }

    private static void validateCallerOperation(OperationNode operationNode, List<Callable> functionDeclares, boolean doAssignTypes, boolean debugMode) {
        if (operationNode.getConstantToken() instanceof CallerNodeToken fn) {
            validateCaller(fn.getNode(), functionDeclares, doAssignTypes, debugMode);
        }

        if (operationNode.getLeftSide() != null) {
            validateCallerOperation(operationNode.getLeftSide(), functionDeclares, doAssignTypes, debugMode);
        }

        if (operationNode.getRightSide() != null) {
            validateCallerOperation(operationNode.getRightSide(), functionDeclares, doAssignTypes, debugMode);
        }

        if (operationNode.isUnary() && operationNode.getMember(0) instanceof OperationNode op) {
            validateCallerOperation(op, functionDeclares, doAssignTypes, debugMode);
        }
    }

    private static boolean validateCaller(Caller caller, List<Callable> functionDeclares, boolean doAssignTypes, boolean debugMode) {
        boolean valid = false;
        // recursively set the types of function calls for nested calls
        for (int a = 0; a < caller.getArgCount(); a++) {
            OperationNode arg = caller.getArg(a);

            validateCallerOperation(arg, functionDeclares, doAssignTypes, debugMode);
        }
        Type[] callTypes = getCallTypes(caller, debugMode);

        for (Callable fn : functionDeclares) {
            if (fn.isOverload(caller.getName(), callTypes)) {
                valid = true;
                if (doAssignTypes)
                    caller.setType(fn.getType());
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
            Type type = validateTypesHelper(expression, false, debugMode);

            if (!KEY_DATA_BOOLEAN.equals(type)) {
                evaluatorNode.throwSemanticError("Expression in conditional must result in a value of type boolean, is instead of type " + type, evaluatorNode.nameToken);
            }
        }

        for (int i = 0; i < evaluatorNode.memberCount(); i++) {
            validateConditionalsHelper(evaluatorNode.getMember(i), debugMode);
        }
    }

    public static void invalidateDynamicDatatype(EvaluatorTree evaluatorTree, boolean debugMode) {
        invalidateDynamicDatatypeHelper(evaluatorTree.mainBlock, debugMode);
    }

    private static void invalidateDynamicDatatypeHelper(EvaluatorNode evaluatorNode, boolean debugMode) {
        for (int i = 0; i < evaluatorNode.memberCount(); i++) {
            EvaluatorNode member = evaluatorNode.getMember(i);

            if (member instanceof DeclarationNode declarationNode && declarationNode.getType().equals(KEY_DATA_ANY)) {
                declarationNode.throwSemanticError(String.format("Cannot use datatype \"%s\" in variable declarations", KEY_DATA_ANY), declarationNode.nameToken);

            } else if (member instanceof CallableNode callableNode) {
                if (callableNode instanceof FunctionDeclareNode functionDeclareNode) {
                    if (functionDeclareNode.getArgumentTypes().contains(KEY_DATA_ANY)) {
                        functionDeclareNode.throwSemanticError(String.format("Cannot use datatype type \"%s\" in function arguments", KEY_DATA_ANY), functionDeclareNode.nameToken);
                    }
                    invalidateDynamicDatatypeHelper(functionDeclareNode.getScope(), debugMode);
                }
                if (callableNode.getType().equals(KEY_DATA_ANY)) {
                    callableNode.throwSemanticError(String.format("Callable cannot return datatype \"%s\"", KEY_DATA_ANY), callableNode.nameToken);
                }

            } else {
                invalidateDynamicDatatypeHelper(member, debugMode);
            }
        }
    }
}
