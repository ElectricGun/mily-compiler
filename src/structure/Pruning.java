package src.structure;

import src.evaluators.*;
import java.util.*;
import java.util.function.*;

import static src.constants.Keywords.*;
import static src.constants.Functions.*;

/**
 * <h1> AST Pruning </h1>
 * Static functions for pruning and simplifying the {@link EvaluatorTree}
 * @author ElectricGun
 */

public class Pruning {

    public static EvaluatorTree pruneEmptyOperations(EvaluatorTree evaluatorTree) {
        pruneEmptyOperationsHelper(evaluatorTree.mainBlock, null);

        return evaluatorTree;
    }

    private static void pruneEmptyOperationsHelper(EvaluatorNode evaluatorNode, EvaluatorNode parent) {
        if (evaluatorNode == null)
            return;

        if (evaluatorNode instanceof OperationNode operationNode) {
            // truncate children of self if they are empty
            truncateEmptyOperationChildrenRecursive(operationNode);

            // truncate self if empty
            if (operationNode.isBlank() && parent != null) {
                parent.replaceMember(operationNode, operationNode.getLeftSide());
            }
        }

        for (int i = 0; i< evaluatorNode.memberCount(); i++) {
            pruneEmptyOperationsHelper(evaluatorNode.getMember(i), evaluatorNode);
        }
    }

    private static void truncateEmptyOperationChildrenRecursive(OperationNode operationNode) {
        if (operationNode.getLeftSide() != null && operationNode.getLeftSide().isBlank()) {
            while (operationNode.getLeftSide().isBlank()) {
                operationNode.setLeftSide(operationNode.getLeftSide().getLeftSide());
            }
        }

        if (operationNode.getRightSide() != null && operationNode.getRightSide().isBlank()) {
            while (operationNode.getRightSide().isBlank())
                operationNode.setRightSide(operationNode.getRightSide().getLeftSide());
        }
    }

    public static EvaluatorTree simplifyNestedUnaries(EvaluatorTree evaluatorTree) throws Exception {
        simplifyUnariesHelper(evaluatorTree.mainBlock, null);

        return evaluatorTree;
    }

    private static void simplifyUnariesHelper(EvaluatorNode evaluatorNode, EvaluatorNode parent) throws Exception {
        if (evaluatorNode == null)
            return;

        if (parent != null && evaluatorNode instanceof OperationNode opCurrent) {
            while (opCurrent.isUnary()) {
                if (opCurrent.getMember(0) instanceof OperationNode opChild && opChild.isUnary()) {
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

    public static EvaluatorTree convertUnariesToBinary(EvaluatorTree evaluatorTree) throws Exception {
        convertUnariesToBinaryHelper(evaluatorTree.mainBlock, null);

        return evaluatorTree;
    }

    private static void convertUnariesToBinaryHelper(EvaluatorNode evaluatorNode, EvaluatorNode parent) throws Exception {
        if (evaluatorNode == null)
            return;

        if (parent != null && evaluatorNode instanceof OperationNode opCurrent) {
            if (opCurrent.isUnary()) {
                parent.replaceMember(opCurrent, opCurrent.asBinaryFromMember(0));
            }
        }

        for (int i = 0; i< evaluatorNode.memberCount(); i++) {
            convertUnariesToBinaryHelper(evaluatorNode.getMember(i), evaluatorNode);
        }
    }

    public static EvaluatorTree simplifyBinaryExpressions(EvaluatorTree evaluatorTreeNode) {
        simplifyBinaryExpressionsHelper(evaluatorTreeNode.mainBlock);

        return evaluatorTreeNode;
    }

    public static final Map<String, Consumer<OperationNode>> operationsParserMap = new HashMap<>();
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


        if (evaluatorNode instanceof OperationNode operationNode) {
            if (operationNode.isConstant()) {
                return;

            } else if (!operationNode.isUnary()) {
                boolean leftIsConstant = operationNode.getLeftSide().isConstant();
                boolean rightIsConstant = operationNode.getRightSide().isConstant();

//                System.out.println(evaluatorNode + " " + operationNode.getLeftSide() + " " + operationNode.getRightSide());

                if (!leftIsConstant) {
                    simplifyBinaryExpressionsHelper(operationNode.getLeftSide());
                }

                if (!rightIsConstant) {
                    simplifyBinaryExpressionsHelper(operationNode.getRightSide());
                }
                boolean leftIsNumeric = isNumeric(operationNode.getLeftSide().constantToken);
                boolean rightIsNumeric = isNumeric(operationNode.getRightSide().constantToken);

                leftIsConstant = operationNode.getLeftSide().isConstant();
                rightIsConstant = operationNode.getRightSide().isConstant();

                if (leftIsConstant && rightIsConstant && leftIsNumeric && rightIsNumeric) {
                    if (operationsParserMap.containsKey(operationNode.type)) {
                        operationsParserMap.get(operationNode.type).accept(operationNode);
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
