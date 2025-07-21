package src.structures;

import src.evaluators.*;
import java.util.*;
import java.util.function.*;

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

    Map<OperationKey, Consumer<OperationNode>> operationValidityMap = new HashMap<>();
    Map<OperationKey, String> operationCastMap = new HashMap<>();

    public void addOperation(String operator, String leftType, String rightType, String castsTo, Consumer<OperationNode> operationConsumer) {
        OperationKey newOperationKey = new OperationKey(operator, leftType, rightType);
        
        operationValidityMap.put(newOperationKey, operationConsumer);
        operationCastMap.put(newOperationKey, castsTo);
    }

    public void parseOperation(OperationNode operationNode) throws Exception {
        String operator = operationNode.getOperator();
        String leftType = operationNode.getLeftTokenType();
        String rightType = operationNode.getRightTokenType();

        OperationKey operationKeyCheck = new OperationKey(operator, leftType, rightType);
        String castTo = operationCastMap.get(operationKeyCheck);

        if (!operationValidityMap.containsKey(operationKeyCheck) || !operationCastMap.containsKey(operationKeyCheck)) {
            throw new Exception(String.format("Invalid operator %s between types %s and %s on line %s", operator, leftType, rightType, operationNode.token.line));
        }

        operationValidityMap.get(operationKeyCheck).accept(operationNode);

        try {
            operationNode.constantToken.setType(castTo);
        } catch (NullPointerException e) {
            throw new Exception(String.format("Unabled to parse operator %s on %s and %s. Is the lambda function empty?", operator, leftType, rightType));
        }
    }

    public String getCastTo(String operator, String leftType, String rightType) throws Exception {
        OperationKey operationKeyCheck = new OperationKey(operator, leftType, rightType);

        if (operationCastMap.containsKey(operationKeyCheck)) {
            return operationCastMap.get(operationKeyCheck);
        } else {
            throw new Exception(String.format("Operator %s cannot be applied to %s and %s", operator, leftType, rightType));
        }
    }

    public boolean isOperationValid(String operator, String leftType, String rightType)  {
        OperationKey operationKeyCheck = new OperationKey(operator, leftType, rightType);

        return operationCastMap.containsKey(operationKeyCheck);
    }
}
