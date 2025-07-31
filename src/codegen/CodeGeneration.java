package src.codegen;

import src.codegen.blocks.*;
import src.codegen.lines.*;
import src.parsing.*;

import static src.codegen.Mlogs.*;
import static src.constants.Keywords.*;

public class CodeGeneration {

    public static IRCode generateIRCode(EvaluatorTree evaluatorTree, boolean debugMode) throws Exception {
        IRCode irCode = new IRCode();

        generateIRCodeHelper(evaluatorTree.mainBlock, irCode, debugMode);

        // todo: end is sometimes redundant
        irCode.irBlocks.add(new IREnd());
        return irCode;
    }

    private static void generateIRCodeHelper(ScopeNode scopeNode, IRCode irCode, boolean debugMode) throws Exception {
        for (int i = 0; i < scopeNode.memberCount(); i++) {
            EvaluatorNode member = scopeNode.getMember(i);

            if (member instanceof DeclarationNode declarationNode) {
                if (declarationNode.memberCount() > 0 && declarationNode.getMember(0) instanceof FunctionDeclareNode fn) {

                    // TODO: fn
                } else if (declarationNode.memberCount() > 0 && declarationNode.getMember(0) instanceof OperationNode op) {
                    addOperationIRBlock(op, irCode, declarationNode.getVariableName(), debugMode);

                } else if (declarationNode.memberCount() == 0) {

                    // TODO: null declaration
                } else {
                    throw new Exception("Malformed declaration node found on codegen stage");
                }
            } else if (member instanceof AssignmentNode as) {
                // first member of assignment nodes should always be an operator
                // otherwise throw an error
                if (member.memberCount() <= 0 || !(member.getMember(0) instanceof OperationNode))
                    throw new Exception("Malformed assignment node found on codegen stage");
                addOperationIRBlock((OperationNode) member.getMember(0), irCode, as.getVariableName(), debugMode);

            } else if (member instanceof IfStatementNode ifs) {
                String branchEndLabel = ifs.nameToken + "end@" + ifs.hashCode();
                while (true) {
                    String currentIfEndLabel = ifs.nameToken + "@" + ifs.hashCode();

                    if (ifs.getElseNode() == null)
                        currentIfEndLabel = branchEndLabel;

                    String conditionalVarName = "if_cond@" + ifs.hashCode();
                    addOperationIRBlock(ifs.getExpression(), irCode, conditionalVarName, debugMode);

                    IRBlock startJumpBlock = new IRBlock();
                    // TODO: unhardcode how this works
                    Jump startJump = new Jump("jump@" + ifs.hashCode(),
                            opAsMlog(KEY_OP_EQUALS) + " " + conditionalVarName + " 1",
                            currentIfEndLabel);

                    startJumpBlock.lineList.add(startJump);
                    irCode.irBlocks.add(startJumpBlock);
                    generateIRCodeHelper(ifs.getScope(), irCode, debugMode);

                    if (ifs.getElseNode() != null) {
                        IRBlock alwaysJumpBlock = new IRBlock();
                        alwaysJumpBlock.lineList.add(new Jump("jump_end@" + ifs.hashCode(), "always", branchEndLabel));
                        irCode.irBlocks.add(alwaysJumpBlock);
                    }

                    IRBlock ifEndLabelBlock = new IRBlock();
                    ifEndLabelBlock.lineList.add(new Line("if_end@" + ifs.hashCode(), currentIfEndLabel + ":"));
                    irCode.irBlocks.add(ifEndLabelBlock);

                    if (ifs.getElseNode() instanceof ElseNode elseNode) {
                        if (elseNode.getIfStatementNode() instanceof IfStatementNode nestedIf) {
                            ifs = nestedIf;

                        } else {
                            generateIRCodeHelper(elseNode.getScope(), irCode, debugMode);

                            IRBlock elseEndLabelBlock = new IRBlock();
                            elseEndLabelBlock.lineList.add(new Line("else_end@" + ifs.hashCode(), branchEndLabel + ":"));
                            irCode.irBlocks.add(elseEndLabelBlock);

                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
        }
    }

    private static void addOperationIRBlock(OperationNode op, IRCode irCode, String variableName, boolean debugMode) {
        IROperation opBlock = generateIROperation(op, debugMode);
        irCode.irBlocks.add(opBlock);
        // change the name of the last op to the declared var name
        opBlock.lineList.getLast().setName(variableName);
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
                BinaryOp binaryOp = new BinaryOp(operationNode.nameToken.string, operationNode.getOperator(), valueAsMlog(operationNode.getLeftConstantString()), valueAsMlog(operationNode.getRightConstantString()));
                irOperation.lineList.add(binaryOp);

            } else {
                BinaryOp binaryOp;

                if (!rightConstant && leftConstant) {
                    binaryOp = new BinaryOp(operationNode.nameToken.string, operationNode.getOperator(), valueAsMlog(operationNode.getLeftConstantString()), operationNode.getRightSide().nameToken.string);
                    generateIROperationHelper(operationNode.getRightSide(), irOperation, debugMode);

                } else if (rightConstant) {
                    binaryOp = new BinaryOp(operationNode.nameToken.string, operationNode.getOperator(), operationNode.getLeftSide().nameToken.string, valueAsMlog(operationNode.getRightConstantString()));
                    generateIROperationHelper(operationNode.getLeftSide(), irOperation, debugMode);

                } else {
                    binaryOp = new BinaryOp(operationNode.nameToken.string, operationNode.getOperator(), operationNode.getLeftSide().nameToken.string, operationNode.getRightSide().nameToken.string);
                    generateIROperationHelper(operationNode.getLeftSide(), irOperation, debugMode);
                    generateIROperationHelper(operationNode.getRightSide(), irOperation, debugMode);
                }
                irOperation.lineList.add(binaryOp);
            }
        } else if (operationNode.isConstant()) {
            irOperation.lineList.add(new Set(operationNode.nameToken.string, valueAsMlog(operationNode.getConstantToken().string)));
        }
    }
}
