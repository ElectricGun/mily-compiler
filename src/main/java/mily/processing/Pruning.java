package mily.processing;

import mily.abstracts.*;
import mily.parsing.*;
import mily.parsing.invokes.*;
import mily.tokens.*;

import static mily.constants.Functions.*;
import static mily.constants.Maps.*;

/**
 * <h1> Class Pruning </h1>
 * Static functions for pruning and simplifying the {@link EvaluatorTree}
 *
 * @author ElectricGun
 */

public class Pruning {

    public static void removeEmptyOperations(EvaluatorTree evaluatorTree) {
        removeEmptyOperationsHelper(evaluatorTree.mainBlock, null);
    }

    private static void removeEmptyOperationsHelper(EvaluatorNode evaluatorNode, EvaluatorNode parent) {
        if (evaluatorNode == null)
            return;

        if (evaluatorNode instanceof OperationNode operationNode) {
            // truncate children of self if they are empty
            truncateEmptyOperationChildrenRecursive(operationNode);

            // truncate self if empty
            if (operationNode.isEmptyConstant() && parent != null) {
                OperationNode replacer = operationNode.getLeftSide();
                replacer.setReturnOperation(operationNode.isReturnOperation());
                parent.replaceMember(operationNode, replacer);
            }

            // truncate from function arguments within operators
            if (operationNode.getConstantToken() instanceof CallerNodeToken callerToken) {
                CallerNode caller = callerToken.getNode();
                truncateFunctionArgs(caller);
            }
        }
        // truncate from operatorless function arguments
        if (evaluatorNode instanceof Caller caller) {
            truncateFunctionArgs(caller);
        }

        for (int i = 0; i < evaluatorNode.memberCount(); i++) {
            removeEmptyOperationsHelper(evaluatorNode.getMember(i), evaluatorNode);
        }
    }

    private static void truncateFunctionArgs(Caller caller) {
        for (int a = 0; a < caller.getArgCount(); a++) {
            OperationNode arg = caller.getArg(a);

            while (arg.isEmptyConstant() && arg.memberCount() > 0) {
                OperationNode childArg = (OperationNode) arg.getMember(0);
                caller.setArg(a, childArg);

                arg = childArg;
            }

            if (arg.getConstantToken() instanceof CallerNodeToken callerNodeToken) {
                truncateFunctionArgs(callerNodeToken.getNode());
            }
        }
    }

    private static void truncateEmptyOperationChildrenRecursive(OperationNode operationNode) {
        if (operationNode.getLeftSide() != null && operationNode.getLeftSide().isEmptyConstant()) {
            while (operationNode.getLeftSide().isEmptyConstant()) {
                operationNode.setLeftSide(operationNode.getLeftSide().getLeftSide());
            }
        }

        if (operationNode.getRightSide() != null && operationNode.getRightSide().isEmptyConstant()) {
            while (operationNode.getRightSide().isEmptyConstant())
                operationNode.setRightSide(operationNode.getRightSide().getLeftSide());
        }
    }

    // TODO: for future use
//    public static void pruneNestedUnaries(EvaluatorTree evaluatorTree) throws Exception {
//        pruneNestedUnariesHelper(evaluatorTree.mainBlock, null);
//    }
//
//    private static void pruneNestedUnariesHelper(EvaluatorNode evaluatorNode, EvaluatorNode parent) throws Exception {
//        if (evaluatorNode == null)
//            return;
//
//        if (parent != null && evaluatorNode instanceof OperationNode opCurrent) {
//            while (opCurrent.isUnary()) {
//                if (opCurrent.getMember(0) instanceof OperationNode opChild && opChild.isUnary()) {
//                    if (!opCurrent.isCast() && !opChild.isCast()) {
//                        parent.replaceMember(opCurrent, opChild);
//
//                        if (KEY_OP_SUB.equals(opCurrent.getOperator())) {
//                            opChild.setOperator(opChild.getOperator().equals(KEY_OP_SUB) ? KEY_OP_ADD : KEY_OP_SUB);
//
//                        } else if (KEY_OP_ADD.equals(opCurrent.getOperator())) {
//                            // TODO: implement something
//
//                        } else {
//                            throw new Exception(String.format("Illegal unary operator %s. Is it not validated?", opCurrent.getOperator()));
//                        }
//                    } else {
//                        // TODO: implement something for nested casts
//
//                    }
//                    opCurrent = opChild;
//
//                } else {
//                    break;
//                }
//            }
//        }
//        for (int i = 0; i < evaluatorNode.memberCount(); i++) {
//            pruneNestedUnariesHelper(evaluatorNode.getMember(i), evaluatorNode);
//        }
//    }

