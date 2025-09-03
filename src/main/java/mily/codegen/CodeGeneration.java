package mily.codegen;

import mily.codegen.blocks.*;
import mily.codegen.lines.*;
import mily.parsing.*;
import mily.parsing.callables.*;
import mily.parsing.invokes.*;
import mily.structures.structs.*;
import mily.tokens.*;
import mily.utils.*;

import java.util.*;

import static mily.codegen.Mlogs.*;
import static mily.constants.Functions.*;
import static mily.constants.Keywords.*;

public class CodeGeneration {

    protected static String pointerVariable = "mem_pointer";

    public static IRCode generateIRCode(EvaluatorTree evaluatorTree, boolean generateComments, boolean debugMode) throws Exception {
        IRCodeConfig irCodeConfig = new IRCodeConfig();

        irCodeConfig.irCode = new IRCode();
        irCodeConfig.irFunctionMap = new HashMap<>();
        irCodeConfig.callableNodeMap = new HashMap<>();
        irCodeConfig.hashCodeSimplifier = new HashCodeSimplifier();
        irCodeConfig.declarationMap = new HashMap<>();
        irCodeConfig.generateComments = generateComments;
        irCodeConfig.debugMode = debugMode;

        irCodeConfig.irCode.addSingleLineBlock(new SetLine(pointerVariable, "0", 0));

        generateIRScopeRecursive(
                irCodeConfig,
                evaluatorTree.mainBlock,
                null,
                0
        );

        irCodeConfig.irCode.addSingleLineBlock(new Stop(0));
        return irCodeConfig.irCode;
    }

