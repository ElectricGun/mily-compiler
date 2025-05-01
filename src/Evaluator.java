package src;

import java.util.ArrayList;
import java.util.List;

import static src.Vars.*;
public class Evaluator {
    public ScopeEvaluator mainBlock = new ScopeEvaluator("__MAIN__", 0);

    public EvaluatorNode begin(List<String> tokenList) {
        mainBlock.evaluate(tokenList, this);
        return mainBlock;
    }

    public static void printRecursive(EvaluatorNode node) {
        printRecursiveHelper(node, 0);
    }
    protected static void printRecursiveHelper(EvaluatorNode node, int depth) {
        String prefix = depth > 0 ? "| ".repeat(depth) : "";
        System.out.println(prefix + node);
        depth++;
        for (EvaluatorNode member : node.members) {
            printRecursiveHelper(member, depth);
        }
    }

    public static class EvaluatorNode {
        int depth;
        String name;
        String buffer = "";
        List<EvaluatorNode> members = new ArrayList<>();

        public EvaluatorNode(String name, int depth) {
            this.name = name;
            this.depth = depth;
        }
        protected EvaluatorNode evaluator(List<String> tokenList, Evaluator evaluator) throws Exception {
            throw new UnsupportedOperationException("This method is not yet implemented.");
        }

        public EvaluatorNode evaluate(List<String> tokenList, Evaluator evaluator) {
            try {
                return evaluator(tokenList, evaluator);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
                return null;
            }
        }
    }

    public static class ScopeEvaluator extends EvaluatorNode {
        // if block starts with '{'
        boolean needsClosing = false;
        FunctionEvaluator functionBlock = null;
        public ScopeEvaluator(String name, int depth) {
            super(name, depth);
        }
        public ScopeEvaluator(String name, int depth, boolean needsClosing, FunctionEvaluator functionBlock) {
            super(name, depth);
            this.needsClosing = needsClosing;
            this.functionBlock = functionBlock;
        }
        @Override
        protected EvaluatorNode evaluator(List<String> tokenList, Evaluator evaluator) throws Exception {
            String indent = " ".repeat(depth);

            boolean isInitialized = false;
            String previousElementToken = "";

            System.out.printf(indent + "Parsing Block %s:%n", name);

            while (!tokenList.isEmpty()) {
                String token = tokenList.removeFirst();

                System.out.printf(indent + "scope\t:\t%s\t:\t%s%n",name, token);

                buffer += token;

                // evaluate punctuations
                if (token.length() == 1 && isPunctuation(token.charAt(0))) {
                    char c = token.charAt(0);
                    if (isWhiteSpace(c)) {
                        continue;
                    }
                    if (isPunctuation(c) && !isInitialized)
                        throw new Exception("Illegal punctuation on scope %s \"%s\"".formatted(name, c));

                    // expect new function '(', or equals '='
                    // FUNCTION DECLARATION
                    if (CHAR_BRACKET_OPEN == c) {
                        System.out.printf(indent + "Creating new function \"%s\"%n", previousElementToken);
                        EvaluatorNode node = new FunctionEvaluator(previousElementToken, depth + 1).evaluate(tokenList, evaluator);
                        members.add(node);
                    }
                    else if (needsClosing && CHAR_CURLY_CLOSE == c) {
                        System.out.printf("Created scope \"%s\"%n", name);
                        return this;
                    }
                    else {
                        throw new Exception("Unexpected token on scope %s: \"%s\"".formatted(name, c));
                    }
                }
                // evaluate operators
                else if (isOperator(token)) {
                    throw new Exception("Unexpected operator on scope %s: \"%s\"".formatted(name, token));
                // evaluate the rest
                } else {

                    // RETURN STATEMENT FOR FUNCTIONS
                    if (functionBlock != null && token.equals(KEYWORD_RETURN)) {
                        OperationEvaluator returnOp = new ReturnOperationEvaluator(name+"_return", depth + 1);
                        members.add(returnOp);
                        returnOp.evaluate(tokenList, evaluator);
                    }
                    // VARIABLE DECLARATION
                    else if (previousElementToken.equals(KEYWORD_LET)) {
                        EvaluatorNode node = new DeclarationEvaluator(token, depth + 1).evaluate(tokenList, evaluator);
                        members.add(node);
                    }
                    previousElementToken = token;
                    isInitialized = true;
                }
            }
            if (needsClosing) {
                throw new Exception("Scope \"%s\" is unclosed".formatted(name));
            }
            return this;
        }

