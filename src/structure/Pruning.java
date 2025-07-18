package src.structure;

import src.evaluators.*;
import java.util.*;
import java.util.function.*;

import static src.constants.Keywords.*;
import static src.constants.Functions.*;

/**
 * <h1> Class Pruning </h1>
 * Static functions for pruning and simplifying the {@link EvaluatorTree}
 * @author ElectricGun
 */

public class Pruning {

    // TODO implement more operators
    // This doesn't look elegant and can be improved, but eh
    public static final Map<String, Consumer<OperationNode>> operationsParserMap = new HashMap<>();
    static {
        operationsParserMap.put(KEY_OP_ADD, o -> {
            if ((KEY_DATA_INT.equals(o.getLeftConstantType()) && KEY_DATA_INT.equals(o.getRightConstantType()))) {
                o.makeConstant((int) (o.getLeftConstantNumeric() + o.getRightConstantNumeric()));
            } else {
                o.makeConstant(o.getLeftConstantNumeric() + o.getRightConstantNumeric());
            }
        });
        operationsParserMap.put(KEY_OP_SUB, o -> {
            if ((KEY_DATA_INT.equals(o.getLeftConstantType()) && KEY_DATA_INT.equals(o.getRightConstantType()))) {
                o.makeConstant((int) (o.getLeftConstantNumeric() - o.getRightConstantNumeric()));

            } else {
                o.makeConstant(o.getLeftConstantNumeric() - o.getRightConstantNumeric());
            }
        });
        operationsParserMap.put(KEY_OP_MUL, o -> {
            if ((KEY_DATA_INT.equals(o.getLeftConstantType()) && KEY_DATA_INT.equals(o.getRightConstantType()))) {
                o.makeConstant((int) (o.getLeftConstantNumeric() * o.getRightConstantNumeric()));

            } else {
                o.makeConstant(o.getLeftConstantNumeric() * o.getRightConstantNumeric());
            }
        });
        operationsParserMap.put(KEY_OP_DIV, o -> o.makeConstant(o.getLeftConstantNumeric() / o.getRightConstantNumeric()));
        operationsParserMap.put(KEY_OP_MOD, o -> {
            if ((KEY_DATA_INT.equals(o.getLeftConstantType()) && KEY_DATA_INT.equals(o.getRightConstantType()))) {
                o.makeConstant((int) (o.getLeftConstantNumeric() % o.getRightConstantNumeric()));

            } else {
                o.makeConstant(o.getLeftConstantNumeric() % o.getRightConstantNumeric());
            }
        });
        operationsParserMap.put(KEY_OP_IDIV, o -> o.makeConstant(
                (int) Math.floor(
                        o.getLeftConstantNumeric()
                                /
                        o.getRightConstantNumeric()
                )
        ));
        operationsParserMap.put(KEY_OP_POW, o ->{
            if ((KEY_DATA_INT.equals(o.getLeftConstantType()) && KEY_DATA_INT.equals(o.getRightConstantType()))) {
                o.makeConstant((int) Math.pow(
                        o.getLeftConstantNumeric(),
                        o.getRightConstantNumeric()
                ));

            } else {
                o.makeConstant( Math.pow(
                        o.getLeftConstantNumeric(),
                        o.getRightConstantNumeric()
                ));
            }
        });
    }

    public static final Map<String, Consumer<OperationNode>> castsParserMap = new HashMap<>();
    static {
        castsParserMap.put(KEY_DATA_INT, o -> {
            o.setType(KEY_DATA_INT);
            o.makeConstant((int) Double.parseDouble(((OperationNode) o.getMember(0)).constantToken.string)
            );
        });
        castsParserMap.put(KEY_DATA_DOUBLE, o -> {
            // TODO currently this does nothing, why would you cast to a double?
        });
        castsParserMap.put(KEY_DATA_BOOLEAN, o -> {
            // TODO currently this does nothing
        });
        castsParserMap.put(KEY_DATA_STRING, o -> {
            // TODO currently this does nothing
        });
        castsParserMap.put(KEY_DATA_LET, o -> {
            // TODO currently this does nothing
        });
    }

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

    public static EvaluatorTree simplifyUnaries(EvaluatorTree evaluatorTree) throws Exception {
        simplifyNestedUnariesHelper(evaluatorTree.mainBlock, null);

        return evaluatorTree;
    }

