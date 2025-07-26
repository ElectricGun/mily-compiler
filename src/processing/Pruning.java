package src.processing;

import src.evaluators.*;

import javax.naming.OperationNotSupportedException;

import static src.constants.Keywords.*;
import static src.constants.Functions.*;
import static src.constants.Maps.*;

/**
 * <h1> Class Pruning </h1>
 * Static functions for pruning and simplifying the {@link EvaluatorTree}
 * @author ElectricGun
 */

public class Pruning {

    public static void removeEmptyOperations(EvaluatorTree evaluatorTree, boolean debugMode) {
        removeEmptyOperationsHelper(evaluatorTree.mainBlock, null, debugMode);
    }

    private static void removeEmptyOperationsHelper(EvaluatorNode evaluatorNode, EvaluatorNode parent, boolean debugMode) {
        if (evaluatorNode == null)
            return;

        if (evaluatorNode instanceof OperationNode operationNode) {
            // truncate children of self if they are empty
            truncateEmptyOperationChildrenRecursive(operationNode);

            // truncate self if empty
            if (operationNode.isEmptyConstant() && parent != null) {
                parent.replaceMember(operationNode, operationNode.getLeftSide());
            }
        }

        for (int i = 0; i< evaluatorNode.memberCount(); i++) {
            removeEmptyOperationsHelper(evaluatorNode.getMember(i), evaluatorNode, debugMode);
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

    public static void pruneNestedUnaries(EvaluatorTree evaluatorTree, boolean debugMode) throws Exception {
        pruneNestedUnariesHelper(evaluatorTree.mainBlock, null, debugMode);
    }

    private static void pruneNestedUnariesHelper(EvaluatorNode evaluatorNode, EvaluatorNode parent, boolean debugMode) throws Exception {
        if (evaluatorNode == null)
            return;

        if (parent != null && evaluatorNode instanceof OperationNode opCurrent) {
            while (opCurrent.isUnary()) {
                if (opCurrent.getMember(0) instanceof OperationNode opChild && opChild.isUnary()) {
                    if (!opCurrent.isCast() && !opChild.isCast()) {
                        parent.replaceMember(opCurrent, opChild);

                        if (KEY_OP_SUB.equals(opCurrent.getOperator())) {
                            opChild.setOperator(opChild.getOperator().equals(KEY_OP_SUB) ? KEY_OP_ADD : KEY_OP_SUB);

                        } else if (KEY_OP_ADD.equals(opCurrent.getOperator())) {
                            // todo implement something

                        } else {
                            throw new Exception(String.format("Illegal unary operator %s. Is it not validated?", opCurrent.getOperator()));
                        }
                    } else {
                        // todo implement something for nested casts

                    }
                    opCurrent = opChild;

                } else {
                    break;
                }
            }
        }
        for (int i = 0; i< evaluatorNode.memberCount(); i++) {
            pruneNestedUnariesHelper(evaluatorNode.getMember(i), evaluatorNode, debugMode);
        }
    }

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
                    evaluatorNode.throwSemanticError(e.getMessage(), evaluatorNode.token);
                }
                parent.replaceMember(opCurrent, evaluatorNode);
            }
        }

        for (int i = 0; i< evaluatorNode.memberCount(); i++) {
            convertUnariesToBinaryHelper(evaluatorNode.getMember(i), evaluatorNode, debugMode);
        }
    }

    public static void solveBinaryExpressions(EvaluatorTree evaluatorTreeNode, boolean debugMode) {
        simplifyBinaryExpressionsHelper(evaluatorTreeNode.mainBlock, null, true, debugMode);
    }

    private static void simplifyBinaryExpressionsHelper(EvaluatorNode evaluatorNode, EvaluatorNode parent, boolean parseCasts, boolean debugMode) {
        if (evaluatorNode == null)
            return;

        if (evaluatorNode instanceof OperationNode operationNode) {
            if (operationNode.isConstant()) {
                return;

            } else if (!operationNode.isUnary()) {
                boolean leftIsConstant = operationNode.getLeftSide().isConstant();
                boolean rightIsConstant = operationNode.getRightSide().isConstant();

                if (!leftIsConstant) {
                    simplifyBinaryExpressionsHelper(operationNode.getLeftSide(), evaluatorNode, parseCasts, debugMode);
                }

                if (!rightIsConstant) {
                    simplifyBinaryExpressionsHelper(operationNode.getRightSide(), evaluatorNode, parseCasts, debugMode);
                }
                boolean leftIsNumeric = isNumeric(operationNode.getLeftSide().constantToken);
                boolean rightIsNumeric = isNumeric(operationNode.getRightSide().constantToken);

                leftIsConstant = operationNode.getLeftSide().isConstant();
                rightIsConstant = operationNode.getRightSide().isConstant();

                if (leftIsConstant && rightIsConstant && leftIsNumeric && rightIsNumeric) {
                    try {
                        operationMap.parseOperation(operationNode);

                    } catch (IllegalArgumentException e) {
                        evaluatorNode.throwSemanticError(e.getMessage(), evaluatorNode.token);
                    }
                }
            } else {
                simplifyBinaryExpressionsHelper(evaluatorNode.getMember(0), evaluatorNode, parseCasts, debugMode);
            }
        } else {
            for (int i = 0; i< evaluatorNode.memberCount(); i++) {
                simplifyBinaryExpressionsHelper(evaluatorNode.getMember(i), evaluatorNode, parseCasts, debugMode);
            }
        }

        // validate and parse casts (has to be here because of how binary simplification works)
        if (evaluatorNode instanceof OperationNode operationNode && operationNode.isCast()) {
            if (operationNode.getMember(0) instanceof OperationNode opChild && opChild.isConstant()) {
//                if (castSolverMap.containsKey(operationNode.getOperator())) {
//                    castSolverMap.get(operationNode.getOperator()).accept(operationNode);
//
//                } else {
//                    throw new Exception(String.format("Unknown cast type \"%s\" on line %s", operationNode.getOperator(), evaluatorNode.token.line));
//                }
                System.out.println("SECTION NOT IMPLEMENTED");
            }
        }
    }
}
