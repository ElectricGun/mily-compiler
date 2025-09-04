package mily.structures.dataobjects;

import mily.parsing.*;

import java.util.*;
import java.util.function.*;

import static mily.constants.Keywords.*;

/**
 * <h1> Class OperationMap </h1>
 * Used for checking the validity of as well as calculating the results of
 * values within operations
 *
 * @author ElectricGun
 */

public class OperationMap {

    protected final Map<String, Consumer<UnaryToBinaryStruct>> unaryOperationConversionMap = new HashMap<>();
    protected final Map<OperationKey, Consumer<OperationNode>> operationParseMap = new HashMap<>();
    protected final Map<OperationKey, Type> operationCastMap = new HashMap<>();

    /**
     * Adds a converter for unary operations to binary <br>
     * There is no operand type because type checking is only done on binary operators
     */
    public void addUnaryOperationConverter(String operator, Consumer<UnaryToBinaryStruct> operationConsumer) {
        unaryOperationConversionMap.put(operator, operationConsumer);
    }

    public OperationNode generateBinaryFromUnaryAtMember(OperationNode operationNode, int memberIndex) {
        OperationNode newOp = new OperationNode(operationNode.nameToken, operationNode.depth, operationNode.isReturnOperation());
        OperationNode memberChild = (OperationNode) operationNode.getMember(memberIndex);
        OperationNode factorConstant = new OperationNode(operationNode.nameToken, operationNode.depth + 1);
        memberChild.depth += 1;
        //this hardcode may reduce flexibility
        newOp.setType(KEY_OP_TYPE_OPERATION);

        UnaryToBinaryStruct unaryToBinaryStruct = new UnaryToBinaryStruct(operationNode, newOp, memberChild, factorConstant);
        String key = operationNode.getOperator();

        if (unaryOperationConversionMap.containsKey(key)) {
            unaryOperationConversionMap.get(key).accept(unaryToBinaryStruct);

        } else {
            throw new IllegalArgumentException("invalid unary operator \"" + key + "\"");
        }

        newOp.setLeftSide(memberChild);
        newOp.setRightSide(factorConstant);

        return newOp;
    }

    public void addOperation(String operator, Type leftType, Type rightType, Type castsTo, Consumer<OperationNode> operationConsumer) {
        OperationKey newOperationKey = new OperationKey(operator, leftType, rightType);

        operationParseMap.put(newOperationKey, operationConsumer);
        operationCastMap.put(newOperationKey, castsTo);
    }

    public void addImplicitCast(Type leftType, Type rightType, Type castsTo, Consumer<OperationNode> operationConsumer) {
        addOperation(KEY_OP_CAST_IMPLICIT, leftType, rightType, castsTo, operationConsumer);
    }

    public void parseOperation(OperationNode operationNode) throws IllegalArgumentException, NoSuchMethodError {
        String operator = operationNode.getOperator();
        Type leftType = operationNode.getLeftTokenType();
        Type rightType = operationNode.getRightTokenType();

        OperationKey operationKeyCheck = new OperationKey(operator, leftType, rightType);
        Type castTo = operationCastMap.get(operationKeyCheck);

        if (!operationParseMap.containsKey(operationKeyCheck) || !operationCastMap.containsKey(operationKeyCheck)) {
            throw new IllegalArgumentException(String.format("Invalid operator %s between types %s and %s on line %s", operator, leftType, rightType, operationNode.nameToken.line));
        }

        operationParseMap.get(operationKeyCheck).accept(operationNode);

        try {
            operationNode.getConstantToken().setType(castTo);

        } catch (NullPointerException e) {
            throw new NoSuchMethodError(String.format("Unable to parse operator %s on %s and %s. Is the lambda function empty?", operator, leftType, rightType));
        }
    }

    public Type getCastTo(String operator, Type leftType, Type rightType) throws IllegalArgumentException {
        OperationKey operationKeyCheck = new OperationKey(operator, leftType, rightType);

        if (operationCastMap.containsKey(operationKeyCheck)) {
            return operationCastMap.get(operationKeyCheck);

        } else {
            throw new IllegalArgumentException(String.format("Operator %s cannot be applied to %s and %s", operator, leftType, rightType));
        }
    }

    public boolean isOperationValid(String operator, Type leftType, Type rightType) {
        OperationKey operationKeyCheck = new OperationKey(operator, leftType, rightType);

        return operationCastMap.containsKey(operationKeyCheck);
    }

    protected static class OperationKey {
        final String operator;
        final Type leftType;
        final Type rightType;

        public OperationKey(String operator, Type leftType, Type rightType) {
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
}
