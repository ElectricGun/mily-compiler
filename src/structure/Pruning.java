package src.structure;

import src.constants.*;
import src.evaluators.*;

import java.util.*;
import java.util.function.Consumer;

import static src.constants.Keywords.*;
import static src.constants.Functions.*;

/**
 * Abstract syntax tree pruning utilities
 * @author ElectricGun
 */

public class Pruning {
    public static EvaluatorNode pruneEmptyOperations(EvaluatorNode evaluatorNode) {
        pruneEmptyOperationsHelper(evaluatorNode, null);

        return evaluatorNode;
    }

    private static void pruneEmptyOperationsHelper(EvaluatorNode evaluatorNode, EvaluatorNode parent) {
        if (evaluatorNode == null)
            return;

        if (evaluatorNode instanceof OperationEvaluatorNode operationEvaluatorNode) {

            // truncate children of self if they are empty
            truncateEmptyOperationChildrenRecursive(operationEvaluatorNode);

            // truncate self if empty
            if (operationEvaluatorNode.isEmpty()) {
                parent.members.set(parent.members.indexOf(operationEvaluatorNode), operationEvaluatorNode.getLeftSide());
            }
        }

        for (int i = 0; i< evaluatorNode.members.size(); i++) {
            EvaluatorNode member = evaluatorNode.members.get(i);

            pruneEmptyOperationsHelper(member, evaluatorNode);
        }
    }

    private static void truncateEmptyOperationChildrenRecursive(OperationEvaluatorNode operationEvaluatorNode) {
        if (operationEvaluatorNode.getLeftSide() != null && operationEvaluatorNode.getLeftSide().isEmpty()) {
            while (operationEvaluatorNode.getLeftSide().isEmpty()) {
                operationEvaluatorNode.setLeftSide(operationEvaluatorNode.getLeftSide().getLeftSide());
            }
        }

        if (operationEvaluatorNode.getRightSide() != null && operationEvaluatorNode.getRightSide().isEmpty()) {
            while (operationEvaluatorNode.getRightSide().isEmpty())
                operationEvaluatorNode.setRightSide(operationEvaluatorNode.getRightSide().getLeftSide());
        }
    }

    public static EvaluatorNode simplifyBinaryExpressions(EvaluatorNode evaluatorNode) {
        simplifyBinaryExpressionsHelper(evaluatorNode);

        return evaluatorNode;
    }

    public static final Map<String, Consumer<OperationEvaluatorNode>> operationsParserMap = new HashMap<>();
    static {
        operationsParserMap.put(KEY_OP_ADD, o -> o.makeConstant(
            o.getLeftConstantNumeric() +
            o.getRightConstantNumeric()
        ));
        operationsParserMap.put(KEY_OP_SUB, o -> o.makeConstant(
            o.getLeftConstantNumeric() -
            o.getRightConstantNumeric()
        ));
        operationsParserMap.put(KEY_OP_MUL, o -> o.makeConstant(
            o.getLeftConstantNumeric() *
            o.getRightConstantNumeric()
        ));
        operationsParserMap.put(KEY_OP_DIV, o -> o.makeConstant(
            o.getLeftConstantNumeric() /
            o.getRightConstantNumeric()
        ));
        operationsParserMap.put(KEY_OP_MOD, o -> o.makeConstant(
            o.getLeftConstantNumeric() %
            o.getRightConstantNumeric()
        ));
        operationsParserMap.put(KEY_OP_IDIV, o -> o.makeConstant(
            Math.floor(
                o.getLeftConstantNumeric() /
                o.getRightConstantNumeric()
            )
        ));
        operationsParserMap.put(KEY_OP_POW, o -> o.makeConstant(
            Math.pow(
                o.getLeftConstantNumeric(),
                o.getRightConstantNumeric()
            )
        ));
    }

    private static void simplifyBinaryExpressionsHelper(EvaluatorNode evaluatorNode) {
        if (evaluatorNode == null)
            return;

        if (evaluatorNode instanceof OperationEvaluatorNode operationEvaluatorNode && !operationEvaluatorNode.isUnary()) {
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
                    if (operationsParserMap.containsKey(operationEvaluatorNode.type)) {
                        operationsParserMap.get(operationEvaluatorNode.type).accept(operationEvaluatorNode);
                    }
                }
            }
        } else {
            for (int i = 0; i< evaluatorNode.members.size(); i++) {
                simplifyBinaryExpressionsHelper(evaluatorNode.members.get(i));
            }
        }
    }
}
