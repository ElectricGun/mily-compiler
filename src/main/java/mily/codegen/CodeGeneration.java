package mily.codegen;

import mily.codegen.blocks.*;
import mily.codegen.lines.*;
import mily.parsing.*;
import mily.parsing.callable.*;
import mily.tokens.*;
import mily.utils.HashCodeSimplifier;

import java.util.*;

import static mily.codegen.Mlogs.*;
import static mily.constants.Functions.*;
import static mily.constants.Keywords.*;

public class CodeGeneration {

    public static IRCode generateIRCode(EvaluatorTree evaluatorTree, boolean debugMode) throws Exception {
        IRCode irCode = new IRCode();

        Map<String, IRFunction> irFunctionMap = new HashMap<>();
        Map<String, RawTemplateDeclareNode> templateNodeMap = new HashMap<>();
        generateIRScopeRecursive(
                evaluatorTree.mainBlock,
                irCode,
                irFunctionMap,
                templateNodeMap,
                null,
                new HashCodeSimplifier(),
                0,
                debugMode);

        irCode.addSingleLineBlock(new Stop(0));
        return irCode;
    }

    private static void generateIRScopeRecursive(ScopeNode scopeNode,
                                                 IRCode irCode,
                                                 Map<String, IRFunction> irFunctionMap,
                                                 Map<String, RawTemplateDeclareNode> templateNodeMap,
                                                 // if the block is a function block, then this is not null
                                                 IRFunction function,
                                                 HashCodeSimplifier hashSimplifier,
                                                 int depth,
                                                 boolean debugMode) throws Exception {
        for (int i = 0; i < scopeNode.memberCount(); i++) {
            EvaluatorNode member = scopeNode.getMember(i);

            if (member instanceof FunctionCallNode fnCall) {
                generateFunctionCall(fnCall, irCode, irFunctionMap, hashSimplifier, depth, debugMode);

            } else if (member instanceof OperationNode operationNode && operationNode.isReturnOperation()) {
                if (function == null)
                    throw new Exception("Return operation found outside a function at line " + operationNode.nameToken.line);

                addOperationIRBlock(operationNode, irCode, irFunctionMap, function.getReturnVar(), hashSimplifier, depth, debugMode);
                irCode.addSingleLineBlock(new SetLine("@counter", function.getCallbackVar(), depth));

            } else if (member instanceof FunctionDeclareNode fn) {
                generateFunctionDeclare(fn, irCode, irFunctionMap, templateNodeMap, hashSimplifier, depth, debugMode);

            } else if (member instanceof DeclarationNode declarationNode) {
                if (declarationNode.memberCount() > 0 && declarationNode.getMember(0) instanceof OperationNode op) {
                    addOperationIRBlock(op, irCode, irFunctionMap, declarationNode.getVariableName(), hashSimplifier, depth, debugMode);

                } else if (declarationNode.memberCount() == 0) {

                    // NOTE: null declaration does nothing
                } else {
                    throw new Exception("Malformed declaration node found on codegen stage");
                }
            } else if (member instanceof AssignmentNode as) {
                // first member of assignment nodes should always be an operator
                // otherwise throw an error
                if (member.memberCount() <= 0 || !(member.getMember(0) instanceof OperationNode))
                    throw new Exception("Malformed assignment node found on codegen stage");
                addOperationIRBlock((OperationNode) member.getMember(0), irCode, irFunctionMap, as.getVariableName(), hashSimplifier, depth, debugMode);

            } else if (member instanceof IfStatementNode ifs) {
                generateBranchStatement(ifs, irCode, irFunctionMap, templateNodeMap, function, hashSimplifier, depth, debugMode);

            } else if (member instanceof WhileLoopNode whileLoop) {
                // todo unify copy pastes
                String whileHashCode = "" + hashSimplifier.simplifyHash(whileLoop.hashCode());
                String startLabelString = "while_loop_start_" + whileHashCode;
                String endLabelString = "while_loop_end_" + whileHashCode;

                // loop start label
                irCode.addSingleLineBlock(new Label(startLabelString, depth));

                // create exit jump
                boolean invertCondition = true;
                Jump jump = createConditionalJump(
                        whileLoop.getExpression(),
                        whileHashCode,
                        endLabelString,
                        irCode,
                        irFunctionMap,
                        hashSimplifier,
                        invertCondition,
                        depth,
                        debugMode
                );
                irCode.addSingleLineBlock(jump);

                // code block
                generateIRScopeRecursive(whileLoop.getScope(), irCode, irFunctionMap, templateNodeMap, function, hashSimplifier, depth + 1, debugMode);

                // always jump
                irCode.addSingleLineBlock(new Jump("always", startLabelString, depth));

                // loop end label
                irCode.addSingleLineBlock(new Label(endLabelString, depth));

            } else if (member instanceof ForLoopNode forLoop) {
                // todo unify copy pastes
                String forLoopHashCode = "" + hashSimplifier.simplifyHash(forLoop.hashCode());
                String startLabelString = "for_loop_start_" + forLoopHashCode;
                String endLabelString = "for_loop_end_" + forLoopHashCode;

                // initial
                VariableNode initial = forLoop.getInitial();
                if (initial == null || initial.memberCount() <= 0 || !(initial.getMember(0) instanceof OperationNode))
                    throw new Exception("Malformed for loop updater found on codegen stage");

                addOperationIRBlock((OperationNode) initial.getMember(0), irCode, irFunctionMap, initial.getVariableName(), hashSimplifier, depth, debugMode);

                // loop start label
                irCode.addSingleLineBlock(new Label(startLabelString, depth));

                // create exit jump
                boolean invertCondition = true;
                Jump jump = createConditionalJump(
                        forLoop.getCondition(),
                        forLoopHashCode,
                        endLabelString,
                        irCode,
                        irFunctionMap,
                        hashSimplifier,
                        invertCondition,
                        depth,
                        debugMode
                );
                irCode.addSingleLineBlock(jump);

                // code block
                generateIRScopeRecursive(forLoop.getScope(), irCode, irFunctionMap, templateNodeMap, function, hashSimplifier, depth + 1, debugMode);

                // updater
                AssignmentNode updater = forLoop.getUpdater();
                if (updater == null || updater.memberCount() <= 0 || !(updater.getMember(0) instanceof OperationNode))
                    throw new Exception("Malformed for loop updater found on codegen stage");

                addOperationIRBlock((OperationNode) updater.getMember(0), irCode, irFunctionMap, updater.getVariableName(), hashSimplifier, depth, debugMode);

                // always jump
                irCode.addSingleLineBlock(new Jump("always", startLabelString, depth));

                // loop end label
                irCode.addSingleLineBlock(new Label(endLabelString, depth));

            } else if (member instanceof RawTemplateDeclareNode rawTemplateDeclareNode) {
                templateNodeMap.put(rawTemplateDeclareNode.getName(), rawTemplateDeclareNode);

            } else if (member instanceof RawTemplateInvoke rawTemplateInvoke) {
                IRBlock irBlock = new IRBlock();
                RawTemplateDeclareNode rawTemplateDeclareNode = templateNodeMap.get(rawTemplateInvoke.getName());

                if (rawTemplateDeclareNode == null) {
                    throw new Exception(String.format("Raw template with name \"%s\" does not exist", rawTemplateInvoke.getName()));
                }
                String formatted = rawTemplateDeclareNode.getScope().asFormatted(rawTemplateInvoke.getArgs());

                String[] lineContent = formatted.split(KEY_NEWLINE);
                for (String s : lineContent) {
                    if (!s.isEmpty())
                        irBlock.addLine(new Line(s.trim(), depth));
                }
                irCode.addSingleLineBlock(new CommentLine(rawTemplateInvoke.getName() + ":", depth));
                irCode.irBlocks.add(irBlock);
            }
        }
    }