    public static void convertUnariesToBinary(EvaluatorTree evaluatorTree, boolean debugMode) {
        convertUnariesToBinaryHelper(evaluatorTree.mainBlock, null, debugMode);
    }

    private static void convertUnariesToBinaryHelper(EvaluatorNode evaluatorNode, EvaluatorNode parent, boolean debugMode) {
        if (evaluatorNode == null)
            return;

        if (parent != null && evaluatorNode instanceof OperationNode opCurrent) {
            if (opCurrent.isUnary()) {
                if (debugMode)
                    System.out.println("converted unary operator " + opCurrent);

                try {
                    evaluatorNode = opCurrent.asBinaryFromMember(0);

                } catch (IllegalArgumentException e) {
                    evaluatorNode.throwSemanticError(e.getMessage(), evaluatorNode.nameToken);
                }
                parent.replaceMember(opCurrent, evaluatorNode);
            }
        }

        for (int i = 0; i < evaluatorNode.memberCount(); i++) {
            convertUnariesToBinaryHelper(evaluatorNode.getMember(i), evaluatorNode, debugMode);
        }
    }

    public static void solveBinaryExpressions(EvaluatorTree evaluatorTreeNode) {
        simplifyBinaryExpressionsHelper(evaluatorTreeNode.mainBlock);
    }

    private static void simplifyBinaryExpressionsHelper(EvaluatorNode evaluatorNode) {
        if (evaluatorNode == null)
            return;

        if (evaluatorNode instanceof OperationNode operationNode) {
            if (operationNode.isConstant()) {
                return;
            }
            
            if (!operationNode.isUnary()) {
                boolean leftIsConstant = operationNode.getLeftSide().isConstant();
                boolean rightIsConstant = operationNode.getRightSide().isConstant();

                if (!leftIsConstant) {
                    simplifyBinaryExpressionsHelper(operationNode.getLeftSide());
                }

                if (!rightIsConstant) {
                    simplifyBinaryExpressionsHelper(operationNode.getRightSide());
                }
                boolean leftIsNumeric = isNumeric(operationNode.getLeftSide().getConstantToken());
                boolean rightIsNumeric = isNumeric(operationNode.getRightSide().getConstantToken());

                leftIsConstant = operationNode.getLeftSide().isConstant();
                rightIsConstant = operationNode.getRightSide().isConstant();

                if (leftIsConstant && rightIsConstant && leftIsNumeric && rightIsNumeric) {
                    try {
                        operationMap.parseOperation(operationNode);

                    } catch (IllegalArgumentException e) {
                        evaluatorNode.throwSemanticError(e.getMessage(), evaluatorNode.nameToken);
                    }
                }
            } else {
                simplifyBinaryExpressionsHelper(evaluatorNode.getMember(0));
            }
        } else {
            for (int i = 0; i < evaluatorNode.memberCount(); i++) {
                simplifyBinaryExpressionsHelper(evaluatorNode.getMember(i));
            }
        }

        // validate and parse casts (has to be here because of how binary simplification works)
//        if (evaluatorNode instanceof OperationNode operationNode && operationNode.isCast()) {
//            if (operationNode.getMember(0) instanceof OperationNode opChild && opChild.isConstant()) {
////                if (castSolverMap.containsKey(operationNode.getOperator())) {
////                    castSolverMap.get(operationNode.getOperator()).accept(operationNode);
////
////                } else {
////                    throw new Exception(String.format("Unknown cast type \"%s\" on line %s", operationNode.getOperator(), evaluatorNode.token.line));
////                }
//                System.out.println("SECTION NOT IMPLEMENTED");
//            }
//        }
    }
}