    private static void simplifyNestedUnariesHelper(EvaluatorNode evaluatorNode, EvaluatorNode parent) throws Exception {
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
                            throw new Exception("Illegal unary operator detected on pruning stage");
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
            simplifyNestedUnariesHelper(evaluatorNode.getMember(i), evaluatorNode);
        }
    }

//    public static EvaluatorTree simplifyCasts(EvaluatorTree evaluatorTree) throws Exception {
//        simplifyCastsHelper(evaluatorTree.mainBlock, null);
//
//        return evaluatorTree;
//    }
//
//    private static void simplifyCastsHelper(EvaluatorNode evaluatorNode, EvaluatorNode parent) {
//        if (evaluatorNode == null)
//            return;
//
//        if (parent != null && evaluatorNode instanceof OperationNode opCurrent) {
//            while (opCurrent.isUnary()) {
//                if (opCurrent.getMember(0) instanceof OperationNode opChild && opChild.isUnary()) {
//                    parent.replaceMember(opCurrent, opChild);
//                }
//            }
//        }
//
//    }

    public static EvaluatorTree convertUnariesToBinary(EvaluatorTree evaluatorTree) throws Exception {
        convertUnariesToBinaryHelper(evaluatorTree.mainBlock, null);

        return evaluatorTree;
    }

    private static void convertUnariesToBinaryHelper(EvaluatorNode evaluatorNode, EvaluatorNode parent) throws Exception {
        if (evaluatorNode == null)
            return;

        if (parent != null && evaluatorNode instanceof OperationNode opCurrent) {
            if (opCurrent.isUnary() && !opCurrent.isCast()) {
                System.out.println("converted unary operator" + opCurrent);
                parent.replaceMember(opCurrent, opCurrent.asBinaryFromMember(0));
            }
        }

        for (int i = 0; i< evaluatorNode.memberCount(); i++) {
            convertUnariesToBinaryHelper(evaluatorNode.getMember(i), evaluatorNode);
        }
    }

    public static EvaluatorTree simplifyBinaryExpressions(EvaluatorTree evaluatorTreeNode) throws Exception {
        simplifyBinaryExpressionsHelper(evaluatorTreeNode.mainBlock, null, true);

        return evaluatorTreeNode;
    }

    private static void simplifyBinaryExpressionsHelper(EvaluatorNode evaluatorNode, EvaluatorNode parent, boolean parseCasts) throws Exception {
        if (evaluatorNode == null)
            return;

        if (evaluatorNode instanceof OperationNode operationNode) {
            if (operationNode.isConstant()) {
                return;

            } else if (!operationNode.isUnary()) {
                boolean leftIsConstant = operationNode.getLeftSide().isConstant();
                boolean rightIsConstant = operationNode.getRightSide().isConstant();

                if (!leftIsConstant) {
                    simplifyBinaryExpressionsHelper(operationNode.getLeftSide(), evaluatorNode, parseCasts);
                }

                if (!rightIsConstant) {
                    simplifyBinaryExpressionsHelper(operationNode.getRightSide(), evaluatorNode, parseCasts);
                }
                boolean leftIsNumeric = isNumeric(operationNode.getLeftSide().constantToken);
                boolean rightIsNumeric = isNumeric(operationNode.getRightSide().constantToken);

                leftIsConstant = operationNode.getLeftSide().isConstant();
                rightIsConstant = operationNode.getRightSide().isConstant();

                if (leftIsConstant && rightIsConstant && leftIsNumeric && rightIsNumeric) {
                    if (operationsParserMap.containsKey(operationNode.getOperator())) {
                        operationsParserMap.get(operationNode.getOperator()).accept(operationNode);
                    }
                }
            } else {
                simplifyBinaryExpressionsHelper(evaluatorNode.getMember(0), evaluatorNode, parseCasts);
            }
        } else {
            for (int i = 0; i< evaluatorNode.memberCount(); i++) {
                simplifyBinaryExpressionsHelper(evaluatorNode.getMember(i), evaluatorNode, parseCasts);
            }
        }

        // validate and parse casts (has to be here because of how binary simplification works)
        if (evaluatorNode instanceof OperationNode operationNode && operationNode.isCast()) {
            if (operationNode.getMember(0) instanceof OperationNode opChild && opChild.isConstant()) {
                if (castsParserMap.containsKey(operationNode.getOperator())) {
                    castsParserMap.get(operationNode.getOperator()).accept(operationNode);

                } else {
                    throw new Exception(String.format("Unknown cast type \"%s\" on line %s", operationNode.getOperator(), evaluatorNode.token.line));
                }
            }
        }
    }
}
