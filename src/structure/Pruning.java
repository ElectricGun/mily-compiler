package src.structure;

import src.evaluators.*;

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
                    evaluatorNode.members.set(i, member);
                    if (member instanceof OperationEvaluatorNode operationEvaluatorNode1) {
                        operationEvaluatorNode = operationEvaluatorNode1;
                    }
                }
            }
            pruneEmptyOperationGroupsHelper(member);
        }
    }
}