    private static void generateIRScopeRecursive(IRCodeConfig irCodeConfig, ScopeNode scopeNode, /*for function scopes*/IRFunction function, int depth) throws Exception {
        for (int i = 0; i < scopeNode.memberCount(); i++) {
            EvaluatorNode member = scopeNode.getMember(i);

            if (member instanceof CallerNode fnCall) {
                CallableSignature sig = fnCall.signature();
                CallableNode callable = irCodeConfig.callableNodeMap.get(sig);

                if (callable instanceof FunctionDeclareNode) {
                    generateFunctionCall(irCodeConfig, fnCall, depth);

                } else if (callable instanceof RawTemplateDeclareNode) {
                    generateRawTemplateInvoke(irCodeConfig, fnCall, null, depth);
                }

//            } else if (member instanceof RawTemplateInvoke rawTemplateInvoke) {
//                generateRawTemplateInvoke(irCodeConfig, rawTemplateInvoke, null, depth);

            } else if (member instanceof OperationNode operationNode && operationNode.isReturnOperation()) {
                if (function == null)
                    throw new Exception("Return operation found outside a function at line " + operationNode.nameToken.line);

                if (!operationNode.getConstantToken().getType().equals(KEY_DATA_VOID)) {
                    addOperationIRBlock(irCodeConfig, operationNode, function.getReturnVar(), depth);
                }
                irCodeConfig.irCode.addSingleLineBlock(new SetLine("@counter", function.getCallbackVar(), depth));

            } else if (member instanceof FunctionDeclareNode fn) {
                generateFunctionDeclare(fn, irCodeConfig, depth);

            } else if (member instanceof DeclarationNode declarationNode) {
                irCodeConfig.declarationMap.put(declarationNode.getName(), declarationNode);

                if (declarationNode.memberCount() > 0 && declarationNode.getMember(0) instanceof OperationNode op) {
                    IROperation declaredOp = addOperationIRBlock(irCodeConfig, op, declarationNode.getName(), depth);

                    if (declarationNode.getType().typeString.equals(KEY_DATA_PTR.typeString)) {
                        Line lastLine = declaredOp.lineList.get(declaredOp.lineList.size() - 1);


                        if (lastLine instanceof VariableLine variableLine) {
                            // if it is a set, just replace it with a write line
                            String oldVarName = variableLine.getVarName();
                            String ptrValueName = "value_" + oldVarName;

                            if (variableLine instanceof SetLine setLine) {
                                declaredOp.lineList.remove(declaredOp.lineList.size() - 1);
                                declaredOp.lineList.add(new WriteLine(setLine.getValue(), "cell1", pointerVariable, depth));
                            } else {
                                // if its an op, overwrite the var name of the evaluated value
                                variableLine.setVarName(ptrValueName);
                                declaredOp.lineList.add(new WriteLine(variableLine.getVarName(), "cell1", pointerVariable, depth));
                            }
                            declaredOp.lineList.add(new SetLine(oldVarName, pointerVariable, depth));
                            declaredOp.lineList.add(new BinaryOp(pointerVariable, KEY_OP_ADD, pointerVariable, "1", depth));

                        } else {
                            throw new Exception("Why is the ptr var declaration line not a VariableLine?");
                        }
                    }

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
                addOperationIRBlock(irCodeConfig, (OperationNode) member.getMember(0), as.getName(), depth);

            } else if (member instanceof IfStatementNode ifs) {
                generateBranchStatement(ifs, irCodeConfig, function, depth);

            } else if (member instanceof WhileLoopNode whileLoop) {
                // todo unify copy pastes
                String whileHashCode = "" + irCodeConfig.hashCodeSimplifier.simplifyHash(whileLoop.hashCode());
                String startLabelString = "while_loop_start_" + whileHashCode;
                String endLabelString = "while_loop_end_" + whileHashCode;

                // loop start label
                irCodeConfig.irCode.addSingleLineBlock(new Label(startLabelString, depth));

                // create exit jump
                boolean invertCondition = true;
                Jump jump = createConditionalJump(
                        irCodeConfig,
                        whileLoop.getExpression(),
                        whileHashCode,
                        endLabelString,
                        invertCondition,
                        depth
                );
                irCodeConfig.irCode.addSingleLineBlock(jump);

                // code block
                generateIRScopeRecursive(irCodeConfig, whileLoop.getScope(), function, depth + 1);

                // always jump
                irCodeConfig.irCode.addSingleLineBlock(new Jump("always", startLabelString, depth));

                // loop end label
                irCodeConfig.irCode.addSingleLineBlock(new Label(endLabelString, depth));

            } else if (member instanceof ForLoopNode forLoop) {
                // todo unify copy pastes
                String forLoopHashCode = "" + irCodeConfig.hashCodeSimplifier.simplifyHash(forLoop.hashCode());
                String startLabelString = "for_loop_start_" + forLoopHashCode;
                String endLabelString = "for_loop_end_" + forLoopHashCode;

                // initial
                VariableNode initial = forLoop.getInitial();
                if (initial == null || initial.memberCount() <= 0 || !(initial.getMember(0) instanceof OperationNode))
                    throw new Exception("Malformed for loop updater found on codegen stage");

                addOperationIRBlock(irCodeConfig, (OperationNode) initial.getMember(0), initial.getName(), depth);

                // loop start label
                irCodeConfig.irCode.addSingleLineBlock(new Label(startLabelString, depth));

                // create exit jump
                boolean invertCondition = true;
                Jump jump = createConditionalJump(
                        irCodeConfig,
                        forLoop.getCondition(),
                        forLoopHashCode,
                        endLabelString,
                        invertCondition,
                        depth
                );
                irCodeConfig.irCode.addSingleLineBlock(jump);

                // code block
                generateIRScopeRecursive(irCodeConfig, forLoop.getScope(), function, depth + 1);

                // updater
                AssignmentNode updater = forLoop.getUpdater();
                if (updater == null || updater.memberCount() <= 0 || !(updater.getMember(0) instanceof OperationNode))
                    throw new Exception("Malformed for loop updater found on codegen stage");

                addOperationIRBlock(irCodeConfig, (OperationNode) updater.getMember(0), updater.getName(), depth);

                // always jump
                irCodeConfig.irCode.addSingleLineBlock(new Jump("always", startLabelString, depth));

                // loop end label
                irCodeConfig.irCode.addSingleLineBlock(new Label(endLabelString, depth));

            } else if (member instanceof RawTemplateDeclareNode rawTemplateDeclareNode) {
                irCodeConfig.callableNodeMap.put(rawTemplateDeclareNode.signature(), rawTemplateDeclareNode);
            }
        }
    }

    private static void generateRawTemplateInvoke(IRCodeConfig irCodeConfig, CallerNode callerNode, String outputVariable, int depth) throws Exception {
        IRBlock irBlock = new IRBlock();
        RawTemplateDeclareNode rawTemplateDeclareNode = (RawTemplateDeclareNode) irCodeConfig.callableNodeMap.get(callerNode.signature());

        if (rawTemplateDeclareNode == null) {
            throw new Exception(String.format("Raw template with name \"%s\" does not exist", callerNode.getName()));
        }
        List<OperationNode> argOps = callerNode.getArgs();

        List<String> argNames = new ArrayList<>();
        for (OperationNode argOp : argOps) {
            IROperation opBlock = generateIROperation(irCodeConfig, argOp, depth);
            irCodeConfig.irCode.irBlocks.add(opBlock);

            Line lastLine = opBlock.lineList.get(opBlock.lineList.size() - 1);

            if (lastLine instanceof BinaryOp binaryOp) {
                argNames.add(binaryOp.getVarName());

            } else if (lastLine instanceof SetLine setLine) {
                // remove because we don't need this
                opBlock.lineList.remove(opBlock.lineList.size() - 1);
                argNames.add(setLine.getValue());

            } else {
                throw new Exception("Expected a BinaryOp or SetLine in RawTemplateInvoke on codegen stage, got \"" + lastLine.getClass() + "\" instead");
            }
        }
        String formatted;
        if (outputVariable == null) {
            formatted = rawTemplateDeclareNode.scopeAsFormatted(argNames);

        } else {
            formatted = rawTemplateDeclareNode.scopeAsFormatted(argNames, outputVariable);
        }

        String[] lineContent = formatted.split(KEY_NEWLINE);
        for (String s : lineContent) {
            if (!s.isEmpty())
                irBlock.addLine(new Line(s.trim(), depth));
        }
        if (irCodeConfig.generateComments)
            irCodeConfig.irCode.addSingleLineBlock(new CommentLine(callerNode.getName() + ":", depth));
        irCodeConfig.irCode.irBlocks.add(irBlock);
    }

    private static IRFunction generateFunctionCall(IRCodeConfig irCodeConfig, CallerNode fnCall, int depth) throws Exception {
        CallableSignature fnKey = fnCall.signature();
        FunctionDeclareNode fn = (FunctionDeclareNode) irCodeConfig.callableNodeMap.get(fnKey);
        if (!irCodeConfig.irFunctionMap.containsKey(fn))
            throw new Exception(String.format("IRFunction of key \"%s\" does not exist", fnKey));

        IRFunction calledFunction = irCodeConfig.irFunctionMap.get(fn);

        for (int a = 0; a < fnCall.getArgCount(); a++) {
            addOperationIRBlock(irCodeConfig, fnCall.getArg(a), calledFunction.getArg(a), depth);
        }

        if (irCodeConfig.generateComments)
            irCodeConfig.irCode.addSingleLineBlock(new CommentLine("call: " + fnKey, depth));
        irCodeConfig.irCode.addSingleLineBlock(new BinaryOp(calledFunction.getCallbackVar(), KEY_OP_ADD, "@counter", "1", depth));
        irCodeConfig.irCode.addSingleLineBlock(new Jump("always", calledFunction.getCallLabel(), depth));

        return calledFunction;
    }

    private static void generateFunctionDeclare(FunctionDeclareNode fn, IRCodeConfig irCodeConfig, int depth) throws Exception {
        CallableSignature fnKey = fn.signature();
        String startJumpLabel = fnKey + "_start";
        String endJumpLabel = fnKey + "_end";
        String callbackVar = fnKey + "_callback";
        String argPrefix = fnKey + "_arg_";
        String returnVar = fnKey + "_returns";

        IRFunction irFunction = new IRFunction(fn, startJumpLabel, callbackVar, argPrefix, returnVar);
        for (int a = 0; a < fn.getArgCount(); a++) {
            irFunction.addArg(fn.getArgType(a), fn.getArg(a));
        }
        irCodeConfig.irFunctionMap.put(fn, irFunction);
        irCodeConfig.callableNodeMap.put(fnKey, fn);

        if (irCodeConfig.generateComments)
            irCodeConfig.irCode.addSingleLineBlock(new CommentLine("function: " + fnKey, depth));
        irCodeConfig.irCode.addSingleLineBlock((new Jump("always", endJumpLabel, depth)));
        irCodeConfig.irCode.addSingleLineBlock(new Label(startJumpLabel, depth));

        generateIRScopeRecursive(irCodeConfig, fn.getScope(), irFunction, depth + 1);

        irCodeConfig.irCode.addSingleLineBlock((new Label(endJumpLabel, depth)));
    }

    private static void generateBranchStatement(IfStatementNode ifs, IRCodeConfig irCodeConfig, IRFunction function, int depth) throws Exception {
        String branchEndLabel = "branch_end_" + irCodeConfig.hashCodeSimplifier.simplifyHash(ifs.hashCode());

        // while loop to go through all the else blocks
        // todo change the true to an actual conditional
        while (true) {
            // loop repeats with a new ifs object
            String currentifHashCode = "" + irCodeConfig.hashCodeSimplifier.simplifyHash(ifs.hashCode());
            String currentIfEndLabel = ifs.nameToken + "_" + currentifHashCode;

            // if current if statement has no else block, just jump to the end
            if (ifs.getElseNode() == null)
                currentIfEndLabel = branchEndLabel;

            // create jump statement (doesn't look pretty)
            IRBlock startJumpBlock = new IRBlock();

            boolean invertCondition = true;
            Jump startJump = createConditionalJump(
                    irCodeConfig,
                    ifs.getExpression(),
                    currentifHashCode,
                    currentIfEndLabel,
                    invertCondition,
                    depth
            );

            startJumpBlock.addLine(startJump);
            irCodeConfig.irCode.irBlocks.add(startJumpBlock);
            generateIRScopeRecursive(irCodeConfig, ifs.getScope(), function, depth + 1);

            // if there is an else node, then there must be an always jump to the end
            if (ifs.getElseNode() != null) {
                irCodeConfig.irCode.addSingleLineBlock(new Jump("always", branchEndLabel, depth));
            }

            // end label for the current if statement
            irCodeConfig.irCode.addSingleLineBlock(new Label(currentIfEndLabel, depth));

            // if there is an else node
            if (ifs.getElseNode() != null) {
                ElseNode elseNode = ifs.getElseNode();
                // if it is an else if
                if (elseNode.getIfStatementNode() != null) {
                    ifs = elseNode.getIfStatementNode();

                } else {
                    // if it is just an else
                    generateIRScopeRecursive(irCodeConfig, elseNode.getScope(), function, depth + 1);
                    irCodeConfig.irCode.addSingleLineBlock(new Label(branchEndLabel, depth));
                    break;
                }
            } else {
                // if there is no else
                break;
            }
        }
    }

    private static Jump createConditionalJump(IRCodeConfig irCodeConfig, OperationNode exp, String jumpId, String targetLabel, boolean invertCondition, int depth) throws Exception {
        String conditionalVarName = "if_cond_" + jumpId;
        IROperation conditionalOp = addOperationIRBlock(irCodeConfig, exp, conditionalVarName, depth);
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

    private static IROperation addOperationIRBlock(IRCodeConfig irCodeConfig, OperationNode op, String variableName, int depth) throws Exception {
        IROperation opBlock = generateIROperation(irCodeConfig, op, depth);
        irCodeConfig.irCode.irBlocks.add(opBlock);
        // change the name of the last op to the declared var name
        Line lastLine = opBlock.lineList.get(opBlock.lineList.size() - 1);

        if (lastLine instanceof VariableLine variableLine)
            variableLine.setVarName(variableName);

        return opBlock;
    }

    private static IROperation generateIROperation(IRCodeConfig irCodeConfig,
                                                   OperationNode operationNode,
                                                   int depth) throws Exception {
        IROperation irOperation = new IROperation();

        generateIROperationHelper(irCodeConfig, operationNode, irOperation, depth);

        return irOperation;
    }

    private static void generateIROperationHelper(IRCodeConfig irCodeConfig, OperationNode operationNode, IROperation irOperation, int depth) throws Exception {
        if (operationNode.isBinary()) {
            boolean leftConstant = operationNode.getLeftSide().isConstant();
            boolean rightConstant = operationNode.getRightSide().isConstant();

            String leftVar = "";
            String rightVar = "";

            if (leftConstant) {
                leftVar = processConstantToken(irCodeConfig, operationNode.getLeftSide().getConstantToken(), depth);
            }

            if (rightConstant) {
                rightVar = processConstantToken(irCodeConfig, operationNode.getRightSide().getConstantToken(), depth);
            }

            // TODO messy
            if (leftConstant && rightConstant) {
                BinaryOp binaryOp = new BinaryOp(operationNode.nameToken.string, operationNode.getOperator(), leftVar, rightVar, depth);
                irOperation.addLine(binaryOp);

            } else {
                BinaryOp binaryOp;

                if (!rightConstant && leftConstant) {
                    binaryOp = new BinaryOp(operationNode.nameToken.string, operationNode.getOperator(), leftVar, operationNode.getRightSide().nameToken.string, depth);
                    generateIROperationHelper(irCodeConfig, operationNode.getRightSide(), irOperation, depth);

                } else if (rightConstant) {
                    binaryOp = new BinaryOp(operationNode.nameToken.string, operationNode.getOperator(), operationNode.getLeftSide().nameToken.string, rightVar, depth);
                    generateIROperationHelper(irCodeConfig, operationNode.getLeftSide(), irOperation, depth);

                } else {
                    binaryOp = new BinaryOp(operationNode.nameToken.string, operationNode.getOperator(), operationNode.getLeftSide().nameToken.string, operationNode.getRightSide().nameToken.string, depth);
                    generateIROperationHelper(irCodeConfig, operationNode.getLeftSide(), irOperation, depth);
                    generateIROperationHelper(irCodeConfig, operationNode.getRightSide(), irOperation, depth);
                }
                irOperation.addLine(binaryOp);
            }
        } else if (operationNode.isConstant()) {
            String constantVar = processConstantToken(irCodeConfig, operationNode.getConstantToken(), depth);
            irOperation.addLine(new SetLine(operationNode.nameToken.string, constantVar, depth));
        }
    }

    private static String processConstantToken(IRCodeConfig irCodeConfig, TypedToken token, int depth) throws Exception {
        if (token instanceof CallerNodeToken callerNodeToken) {
            CallerNode callerNode = callerNodeToken.getNode();
            int callerHashCode = irCodeConfig.hashCodeSimplifier.simplifyHash(callerNodeToken.hashCode());
            CallableNode callableNode = irCodeConfig.callableNodeMap.get(callerNode.signature());

            if (callableNode instanceof FunctionDeclareNode) {
                IRFunction irFunction = generateFunctionCall(irCodeConfig, callerNode, depth);
                String argOutput = irFunction.getReturnVar() + "_" + callerHashCode;
                irCodeConfig.irCode.addSingleLineBlock(new SetLine(argOutput, irFunction.getReturnVar(), depth));
                return argOutput;

            } else if (callableNode instanceof RawTemplateDeclareNode) {
                String argOutput = token.string + "_" + callerHashCode;
                generateRawTemplateInvoke(irCodeConfig, callerNode, argOutput, depth);
                return argOutput;

            } else {
                throw new IllegalArgumentException("Unknown node in CallerNodeToken");
            }

        } else {
            return tokenAsMlog(token);
        }
    }
}
