package src.structure;

import src.evaluators.*;
import java.util.*;
import java.util.function.*;

import static src.constants.Keywords.*;
import static src.constants.Functions.*;

/**
 * Abstract syntax tree pruning utilities
 * @author ElectricGun
 */

public class Pruning {

    public static Evaluator pruneEmptyOperations(Evaluator evaluator) {
        pruneEmptyOperationsHelper(evaluator.mainBlock, null);

        return evaluator;
    }

    private static void pruneEmptyOperationsHelper(EvaluatorNode evaluatorNode, EvaluatorNode parent) {
        if (evaluatorNode == null)
            return;

        if (evaluatorNode instanceof OperationEvaluatorNode operationEvaluatorNode) {
            // truncate children of self if they are empty
            truncateEmptyOperationChildrenRecursive(operationEvaluatorNode);

            // truncate self if empty
            if (operationEvaluatorNode.isBlank() && parent != null) {
                parent.replaceMember(operationEvaluatorNode, operationEvaluatorNode.getLeftSide());
            }
        }

        for (int i = 0; i< evaluatorNode.memberCount(); i++) {
            pruneEmptyOperationsHelper(evaluatorNode.getMember(i), evaluatorNode);
        }
    }

    private static void truncateEmptyOperationChildrenRecursive(OperationEvaluatorNode operationEvaluatorNode) {
        if (operationEvaluatorNode.getLeftSide() != null && operationEvaluatorNode.getLeftSide().isBlank()) {
            while (operationEvaluatorNode.getLeftSide().isBlank()) {
                operationEvaluatorNode.setLeftSide(operationEvaluatorNode.getLeftSide().getLeftSide());
            }
        }

        if (operationEvaluatorNode.getRightSide() != null && operationEvaluatorNode.getRightSide().isBlank()) {
            while (operationEvaluatorNode.getRightSide().isBlank())
                operationEvaluatorNode.setRightSide(operationEvaluatorNode.getRightSide().getLeftSide());
        }
    }

    public static Evaluator simplifyNestedUnaries(Evaluator evaluator) throws Exception {
        simplifyUnariesHelper(evaluator.mainBlock, null);

        return evaluator;
    }

    private static void simplifyUnariesHelper(EvaluatorNode evaluatorNode, EvaluatorNode parent) throws Exception {
        if (evaluatorNode == null)
            return;

        if (parent != null && evaluatorNode instanceof OperationEvaluatorNode opCurrent) {
            while (opCurrent.isUnary()) {
                if (opCurrent.getMember(0) instanceof OperationEvaluatorNode opChild && opChild.isUnary()) {
                    parent.replaceMember(opCurrent, opChild);

                    if (KEY_OP_SUB.equals(opCurrent.type)) {
                        opChild.type = opChild.type.equals(KEY_OP_SUB) ? KEY_OP_ADD : KEY_OP_SUB;

                    } else if (KEY_OP_ADD.equals(opCurrent.type)) {
                        // todo something

                    } else {
                        throw new Exception("Illegal unary operator detected on pruning stage");
                    }
                    opCurrent = opChild;

                } else {
                    break;
                }
            }
        }

        for (int i = 0; i< evaluatorNode.memberCount(); i++) {
            simplifyUnariesHelper(evaluatorNode.getMember(i), evaluatorNode);
        }
    }

    public static Evaluator simplifyBinaryExpressions(Evaluator evaluatorNode) {
        simplifyBinaryExpressionsHelper(evaluatorNode.mainBlock);

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
                    if (operationsParserMap.containsKey(operationEvaluatorNode.type)) {
                        operationsParserMap.get(operationEvaluatorNode.type).accept(operationEvaluatorNode);
                    }
                }
            } else {
                simplifyBinaryExpressionsHelper(evaluatorNode.getMember(0));
            }
        } else {
            for (int i = 0; i< evaluatorNode.memberCount(); i++) {
                simplifyBinaryExpressionsHelper(evaluatorNode.getMember(i));
            }
        }
    }
}