    private static IRFunction generateFunctionCall(FunctionCallNode fnCall,
                                                   IRCode irCode, Map<String, IRFunction> functionMap,
                                                   HashCodeSimplifier hashCodeSimplifier,
                                                   int depth,
                                                   boolean debugMode) throws Exception {
        String fnKey = fnCall.getFnKey();
        if (!functionMap.containsKey(fnKey))
            throw new Exception(String.format("IRFunction of key \"%s\" does not exist", fnKey));

        IRFunction calledFunction = functionMap.get(fnKey);

        for (int a = 0; a < fnCall.getArgCount(); a++) {
            addOperationIRBlock(fnCall.getArg(a), irCode, functionMap, calledFunction.getArg(a), hashCodeSimplifier, depth, debugMode);
        }

        irCode.addSingleLineBlock(new CommentLine("call: " + fnKey, depth));
        irCode.addSingleLineBlock(new BinaryOp(calledFunction.getCallbackVar(), KEY_OP_ADD, "@counter", "1", depth));
        irCode.addSingleLineBlock(new Jump("always", calledFunction.getCallLabel(), depth));

        return calledFunction;
    }

    private static IRFunction generateFunctionDeclare(FunctionDeclareNode fn,
                                                      IRCode irCode,
                                                      Map<String, IRFunction> functionMap,
                                                      Map<String, RawTemplateDeclareNode> templateNodeMap,
                                                      HashCodeSimplifier hashSimplifier,
                                                      int depth,
                                                      boolean debugMode) throws Exception {
        String fnKey = fn.getFnKey();
        String startJumpLabel = fnKey + "_start";
        String endJumpLabel = fnKey + "_end";
        String callbackVar = fnKey + "_callback";
        String argPrefix = fnKey + "_arg_";
        String returnVar = fnKey + "_returns";

        IRFunction irFunction = new IRFunction(fn, startJumpLabel, callbackVar, argPrefix, returnVar);
        for (int a = 0; a < fn.getArgCount(); a++) {
            irFunction.addArg(fn.getArgType(a), fn.getArg(a));
        }
        functionMap.put(fnKey, irFunction);

        irCode.addSingleLineBlock(new CommentLine("function: " + fnKey, depth));
        irCode.addSingleLineBlock((new Jump("always", endJumpLabel, depth)));
        irCode.addSingleLineBlock(new Label(startJumpLabel, depth));

        generateIRScopeRecursive(fn.getScope(), irCode, functionMap, templateNodeMap, irFunction, hashSimplifier, depth + 1, debugMode);

        irCode.addSingleLineBlock((new Label(endJumpLabel, depth)));


        return irFunction;
    }

