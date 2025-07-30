package src.codegen;

import src.codegen.blocks.*;
import src.codegen.lines.*;
import src.parsing.*;

public class CodeGeneration {

    public static IRCode generateIRCode(EvaluatorTree evaluatorTree, boolean debugMode) {
        IRCode irCode = new IRCode();

        generateIRCodeHelper(evaluatorTree.mainBlock, irCode, debugMode);

        return irCode;
    }

    private static void generateIRCodeHelper(ScopeNode scopeNode, IRCode irCode, boolean debugMode) {
        for (int i = 0; i < scopeNode.memberCount(); i++) {
            EvaluatorNode member = scopeNode.getMember(i);

            if (member instanceof DeclarationNode declarationNode) {
                if (declarationNode.memberCount() > 0 && declarationNode.getMember(0) instanceof FunctionDeclareNode fn) {

                    // TODO: fn
                } else if (declarationNode.memberCount() > 0 && declarationNode.getMember(0) instanceof OperationNode op) {
                    IROperation opBlock = generateIROperation(op, debugMode);
                    irCode.irBlocks.add(opBlock);
                    // change the name of the last op to the declared var name
                    opBlock.lineList.getLast().setName(declarationNode.getVariableName());

                } else if (declarationNode.memberCount() == 0) {

                    // TODO: null declaration
                }
            }
        }
    }

    public static IROperation generateIROperation(OperationNode operationNode, boolean debugMode) {
        IROperation irOperation = new IROperation();

        generateIROperationHelper(operationNode, irOperation, debugMode);

        return irOperation;
    }

    private static void generateIROperationHelper(OperationNode operationNode, IROperation irOperation, boolean debugMode) {
        if (operationNode.isBinary()) {
            boolean leftConstant = operationNode.getLeftSide().isConstant();
            boolean rightConstant = operationNode.getRightSide().isConstant();

            // TODO messy
            if (leftConstant && rightConstant) {
                BinaryOp binaryOp = new BinaryOp(operationNode.nameToken.string, operationNode.getOperator(), operationNode.getLeftConstantString(), operationNode.getRightConstantString());
                irOperation.lineList.add(binaryOp);

            } else {
                BinaryOp binaryOp;

                if (!rightConstant && leftConstant) {
                    binaryOp = new BinaryOp(operationNode.nameToken.string, operationNode.getOperator(), operationNode.getLeftConstantString(), operationNode.getRightSide().nameToken.string);
                    generateIROperationHelper(operationNode.getRightSide(), irOperation, debugMode);

                } else if (rightConstant) {
                    binaryOp = new BinaryOp(operationNode.nameToken.string, operationNode.getOperator(), operationNode.getLeftSide().nameToken.string, operationNode.getRightConstantString());
                    generateIROperationHelper(operationNode.getLeftSide(), irOperation, debugMode);

                } else {
                    binaryOp = new BinaryOp(operationNode.nameToken.string, operationNode.getOperator(), operationNode.getLeftSide().nameToken.string, operationNode.getRightConstantString());
                    generateIROperationHelper(operationNode.getLeftSide(), irOperation, debugMode);
                    generateIROperationHelper(operationNode.getRightSide(), irOperation, debugMode);

                }
                irOperation.lineList.add(binaryOp);
            }
        } else if (operationNode.isConstant()) {
            // TODO should use set instead maybe?
            BinaryOp binaryOp = new BinaryOp(operationNode.nameToken.string, "+", operationNode.getConstantToken().string, "0");
            irOperation.lineList.add(binaryOp);
        }
    }
}
