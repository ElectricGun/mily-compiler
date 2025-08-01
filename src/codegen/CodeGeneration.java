package src.codegen;

import src.codegen.blocks.*;
import src.codegen.lines.*;
import src.parsing.*;

import static src.codegen.Mlogs.*;
import static src.constants.Functions.*;
import static src.constants.Keywords.*;

public class CodeGeneration {

    public static IRCode generateIRCode(EvaluatorTree evaluatorTree, boolean debugMode) throws Exception {
        IRCode irCode = new IRCode();

        generateIRCodeHelper(evaluatorTree.mainBlock, irCode, new HashCodeSimplifier(), 0, debugMode);

        // todo: end is sometimes redundant
        irCode.irBlocks.add(new IREnd());
        return irCode;
    }

    private static void generateIRCodeHelper(ScopeNode scopeNode, IRCode irCode, HashCodeSimplifier hashSimplifier, int depth, boolean debugMode) throws Exception {
        for (int i = 0; i < scopeNode.memberCount(); i++) {
            EvaluatorNode member = scopeNode.getMember(i);

            if (member instanceof DeclarationNode declarationNode) {
                if (declarationNode.memberCount() > 0 && declarationNode.getMember(0) instanceof FunctionDeclareNode fn) {

                    // TODO: fn
                } else if (declarationNode.memberCount() > 0 && declarationNode.getMember(0) instanceof OperationNode op) {
                    addOperationIRBlock(op, irCode, declarationNode.getVariableName(), depth, debugMode);

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
                addOperationIRBlock((OperationNode) member.getMember(0), irCode, as.getVariableName(), depth, debugMode);

            } else if (member instanceof IfStatementNode ifs) {
                generateBranchStatement(ifs, irCode, hashSimplifier, depth, debugMode);

            } else if (member instanceof WhileLoopNode whileLoop) {
                // todo unify copy pastes
                String whileHashCode = "" + hashSimplifier.simplifyHash(whileLoop.hashCode());
                String startLabelString = "while_loop_start_" + whileHashCode;

                IRBlock startLabelBlock = new IRBlock();
                Line startLabelLine = new Line("while_loop_start", startLabelString + ":", depth);
                startLabelBlock.lineList.add(startLabelLine);
                irCode.irBlocks.add(startLabelBlock);

                generateIRCodeHelper(whileLoop.getScope(), irCode, hashSimplifier, depth + 1, debugMode);

                // create loop jump
                boolean invertCondition = false;
                Jump jump = createConditionalJump(
                        whileLoop.getExpression(),
                        whileHashCode,
                        startLabelString,
                        irCode,
                        invertCondition,
                        depth,
                        debugMode
                );

                IRBlock jumpBlock = new IRBlock();
                jumpBlock.lineList.add(jump);
                irCode.irBlocks.add(jumpBlock);

            } else if (member instanceof ForLoopNode forLoop) {
                // todo unify copy pastes
                String forLoopHashCode = "" + hashSimplifier.simplifyHash(forLoop.hashCode());
                String startLabelString = "for_loop_start_" + forLoopHashCode;

                // initial
                VariableNode initial = forLoop.getInitial();
                if (initial == null || initial.memberCount() <= 0 || !(initial.getMember(0) instanceof OperationNode))
                    throw new Exception("Malformed for loop updater found on codegen stage");

                addOperationIRBlock((OperationNode) initial.getMember(0), irCode, initial.getVariableName(), depth, debugMode);

                // loop start
                IRBlock startLabelBlock = new IRBlock();
                Line startLabelLine = new Line("for_loop_start", startLabelString + ":", depth);
                startLabelBlock.lineList.add(startLabelLine);
                irCode.irBlocks.add(startLabelBlock);

                // code block
                generateIRCodeHelper(forLoop.getScope(), irCode, hashSimplifier, depth + 1, debugMode);

                // updater
                AssignmentNode updater = forLoop.getUpdater();
                if (updater == null || updater.memberCount() <= 0 || !(updater.getMember(0) instanceof OperationNode))
                    throw new Exception("Malformed for loop updater found on codegen stage");

                addOperationIRBlock((OperationNode) updater.getMember(0), irCode, updater.getVariableName(), depth, debugMode);

                // create loop jump
                boolean invertCondition = false;
                Jump jump = createConditionalJump(
                        forLoop.getCondition(),
                        forLoopHashCode,
                        startLabelString,
                        irCode,
                        invertCondition,
                        depth,
                        debugMode
                );

                IRBlock jumpBlock = new IRBlock();
                jumpBlock.lineList.add(jump);
                irCode.irBlocks.add(jumpBlock);
            }
        }
    }

    private static void generateBranchStatement(IfStatementNode ifs, IRCode irCode, HashCodeSimplifier hashSimplifier, int depth, boolean debugMode) throws Exception {
        String branchEndLabel = "branch_end_" + hashSimplifier.simplifyHash(ifs.hashCode());

        // while loop to go through all the else blocks
        // todo change the true to an actual conditional
        while (true) {
            // loop repeats with a new ifs object
            String currentifHashCode = "" + hashSimplifier.simplifyHash(ifs.hashCode());
            String currentIfEndLabel = ifs.nameToken + "_" + currentifHashCode;

            // if current if statement has no else block, just jump to the end
            if (ifs.getElseNode() == null)
                currentIfEndLabel = branchEndLabel;

            // create jump statement (doesn't look pretty)
            IRBlock startJumpBlock = new IRBlock();

            boolean invertCondition = true;
            Jump startJump = createConditionalJump(
                    ifs.getExpression(),
                    currentifHashCode,
                    currentIfEndLabel,
                    irCode,
                    invertCondition,
                    depth,
                    debugMode);

            startJumpBlock.lineList.add(startJump);
            irCode.irBlocks.add(startJumpBlock);
            generateIRCodeHelper(ifs.getScope(), irCode, hashSimplifier,depth + 1, debugMode);

            // if there is an else node, then there must be an always jump to the end
            if (ifs.getElseNode() != null) {
                IRBlock alwaysJumpBlock = new IRBlock();
                alwaysJumpBlock.lineList.add(new Jump("jump_end_" + currentifHashCode, "always", branchEndLabel, depth));
                irCode.irBlocks.add(alwaysJumpBlock);
            }

            // end label for the current if statement
            IRBlock ifEndLabelBlock = new IRBlock();
            ifEndLabelBlock.lineList.add(new Line("if_end_" + currentifHashCode, currentIfEndLabel + ":", depth));
            irCode.irBlocks.add(ifEndLabelBlock);

            // if there is an else node
            if (ifs.getElseNode() instanceof ElseNode elseNode) {
                // if it is an else if
                if (elseNode.getIfStatementNode() instanceof IfStatementNode nestedIf) {
                    ifs = nestedIf;

                } else {
                    // if it is just an else
                    generateIRCodeHelper(elseNode.getScope(), irCode, hashSimplifier, depth + 1, debugMode);

                    IRBlock elseEndLabelBlock = new IRBlock();
                    elseEndLabelBlock.lineList.add(new Line("else_end_" + currentifHashCode, branchEndLabel + ":", depth));
                    irCode.irBlocks.add(elseEndLabelBlock);

                    break;
                }
            } else {
                // if there is no else
                break;
            }
        }
    }

    private static Jump createConditionalJump(OperationNode exp, String jumpId, String targetLabel, IRCode irCode, boolean invertCondition, int depth, boolean debugMode) throws Exception {
        String conditionalVarName = "if_cond_" + jumpId;
        IROperation conditionalOp = addOperationIRBlock(exp, irCode, conditionalVarName, depth, debugMode);
        Line lastOperation = conditionalOp.lineList.removeLast();
        Jump startJump;
        if (lastOperation instanceof Set set) {
            startJump = new Jump("jump_" + jumpId,
                    (invertCondition ? opAsMlog(KEY_OP_NOT_EQUAL) : opAsMlog(KEY_OP_EQUALS)) +
                            " " + set.getValue() + " 1",
                    targetLabel, depth);

        } else if (lastOperation instanceof BinaryOp bop) {
            startJump = new Jump("jump_" + jumpId,
                    (invertCondition ? opAsMlog(negateBooleanOperator(bop.getOp())) : opAsMlog(bop.getOp())) +
                            " " + bop.getLeft() + " " + bop.getRight(),
                    targetLabel, depth);

        } else {
            throw new Exception("Jump conditional must be BinaryOp or Set");
        }

        return startJump;
    }

    private static IROperation addOperationIRBlock(OperationNode op, IRCode irCode, String variableName, int depth, boolean debugMode) {
        IROperation opBlock = generateIROperation(op, depth, debugMode);
        irCode.irBlocks.add(opBlock);
        // change the name of the last op to the declared var name
        opBlock.lineList.getLast().setName(variableName);

        return opBlock;
    }

    public static IROperation generateIROperation(OperationNode operationNode, int depth, boolean debugMode) {
        IROperation irOperation = new IROperation();

        generateIROperationHelper(operationNode, irOperation, depth, debugMode);

        return irOperation;
    }

    private static void generateIROperationHelper(OperationNode operationNode, IROperation irOperation, int depth, boolean debugMode) {
        if (operationNode.isBinary()) {
            boolean leftConstant = operationNode.getLeftSide().isConstant();
            boolean rightConstant = operationNode.getRightSide().isConstant();

            // TODO messy
            if (leftConstant && rightConstant) {
                BinaryOp binaryOp = new BinaryOp(operationNode.nameToken.string, operationNode.getOperator(), valueAsMlog(operationNode.getLeftConstantString()), valueAsMlog(operationNode.getRightConstantString()), depth);
                irOperation.lineList.add(binaryOp);

            } else {
                BinaryOp binaryOp;

                if (!rightConstant && leftConstant) {
                    binaryOp = new BinaryOp(operationNode.nameToken.string, operationNode.getOperator(), valueAsMlog(operationNode.getLeftConstantString()), operationNode.getRightSide().nameToken.string, depth);
                    generateIROperationHelper(operationNode.getRightSide(), irOperation, depth, debugMode);

                } else if (rightConstant) {
                    binaryOp = new BinaryOp(operationNode.nameToken.string, operationNode.getOperator(), operationNode.getLeftSide().nameToken.string, valueAsMlog(operationNode.getRightConstantString()), depth);
                    generateIROperationHelper(operationNode.getLeftSide(), irOperation, depth, debugMode);

                } else {
                    binaryOp = new BinaryOp(operationNode.nameToken.string, operationNode.getOperator(), operationNode.getLeftSide().nameToken.string, operationNode.getRightSide().nameToken.string, depth);
                    generateIROperationHelper(operationNode.getLeftSide(), irOperation, depth, debugMode);
                    generateIROperationHelper(operationNode.getRightSide(), irOperation, depth, debugMode);
                }
                irOperation.lineList.add(binaryOp);
            }
        } else if (operationNode.isConstant()) {
            irOperation.lineList.add(new Set(operationNode.nameToken.string, valueAsMlog(operationNode.getConstantToken().string), depth));
        }
    }
}