        @Override
        public String toString() {
            return ((functionBlock != null ? "function " : "")  + "scope : " + name);
        }
    }

    public static class FunctionEvaluator extends EvaluatorNode {
        List<String> argumentNames = new ArrayList<>();
        ScopeEvaluator scope;
        public FunctionEvaluator(String name, int depth) {
            super(name, depth);
        }

        @Override
        protected EvaluatorNode evaluator(List<String> tokenList, Evaluator evaluator) throws Exception {
            String indent = " ".repeat(depth);

            boolean isInitialized = false;
            boolean functionDeclared = false;
            boolean argumentWanted = false;

            System.out.printf(indent + "Parsing Function %s:%n", name);

            while (!tokenList.isEmpty()) {
                String token = tokenList.removeFirst();

                System.out.printf(indent + "function\t:\t%s\t:\t%s%n",name, token);

                buffer += token;

                // evaluate punctuations
                if (token.length() == 1 && isPunctuation(token.charAt(0))) {
                    char c = token.charAt(0);
                    if (isWhiteSpace(c)) {
                        continue;
                    }

                    if (argumentWanted) {
                        throw new Exception("Expecting an argument at function declaration %s: \"%s\"".formatted(name, c));
                    } else if (CHAR_BRACKET_CLOSE == c) {
                        functionDeclared = true;
                    } else if (CHAR_COMMA == c) {
                        argumentWanted = true;
                    }
                    else if (functionDeclared && CHAR_CURLY_OPEN == c) {
                        System.out.printf(indent + "Function header \"%s(%s)\" created%n", name, String.join(", ", argumentNames));
                        scope = new ScopeEvaluator(name, depth + 1, true, this);
                        members.add(scope.evaluate(tokenList, evaluator));
                        return this;
                    } else {
                        throw new Exception("Unexpected token at function declaration %s: \"%s\"".formatted(name, c));
                    }
                }
                // evaluate operators
                else if (isOperator(token)) {
                    throw new Exception("Unexpected operator at function declaration %s: \"%s\"".formatted(name, token));
                }
                // evaluate the rest
                else {
                      if (!isInitialized || argumentWanted) {
                          argumentNames.add(token);
                          argumentWanted = false;
                          System.out.printf("Added argument %s%n", token);
                        }
                    isInitialized = true;
                }
            }
            return null;
        }
        @Override
        public String toString() {
            return "function : %s : %s(%s)".formatted(name, name, String.join(", ", argumentNames));
        }
    }

    public static class DeclarationEvaluator extends EvaluatorNode {

        public DeclarationEvaluator(String name, int depth) {
            super(name, depth);
        }

        @Override
        protected EvaluatorNode evaluator(List<String> tokenList, Evaluator evaluator) throws Exception {
            String indent = " ".repeat(depth);

            System.out.printf("Parsing Variable Declaration %s:%n", name);

            while (!tokenList.isEmpty()) {
                String token = tokenList.removeFirst();

                System.out.printf(indent + "declaration :  %s : %s%n",name, token);

                // evaluate punctuations
                if (token.length() == 1 && isPunctuation(token.charAt(0))) {
                    char c = token.charAt(0);
                    if (isWhiteSpace(c)) {
                        continue;
                    }

                    throw new Exception("Unexpected punctuation on variable declaration %s: \"%s\"".formatted(name, c));
                }
                // evaluate operators
                else if (isOperator(token)) {
                    // check for equal sign
                    if (token.equals(OP_EQUALS)) {
                        OperationEvaluator operationEvaluator = new OperationEvaluator("op_"+name, depth + 1);
                        operationEvaluator.evaluate(tokenList, evaluator);
                        members.add(operationEvaluator);
                        return this;
                    }
                    else {
                        throw new Exception("Missing '=' sign %s: \"%s\"".formatted(name, token));
                    }
                }
                // evaluate the rest
                else {

                }
            }
            return null;
        }

