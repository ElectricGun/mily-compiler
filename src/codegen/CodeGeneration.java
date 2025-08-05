package src.codegen;

import src.codegen.blocks.*;
import src.codegen.lines.*;
import src.codegen.lines.SetLine;
import src.parsing.*;
import src.tokens.FunctionCallToken;
import src.tokens.TypedToken;

import java.util.*;

import static src.codegen.Mlogs.*;
import static src.constants.Functions.*;
import static src.constants.Keywords.*;

public class CodeGeneration {

    public static IRCode generateIRCode(EvaluatorTree evaluatorTree, boolean debugMode) throws Exception {
        IRCode irCode = new IRCode();

        Map<String, IRFunction> functionMap = new HashMap<>();
        generateIRScopeRecursive(evaluatorTree.mainBlock, irCode, functionMap, null, new HashCodeSimplifier(), 0, debugMode);

        irCode.addSingleLineBlock(new Stop(0));
        return irCode;
    }

    private static void generateIRScopeRecursive(ScopeNode scopeNode,
                                                 IRCode irCode,
                                                 Map<String, IRFunction> functionMap,
                                                 IRFunction function,
                                                 HashCodeSimplifier hashSimplifier,
                                                 int depth,
                                                 boolean debugMode) throws Exception {
        for (int i = 0; i < scopeNode.memberCount(); i++) {
            EvaluatorNode member = scopeNode.getMember(i);

            if (member instanceof FunctionCallNode fnCall) {
                generateFunctionCall(fnCall, irCode, functionMap, hashSimplifier, depth, debugMode);

            } else if (member instanceof OperationNode operationNode && operationNode.isReturnOperation()) {
                if (function == null)
                    throw new Exception("Return operation found outside a function at line " + operationNode.nameToken.line);

                addOperationIRBlock(operationNode, irCode, functionMap, function.getReturnVar(), hashSimplifier, depth, debugMode);
                irCode.addSingleLineBlock(new SetLine("@counter", function.getCallbackVar(), depth));

            } else if (member instanceof DeclarationNode declarationNode) {
                if (declarationNode.memberCount() > 0 && declarationNode.getMember(0) instanceof FunctionDeclareNode fn) {
                    generateFunctionDeclare(fn, irCode, functionMap, hashSimplifier, depth, debugMode);

                } else if (declarationNode.memberCount() > 0 && declarationNode.getMember(0) instanceof OperationNode op) {
                    addOperationIRBlock(op, irCode, functionMap, declarationNode.getVariableName(), hashSimplifier, depth, debugMode);

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
                addOperationIRBlock((OperationNode) member.getMember(0), irCode, functionMap, as.getVariableName(), hashSimplifier, depth, debugMode);

            } else if (member instanceof IfStatementNode ifs) {
                generateBranchStatement(ifs, irCode, functionMap, function, hashSimplifier, depth, debugMode);

            } else if (member instanceof WhileLoopNode whileLoop) {
                // todo unify copy pastes
                String whileHashCode = "" + hashSimplifier.simplifyHash(whileLoop.hashCode());
                String startLabelString = "while_loop_start_" + whileHashCode;

                irCode.addSingleLineBlock(new Label(startLabelString, depth));

                generateIRScopeRecursive(whileLoop.getScope(), irCode, functionMap, function, hashSimplifier, depth + 1, debugMode);

                // create loop jump
                boolean invertCondition = false;
                Jump jump = createConditionalJump(
                        whileLoop.getExpression(),
                        whileHashCode,
                        startLabelString,
                        irCode,
                        functionMap,
                        hashSimplifier,
                        invertCondition,
                        depth,
                        debugMode
                );
                irCode.addSingleLineBlock(jump);

            } else if (member instanceof ForLoopNode forLoop) {
                // todo unify copy pastes
                String forLoopHashCode = "" + hashSimplifier.simplifyHash(forLoop.hashCode());
                String startLabelString = "for_loop_start_" + forLoopHashCode;

                // initial
                VariableNode initial = forLoop.getInitial();
                if (initial == null || initial.memberCount() <= 0 || !(initial.getMember(0) instanceof OperationNode))
                    throw new Exception("Malformed for loop updater found on codegen stage");

                addOperationIRBlock((OperationNode) initial.getMember(0), irCode, functionMap, initial.getVariableName(), hashSimplifier, depth, debugMode);

                // loop start
                irCode.addSingleLineBlock(new Label(startLabelString, depth));

                // code block
                generateIRScopeRecursive(forLoop.getScope(), irCode, functionMap, function, hashSimplifier, depth + 1, debugMode);

                // updater
                AssignmentNode updater = forLoop.getUpdater();
                if (updater == null || updater.memberCount() <= 0 || !(updater.getMember(0) instanceof OperationNode))
                    throw new Exception("Malformed for loop updater found on codegen stage");

                addOperationIRBlock((OperationNode) updater.getMember(0), irCode, functionMap, updater.getVariableName(), hashSimplifier, depth, debugMode);

                // create loop jump
                boolean invertCondition = false;
                Jump jump = createConditionalJump(
                        forLoop.getCondition(),
                        forLoopHashCode,
                        startLabelString,
                        irCode,
                        functionMap,
                        hashSimplifier,
                        invertCondition,
                        depth,
                        debugMode
                );
                irCode.addSingleLineBlock(jump);
            }
        }
    }

    private static IRFunction generateFunctionCall(FunctionCallNode fnCall, IRCode irCode, Map<String, IRFunction> functionMap, HashCodeSimplifier hashCodeSimplifier, int depth, boolean debugMode) throws Exception {
        // TODO make this an interface member
        String fnKey = fnCall.getName() + "_" + fnCall.getArgCount();
        if (!functionMap.containsKey(fnKey))
            throw new Exception(String.format("IRFunction of key \"%s\" does not exist", fnKey));

        IRFunction calledFunction = functionMap.get(fnKey);

        irCode.addSingleLineBlock(new CommentLine("call: " + fnKey, depth));
        for (int a = 0; a < fnCall.getArgCount(); a++) {
            addOperationIRBlock(fnCall.getArg(a), irCode, functionMap, calledFunction.getArg(a), hashCodeSimplifier, depth, debugMode);
        }

        irCode.addSingleLineBlock(new BinaryOp(calledFunction.getCallbackVar(), KEY_OP_ADD, "@counter", "1", depth));
        irCode.addSingleLineBlock(new Jump("always", calledFunction.getCallLabel(), depth));

        return calledFunction;
    }

    private static IRFunction generateFunctionDeclare(FunctionDeclareNode fn,
                                                      IRCode irCode,
                                                      Map<String, IRFunction> functionMap,
                                                      HashCodeSimplifier hashSimplifier,
                                                      int depth,
                                                      boolean debugMode) throws Exception {
        // TODO make this an interface member
        String fnKey = fn.getName() + "_" + fn.getArgCount();

        String startJumpLabel = fnKey + "_start";
        String endJumpLabel = fnKey + "_end";
        String callbackVar = fnKey + "_callback";
        String argPrefix = fnKey + "_arg_";
        String returnVar = fnKey + "_returns";

        IRFunction irFunction = new IRFunction(startJumpLabel, callbackVar, argPrefix, returnVar);
        for (int a = 0; a < fn.getArgCount(); a++) {
            irFunction.addArg(fn.getArg(a));
        }
        functionMap.put(fnKey, irFunction);

        irCode.addSingleLineBlock(new CommentLine("function: " + fnKey, depth));
        irCode.addSingleLineBlock((new Jump("always", endJumpLabel, depth)));
        irCode.addSingleLineBlock(new Label(startJumpLabel, depth));

        generateIRScopeRecursive(fn.getScope(), irCode, functionMap, irFunction, hashSimplifier, depth + 1, debugMode);

        irCode.addSingleLineBlock((new Label(endJumpLabel, depth)));


        return irFunction;
    }

    private static void generateBranchStatement(IfStatementNode ifs, IRCode irCode, Map<String, IRFunction> functionMap, IRFunction function, HashCodeSimplifier hashSimplifier, int depth, boolean debugMode) throws Exception {
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
                    functionMap,
                    hashSimplifier,
                    invertCondition,
                    depth,
                    debugMode);

            startJumpBlock.addLine(startJump);
            irCode.irBlocks.add(startJumpBlock);
            generateIRScopeRecursive(ifs.getScope(), irCode, functionMap, function, hashSimplifier,depth + 1, debugMode);

            // if there is an else node, then there must be an always jump to the end
            if (ifs.getElseNode() != null) {
                irCode.addSingleLineBlock(new Jump("always", branchEndLabel, depth));
            }

            // end label for the current if statement
            irCode.addSingleLineBlock(new Label(currentIfEndLabel, depth));


            // if there is an else node
            if (ifs.getElseNode() instanceof ElseNode elseNode) {
                // if it is an else if
                if (elseNode.getIfStatementNode() instanceof IfStatementNode nestedIf) {
                    ifs = nestedIf;

                } else {
                    // if it is just an else
                    generateIRScopeRecursive(elseNode.getScope(), irCode, functionMap, function, hashSimplifier, depth + 1, debugMode);

//                    IRBlock elseEndLabelBlock = new IRBlock();
//                    elseEndLabelBlock.lineList.add(new Line(branchEndLabel + ":", depth));
//                    irCode.irBlocks.add(elseEndLabelBlock);

                    irCode.addSingleLineBlock(new Label(branchEndLabel, depth));

                    break;
                }
            } else {
                // if there is no else
                break;
            }
        }
    }

    private static Jump createConditionalJump(OperationNode exp, String jumpId, String targetLabel, IRCode irCode, Map<String, IRFunction> functionMap,  HashCodeSimplifier hashCodeSimplifier, boolean invertCondition, int depth, boolean debugMode) throws Exception {
        String conditionalVarName = "if_cond_" + jumpId;
        IROperation conditionalOp = addOperationIRBlock(exp, irCode, functionMap, conditionalVarName, hashCodeSimplifier, depth, debugMode);
        Line lastOperation = conditionalOp.lineList.removeLast();
        Jump startJump;
        if (lastOperation instanceof SetLine setLine) {
            startJump = new Jump(
                    (invertCondition ? opAsMlog(KEY_OP_NOT_EQUAL) : opAsMlog(KEY_OP_EQUALS)) +
                            " " + setLine.getValue() + " 1",
                    targetLabel, depth);

        } else if (lastOperation instanceof BinaryOp bop) {
            startJump = new Jump(
                    (invertCondition ? opAsMlog(negateBooleanOperator(bop.getOp())) : opAsMlog(bop.getOp())) +
                            " " + bop.getLeft() + " " + bop.getRight(),
                    targetLabel, depth);

        } else {
            throw new Exception("Jump conditional must be BinaryOp or Set");
        }

        return startJump;
    }

    private static IROperation addOperationIRBlock(OperationNode op, IRCode irCode, Map<String, IRFunction> functionMap, String variableName, HashCodeSimplifier hashCodeSimplifier, int depth, boolean debugMode) throws Exception {
        IROperation opBlock = generateIROperation(op, irCode, functionMap, hashCodeSimplifier, depth, debugMode);
        irCode.irBlocks.add(opBlock);
        // change the name of the last op to the declared var name
        Line lastLine = opBlock.lineList.getLast();
        if (lastLine instanceof VariableLine variableLine)
            variableLine.setVarName(variableName);

        return opBlock;
    }

    public static IROperation generateIROperation(OperationNode operationNode, IRCode irCode,  Map<String, IRFunction> functionMap, HashCodeSimplifier hashCodeSimplifier, int depth, boolean debugMode) throws Exception {
        IROperation irOperation = new IROperation();

        generateIROperationHelper(operationNode, irCode, functionMap, irOperation, hashCodeSimplifier, depth, debugMode);

        return irOperation;
    }

    private static void generateIROperationHelper(OperationNode operationNode, IRCode irCode, Map<String, IRFunction> functionMap, IROperation irOperation, HashCodeSimplifier hashCodeSimplifier, int depth, boolean debugMode) throws Exception {
        if (operationNode.isBinary()) {
            boolean leftConstant = operationNode.getLeftSide().isConstant();
            boolean rightConstant = operationNode.getRightSide().isConstant();

            String leftVar = "";
            String rightVar = "";

            if (leftConstant) {
                leftVar = processConstantToken(operationNode.getLeftSide().constantToken, irCode, functionMap, hashCodeSimplifier, depth, debugMode);
            }

            if (rightConstant) {
                rightVar = processConstantToken(operationNode.getRightSide().constantToken, irCode, functionMap, hashCodeSimplifier, depth, debugMode);
            }

            // TODO messy
            if (leftConstant && rightConstant) {
                BinaryOp binaryOp = new BinaryOp(operationNode.nameToken.string, operationNode.getOperator(), leftVar, rightVar, depth);
                irOperation.addLine(binaryOp);

            } else {
                BinaryOp binaryOp;

                if (!rightConstant && leftConstant) {
                    binaryOp = new BinaryOp(operationNode.nameToken.string, operationNode.getOperator(), leftVar, operationNode.getRightSide().nameToken.string, depth);
                    generateIROperationHelper(operationNode.getRightSide(), irCode, functionMap, irOperation, hashCodeSimplifier, depth, debugMode);

                } else if (rightConstant) {
                    binaryOp = new BinaryOp(operationNode.nameToken.string, operationNode.getOperator(), operationNode.getLeftSide().nameToken.string, rightVar, depth);
                    generateIROperationHelper(operationNode.getLeftSide(), irCode, functionMap, irOperation, hashCodeSimplifier, depth, debugMode);

                } else {
                    binaryOp = new BinaryOp(operationNode.nameToken.string, operationNode.getOperator(), operationNode.getLeftSide().nameToken.string, operationNode.getRightSide().nameToken.string, depth);
                    generateIROperationHelper(operationNode.getLeftSide(), irCode, functionMap, irOperation, hashCodeSimplifier, depth, debugMode);
                    generateIROperationHelper(operationNode.getRightSide(), irCode, functionMap, irOperation, hashCodeSimplifier, depth, debugMode);
                }
                irOperation.addLine(binaryOp);
            }
        } else if (operationNode.isConstant()) {
            String constantVar = processConstantToken(operationNode.constantToken, irCode, functionMap, hashCodeSimplifier, depth, debugMode);
            irOperation.addLine(new SetLine(operationNode.nameToken.string, constantVar, depth));
        }
    }

    private static String processConstantToken(TypedToken token,
                                               IRCode irCode,
                                               Map<String, IRFunction> functionMap,
                                               HashCodeSimplifier hashCodeSimplifier,
                                               int depth,
                                               boolean debugMode) throws Exception {

        if (token instanceof FunctionCallToken functionCallToken) {
            IRFunction irFunction = generateFunctionCall(functionCallToken.getNode(), irCode, functionMap, hashCodeSimplifier, depth, debugMode);
            String argOutput = irFunction.getReturnVar() + "_" + hashCodeSimplifier.simplifyHash(functionCallToken.hashCode());
            irCode.addSingleLineBlock(new SetLine(argOutput, irFunction.getReturnVar(), depth));
            return argOutput;

        } else {
            return valueAsMlog(token.string);
        }
    }
}