    private static void generateBranchStatement(IfStatementNode ifs,
                                                IRCode irCode, Map<String, IRFunction> functionMap,
                                                Map<String, RawTemplateDeclareNode> templateNodeMap,
                                                IRFunction function, HashCodeSimplifier hashSimplifier,
                                                int depth,
                                                boolean debugMode) throws Exception {
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
            generateIRScopeRecursive(ifs.getScope(), irCode, functionMap, templateNodeMap, function, hashSimplifier, depth + 1, debugMode);

            // if there is an else node, then there must be an always jump to the end
            if (ifs.getElseNode() != null) {
                irCode.addSingleLineBlock(new Jump("always", branchEndLabel, depth));
            }

            // end label for the current if statement
            irCode.addSingleLineBlock(new Label(currentIfEndLabel, depth));

            // if there is an else node
//            if (ifs.getElseNode() instanceof ElseNode elseNode) {
            if (ifs.getElseNode() != null) {
                ElseNode elseNode = ifs.getElseNode();
                // if it is an else if
//                if (elseNode.getIfStatementNode() instanceof IfStatementNode nestedIf) {
                if (elseNode.getIfStatementNode() != null) {
                    ifs = elseNode.getIfStatementNode();

                } else {
                    // if it is just an else
                    generateIRScopeRecursive(elseNode.getScope(), irCode, functionMap, templateNodeMap, function, hashSimplifier, depth + 1, debugMode);

                    irCode.addSingleLineBlock(new Label(branchEndLabel, depth));

                    break;
                }
            } else {
                // if there is no else
                break;
            }
        }
    }

    private static Jump createConditionalJump(OperationNode exp,
                                              String jumpId, String targetLabel,
                                              IRCode irCode,
                                              Map<String, IRFunction> functionMap,
                                              HashCodeSimplifier hashCodeSimplifier,
                                              boolean invertCondition,
                                              int depth,
                                              boolean debugMode) throws Exception {
        String conditionalVarName = "if_cond_" + jumpId;
        IROperation conditionalOp = addOperationIRBlock(exp, irCode, functionMap, conditionalVarName, hashCodeSimplifier, depth, debugMode);
//        Line lastOperation = conditionalOp.lineList.removeLast();
        Line lastOperation = conditionalOp.lineList.remove(conditionalOp.lineList.size() - 1);

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

    private static IROperation addOperationIRBlock(OperationNode op,
                                                   IRCode irCode,
                                                   Map<String, IRFunction> functionMap,
                                                   String variableName,
                                                   HashCodeSimplifier hashCodeSimplifier,
                                                   int depth,
                                                   boolean debugMode) throws Exception {
        IROperation opBlock = generateIROperation(op, irCode, functionMap, hashCodeSimplifier, depth, debugMode);
        irCode.irBlocks.add(opBlock);
        // change the name of the last op to the declared var name
//        Line lastLine = opBlock.lineList.getLast();
        Line lastLine = opBlock.lineList.get(opBlock.lineList.size() - 1);

        if (lastLine instanceof VariableLine variableLine)
            variableLine.setVarName(variableName);

        return opBlock;
    }

    private static IROperation generateIROperation(OperationNode operationNode,
                                                   IRCode irCode,
                                                   Map<String, IRFunction> functionMap,
                                                   HashCodeSimplifier hashCodeSimplifier,
                                                   int depth,
                                                   boolean debugMode) throws Exception {
        IROperation irOperation = new IROperation();

        generateIROperationHelper(operationNode, irCode, functionMap, irOperation, hashCodeSimplifier, depth, debugMode);

        return irOperation;
    }

    private static void generateIROperationHelper(OperationNode operationNode,
                                                  IRCode irCode,
                                                  Map<String, IRFunction> functionMap,
                                                  IROperation irOperation,
                                                  HashCodeSimplifier hashCodeSimplifier,
                                                  int depth,
                                                  boolean debugMode) throws Exception {
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