        @Override
        public String toString() {
            return "declare %s".formatted(name);
        }
    }

    public static class OperationEvaluator extends EvaluatorNode {
        String type = OP_CONSTANT;
        String constantValue = "";
        EvaluatorNode baseOperand = null;
        EvaluatorNode targetOperand = null;
        List<String> operationTokens = new ArrayList<>();

        public OperationEvaluator(String name, int depth) {
            super(name, depth);
        }
        @Override
        protected EvaluatorNode evaluator(List<String> tokenList, Evaluator evaluator) throws Exception {
            String indent = " ".repeat(depth);

            System.out.printf(indent + "Parsing Operation Declaration %s:%n", name);

            while (!tokenList.isEmpty()) {
                String token = tokenList.removeFirst();

                System.out.printf(indent + "operation : %s : %s%n",name, token);

                // evaluate punctuations
                if (token.length() == 1 && isPunctuation(token.charAt(0))) {
                    char c = token.charAt(0);
                    if (isWhiteSpace(c)) {
                        continue;
                    }

                    if (CHAR_SEMICOLON == c) {
                        System.out.printf(indent + "operation : %s tokens : %s%n",name, operationTokens);
                        int[] orders = new int[operationTokens.size()];

                        // if smallest value is -1 then its probably a constant or something is very wrong
                        int smallestIndex = -1;
                        int smallestValue = -1;
                        for (int i = 0; i< orders.length; i++) {
                            orders[i] = operationOrder(operationTokens.get(i));

                            if (orders[i] != -1 &&
                                    (smallestValue == -1 || orders[i] > smallestValue)) {
                                smallestValue = orders[i];
                                smallestIndex = i;
                            }
                        }
                        if (smallestValue != -1) {
                            type = operationTokens.get(smallestIndex);
                        }
                        // if it only has -1 then its a constant
                        else if (orders.length == 1) {
                            constantValue = operationTokens.removeFirst();
                            return this;
                        } else {
                            throw new Exception("Invalid operation %s on token \"%s\"".formatted(name, token));
                        }

                        List<String> left = new ArrayList<>(operationTokens.subList(0, smallestIndex));
                        List<String> right = new ArrayList<>(operationTokens.subList(smallestIndex + 1, operationTokens.size()));

                        left.add(";");
                        right.add(";");


                        baseOperand = new OperationEvaluator("l_" + name, depth + 1);
                        targetOperand = new OperationEvaluator("r_" + name, depth + 1);

                        baseOperand.evaluate(left, evaluator);
                        targetOperand.evaluate(right, evaluator);

                        members.add(baseOperand);
                        members.add(targetOperand);

                        return this;
                    } else {
                        throw new Exception("Unexpected token on operation %s, \"%s\"".formatted(name, token));
                    }
                }
                // evaluate operators
                else if (isOperator(token)) {
                    // entire operations are evaluated after a semicolon is detected, so this isn't really used
                    operationTokens.add(token);
                }
                // evaluate the rest
                else {
                    operationTokens.add(token);
                }
            }
            return this;
        }

        @Override
        public String toString() {
            return "%s %s".formatted( baseOperand == null ? constantValue : "operator", type.equals(OP_CONSTANT) ? "" : type);
        }
    }

    public static class ReturnOperationEvaluator extends OperationEvaluator {
        public ReturnOperationEvaluator(String name, int depth) {
            super(name, depth);
        }

        @Override
        public String toString() {
            return "%s %s".formatted( baseOperand == null ? "returns " + constantValue : "returns operator", type.equals(OP_CONSTANT) ? "" : type);
        }
    }

}

