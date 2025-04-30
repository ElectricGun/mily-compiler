package src;

import java.util.ArrayList;
import java.util.List;

import static src.Vars.*;
public class Evaluator {
    public ScopeEvaluator mainBlock = new ScopeEvaluator("__MAIN__");

    public EvaluatorNode begin(List<String> tokenList) {
        mainBlock.evaluate(tokenList, this);
        return mainBlock;
    }

    public static class EvaluatorNode {
        String name;
        String buffer = "";
        List<EvaluatorNode> members = new ArrayList<>();

        public EvaluatorNode(String name) {
            this.name = name;
        }
        protected EvaluatorNode evaluator(List<String> tokenList, Evaluator evaluator) throws Exception {
            throw new UnsupportedOperationException("This method is not yet implemented.");
        }

        public EvaluatorNode evaluate(List<String> tokenList, Evaluator evaluator) {
            try {
                return evaluator(tokenList, evaluator);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static class ScopeEvaluator extends EvaluatorNode {
        // if block starts with '{'
        boolean needsClosing = false;
        boolean functionBlock = false;
        public ScopeEvaluator(String name) {
            super(name);
        }
        public ScopeEvaluator(String name, boolean needsClosing, boolean functionBlock) {
            super(name);
            this.needsClosing = needsClosing;
            this.functionBlock = functionBlock;
        }
        @Override
        protected EvaluatorNode evaluator(List<String> tokenList, Evaluator evaluator) throws Exception {
            boolean isInitialized = false;
            String previousElementToken = "";

            System.out.printf("Parsing Block %s:%n", name);

            while (!tokenList.isEmpty()) {
                String token = tokenList.removeFirst();

                System.out.printf("scope :  %s  :  %s%n",name, token);

                buffer += token;

                // evaluate punctuations
                if (token.length() == 1 && isPunctuation(token.charAt(0))) {
                    char c = token.charAt(0);
                    if (isWhiteSpace(c)) {
                        continue;
                    }
                    if (isPunctuation(c) && !isInitialized)
                        throw new Exception("scope cannot start with a punctuation at token \"%s\"".formatted(c));

                    // expect new function '(', or equals '='
                    // FUNCTION DECLARATION
                    if (CHAR_BRACKET_OPEN == c) {
                        System.out.printf("Creating new function \"%s\"%n", previousElementToken);
                        EvaluatorNode node = new FunctionEvaluator(previousElementToken).evaluate(tokenList, evaluator);
                        members.add(node);
                    }
                    else if (needsClosing && CHAR_CURLY_CLOSE == c) {
                        System.out.printf("Created scope \"%s\"%n", name);
                        return this;
                    }
                    else {
                        throw new Exception("Unexpected token on scope %s: \"%s\"".formatted(name, c));
                    }
                } else {

                    // RETURN
                    if (functionBlock && token.equals(KEYWORD_RETURN)) {
                        OperationEvaluator returnOp = new OperationEvaluator(name+"_return");
                        members.add(returnOp);
                        returnOp.evaluate(tokenList, evaluator);
                    }
                    // VARIABLE DECLARATION
                    else if (previousElementToken.equals(KEYWORD_LET)) {
                        EvaluatorNode node = new DeclarationEvaluator(token).evaluate(tokenList, evaluator);
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
            return ("scope : " + name);
        }
    }

    public static class FunctionEvaluator extends EvaluatorNode {
        List<String> argumentNames = new ArrayList<>();
        ScopeEvaluator scope;

        public FunctionEvaluator(String name) {
            super(name);
        }

        @Override
        protected EvaluatorNode evaluator(List<String> tokenList, Evaluator evaluator) throws Exception {
            boolean isInitialized = false;
            String previousToken = "";
            boolean functionDeclared = false;
            boolean argumentWanted = false;

            System.out.printf("Parsing Function %s:%n", name);

            while (!tokenList.isEmpty()) {
                String token = tokenList.removeFirst();

                System.out.printf("function : %s : %s%n",name, token);

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
                        System.out.printf("Function header \"%s(%s)\" created%n", name, String.join(", ", argumentNames));
                        scope = new ScopeEvaluator(name, true, true);
                        members.add(scope.evaluate(tokenList, evaluator));
                        return this;
                    } else {
                        throw new Exception("Unexpected token at function declaration %s: \"%s\"".formatted(name, c));
                    }
                } else {


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
        // todo create an evaluator for operator
        public DeclarationEvaluator(String name) {
            super(name);
        }

        @Override
        protected EvaluatorNode evaluator(List<String> tokenList, Evaluator evaluator) throws Exception {

            String previousToken = "";
            boolean isInitialized = false;

            System.out.printf("Parsing Variable Declaration %s:%n", name);

            while (!tokenList.isEmpty()) {
                String token = tokenList.removeFirst();

                System.out.printf("declaration :  %s : %s%n",name, token);

                if (token.length() == 1 && isPunctuation(token.charAt(0))) {
                    char c = token.charAt(0);
                    if (isWhiteSpace(c)) {
                        continue;
                    }

                    throw new Exception("Unexpected token on variable declaration %s: \"%s\"".formatted(name, c));

                } else {
                    // check for equal sign
                    if (isOperator(token)) {
                        if (token.equals(OP_EQUALS)) {
                            OperationEvaluator operationEvaluator = new OperationEvaluator("op_"+name);
                            operationEvaluator.evaluate(tokenList, evaluator);
                            members.add(operationEvaluator);
                            return this;
                        }
                        else {
                            throw new Exception("Missing '=' sign %s: \"%s\"".formatted(name, token));
                        }
                    }
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return "declare %s".formatted(name, members);
        }
    }

    public static class OperationEvaluator extends EvaluatorNode {
        String type = OP_CONSTANT;
        String constantValue = "";
        EvaluatorNode baseOperand = null;
        EvaluatorNode targetOperand = null;
        List<String> operationTokens = new ArrayList<>();

        public OperationEvaluator(String name) {
            super(name);
        }
        @Override
        protected EvaluatorNode evaluator(List<String> tokenList, Evaluator evaluator) throws Exception {
            String previousToken = "";
            boolean isInitialized = false;

            System.out.printf("Parsing Operation Declaration %s:%n", name);

            while (!tokenList.isEmpty()) {
                String token = tokenList.removeFirst();

                System.out.printf("operation :  %s : %s%n",name, token);

                if (token.length() == 1 && isPunctuation(token.charAt(0))) {
                    char c = token.charAt(0);
                    if (isWhiteSpace(c)) {
                        continue;
                    }

                    if (CHAR_SEMICOLON == c) {
                        System.out.printf("operation : %s tokens : %s%n",name, operationTokens);
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
                            throw new Exception("Invalid declaration on %s, \"%s\"".formatted(name, token));
                        }

                        List<String> left = new ArrayList<>(operationTokens.subList(0, smallestIndex));
                        List<String> right = new ArrayList<>(operationTokens.subList(smallestIndex + 1, operationTokens.size()));

                        left.add(";");
                        right.add(";");

//                        System.out.printf("%s %s %s%n", left, right, type);

                        baseOperand = new OperationEvaluator("left_" + name);
                        targetOperand = new OperationEvaluator("right_" + name);

                        baseOperand.evaluate(left, evaluator);
                        targetOperand.evaluate(right, evaluator);

                        members.add(baseOperand);
                        members.add(targetOperand);

                        return this;
                    }
                } else {
                    operationTokens.add(token);
                }
            }
            return this;
        }

        @Override
        public String toString() {
//            return "operator :  %s : %s : %s".formatted(name, baseOperand == null ? constantValue : "", type);
            return "%s %s".formatted( baseOperand == null ? constantValue : "operator", type.equals(OP_CONSTANT) ? "" : type);

        }
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
}

