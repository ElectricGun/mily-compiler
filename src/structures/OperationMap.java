package src.structures;

import src.evaluators.*;
import src.tokens.*;

import java.util.*;
import java.util.function.*;

import static src.constants.Functions.*;
import static src.constants.Keywords.*;

/**
 * <h1> Class OperationMap </h1>
 * Used for checking the validity of as well as calculating the results of
 * values within operations
 * @author ElectricGun
 */

public class OperationMap {

    static class OperationKey {
        String operator;
        String leftType;
        String rightType;

        public OperationKey(String operator, String leftType, String rightType) {
            this.operator = operator;
            this.leftType = leftType;
            this.rightType = rightType;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            OperationKey that = (OperationKey) o;
            return Objects.equals(operator, that.operator) && Objects.equals(leftType, that.leftType) && Objects.equals(rightType, that.rightType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(operator, leftType, rightType);
        }
    }

//    static class UnaryOperationKey {
//        String operator;
//        String operandType;
//
//        public UnaryOperationKey(String operator, String operandType) {
//            this.operator = operator;
//            this.operandType = operandType;
//        }
//
//        @Override
//        public boolean equals(Object o) {
//            if (o == null || getClass() != o.getClass()) return false;
//            UnaryOperationKey that = (UnaryOperationKey) o;
//            return Objects.equals(operator, that.operator) && Objects.equals(operandType, that.operandType);
//        }
//
//        @Override
//        public int hashCode() {
//            return Objects.hash(operator, operandType);
//        }
//    }

    Map<String, Consumer<UnaryToBinaryStruct>> unaryOperationConversionMap = new HashMap<>();
    Map<OperationKey, Consumer<OperationNode>> operationParseMap = new HashMap<>();
    Map<OperationKey, String> operationCastMap = new HashMap<>();

    /**
     * Adds a converter for unary operations to binary
     */
    public void addUnaryOperationConverter(String operator, String operandType, Consumer<UnaryToBinaryStruct> operationConsumer) {
//        UnaryOperationKey unaryOperationKey = new UnaryOperationKey(operator, operandType);

        unaryOperationConversionMap.put(operator, operationConsumer);
    }

    public OperationNode generateBinaryFromUnaryAtMember(OperationNode operationNode, int memberIndex) {
        OperationNode newOp = new OperationNode(operationNode.token, operationNode.depth, operationNode.isReturnOperation());
        OperationNode memberChild = (OperationNode) operationNode.getMember(memberIndex);
        OperationNode factorConstant = new OperationNode(operationNode.token, operationNode.depth + 1);
        memberChild.depth += 1;
        // todo hardcode might reduce flexibility
        newOp.setType(KEY_OP_TYPE_OPERATION);

        UnaryToBinaryStruct unaryToBinaryStruct = new UnaryToBinaryStruct(operationNode, newOp, memberChild, factorConstant);

        if (!operationNode.isCast()) {
            String key = operationNode.getOperator();

            if (unaryOperationConversionMap.containsKey(key)) {
                unaryOperationConversionMap.get(key).accept(unaryToBinaryStruct);
            } else {
                throw new IllegalArgumentException("invalid unary operator \"" + key + "\"");

            }
        } else {
            newOp.setOperator(KEY_OP_CAST_EXPLICIT);
            factorConstant.constantToken = new TypedToken("1", operationNode.token.line, operationNode.getOperator());
        }

        newOp.setLeftSide(memberChild);
        newOp.setRightSide(factorConstant);

        return newOp;
    }

    public void generateBinaryFromUnary(OperationNode operationNode, int memberIndex) {
        generateBinaryFromUnaryAtMember(operationNode, 0);
    }

    public void addOperation(String operator, String leftType, String rightType, String castsTo, Consumer<OperationNode> operationConsumer) {
        OperationKey newOperationKey = new OperationKey(operator, leftType, rightType);
        
        operationParseMap.put(newOperationKey, operationConsumer);
        operationCastMap.put(newOperationKey, castsTo);
    }

    public void parseOperation(OperationNode operationNode) throws IllegalArgumentException {
        String operator = operationNode.getOperator();
        String leftType = operationNode.getLeftTokenType();
        String rightType = operationNode.getRightTokenType();

        // TODO. this complete skips parsing if one of the datatypes is dynamic
        if (keyEquals(KEY_DATA_DYNAMIC, leftType) || keyEquals(KEY_DATA_DYNAMIC, rightType)) {
            return;
        }

        OperationKey operationKeyCheck = new OperationKey(operator, leftType, rightType);
        String castTo = operationCastMap.get(operationKeyCheck);

        if (!operationParseMap.containsKey(operationKeyCheck) || !operationCastMap.containsKey(operationKeyCheck)) {
            throw new IllegalArgumentException(String.format("Invalid operator %s between types %s and %s on line %s", operator, leftType, rightType, operationNode.token.line));
        }

        operationParseMap.get(operationKeyCheck).accept(operationNode);

        try {
            operationNode.constantToken.setType(castTo);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException(String.format("Unable to parse operator %s on %s and %s. Is the lambda function empty?", operator, leftType, rightType));
        }
    }

    public String getCastTo(String operator, String leftType, String rightType) throws IllegalArgumentException {
        OperationKey operationKeyCheck = new OperationKey(operator, leftType, rightType);

        // TODO. should probably change how dynamic typing works
        if (keyEquals(KEY_DATA_DYNAMIC, leftType) || keyEquals(KEY_DATA_DYNAMIC, rightType)) {
            return KEY_DATA_DYNAMIC;
        }

        if (operationCastMap.containsKey(operationKeyCheck)) {
            return operationCastMap.get(operationKeyCheck);
        } else {
            throw new IllegalArgumentException(String.format("Operator %s cannot be applied to %s and %s", operator, leftType, rightType));
        }
    }

    public boolean isOperationValid(String operator, String leftType, String rightType)  {
        OperationKey operationKeyCheck = new OperationKey(operator, leftType, rightType);

        return operationCastMap.containsKey(operationKeyCheck);
    }
}
