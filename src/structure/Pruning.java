package src.structure;

import src.constants.*;
import src.evaluators.*;
import static src.constants.Keywords.*;
import static src.constants.Functions.*;

/**
 * Abstract syntax tree pruning utilities
 * @author ElectricGun
 */

public class Pruning {
    public static EvaluatorNode pruneEmptyOperations(EvaluatorNode evaluatorNode) {
        pruneEmptyOperationGroupsHelper(evaluatorNode);

        return evaluatorNode;
    }

    private static void pruneEmptyOperationGroupsHelper(EvaluatorNode evaluatorNode) {
        for (int i = 0; i< evaluatorNode.members.size(); i++) {
            EvaluatorNode member = evaluatorNode.members.get(i);

            if (member instanceof OperationEvaluatorNode operationEvaluatorNode) {
                while (operationEvaluatorNode.isEmpty()) {
                    member = operationEvaluatorNode.members.getFirst();
                    if (member instanceof OperationEvaluatorNode operationEvaluatorNode1) {
                        evaluatorNode.members.set(i, member);

                        operationEvaluatorNode = operationEvaluatorNode1;
                    }
                }
            }
            pruneEmptyOperationGroupsHelper(member);
        }
    }

    public static EvaluatorNode simplifyBinaryExpressions(EvaluatorNode evaluatorNode) {
        simplifyBinaryExpressionsHelper(evaluatorNode);

        return evaluatorNode;
    }

    private static void simplifyBinaryExpressionsHelper(EvaluatorNode evaluatorNode) {
        if (evaluatorNode instanceof OperationEvaluatorNode operationEvaluatorNode) {

            if (operationEvaluatorNode.isConstant()) {
                return;

            } else if (!operationEvaluatorNode.isUnary()) {
                boolean leftIsConstant = operationEvaluatorNode.getLeftSide().isConstant();
                boolean rightIsConstant = operationEvaluatorNode.getRightSide().isConstant();

                if (!leftIsConstant) {
                    simplifyBinaryExpressionsHelper(operationEvaluatorNode.getLeftSide());
                }

                if (!rightIsConstant) {
                    simplifyBinaryExpressionsHelper(operationEvaluatorNode.getRightSide());
                }

                boolean leftIsNumeric = isNumeric(operationEvaluatorNode.getLeftSide().constantToken);
                boolean rightIsNumeric = isNumeric(operationEvaluatorNode.getRightSide().constantToken);

                leftIsConstant = operationEvaluatorNode.getLeftSide().isConstant();
                rightIsConstant = operationEvaluatorNode.getRightSide().isConstant();

                if (leftIsConstant && rightIsConstant && leftIsNumeric && rightIsNumeric) {
                    String leftConstantString = operationEvaluatorNode.getLeftSide().constantToken.string;
                    String rightConstantString = operationEvaluatorNode.getRightSide().constantToken.string;

                    if (Functions.equals(KEY_OP_ADD, operationEvaluatorNode.type)) {
                        operationEvaluatorNode.makeConstant( String.valueOf (
                            Double.parseDouble(leftConstantString) + Double.parseDouble(rightConstantString)
                            )
                        );
                    } else if (Functions.equals(KEY_OP_SUB, operationEvaluatorNode.type)) {
                        operationEvaluatorNode.makeConstant(String.valueOf(
                            Double.parseDouble(leftConstantString) - Double.parseDouble(rightConstantString)
                            )
                        );
                    } else if (Functions.equals(KEY_OP_MUL, operationEvaluatorNode.type)) {
                        operationEvaluatorNode.makeConstant( String.valueOf(
                            Double.parseDouble(leftConstantString) * Double.parseDouble(rightConstantString)
                            )
                        );
                    } else if (Functions.equals(KEY_OP_DIV, operationEvaluatorNode.type)) {
                        operationEvaluatorNode.makeConstant( String.valueOf(
                            Double.parseDouble(leftConstantString) / Double.parseDouble(rightConstantString)
                            )
                        );
                    }
                }
            }
        } else {
            for (int i = 0; i< evaluatorNode.members.size(); i++) {
                EvaluatorNode member = evaluatorNode.members.get(i);

                simplifyBinaryExpressionsHelper(member);
            }
        }
    }
}
