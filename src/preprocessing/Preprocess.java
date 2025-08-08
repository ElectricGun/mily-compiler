package src.preprocessing;

import src.constants.*;
import src.structures.structs.*;
import src.tokens.*;

import java.io.File;
import java.util.*;

import static src.constants.Keywords.*;

public class Preprocess {

    public static List<Token> processIncludes(List<Token> tokenList, String directory, boolean debugMode) throws Exception {
        List<Token> newTokenList = new ArrayList<>();

        while (!tokenList.isEmpty()) {
            Token token = tokenList.removeFirst();

            if (token.equalsKey(KEY_HASH)) {
                Token macroKeyword = tokenList.removeFirst();
                while (Functions.isWhiteSpace(macroKeyword))
                    macroKeyword = tokenList.removeFirst();

                if (macroKeyword.equalsKey(KEY_INCLUDE)) {
                    Token macroStart = tokenList.removeFirst();
                    while (Functions.isWhiteSpace(macroStart))
                        macroStart = tokenList.removeFirst();

                    if (macroStart.equalsKey(KEY_DOLLAR)) {
                        StringBuilder buffer = new StringBuilder();

                        Token currMacroToken;
                        while (true) {
                            currMacroToken = tokenList.removeFirst();

                            if (currMacroToken.equalsKey(KEY_SEMICOLON)) {
                                break;

                            } else {
                                if (tokenList.isEmpty()) {
                                    throw new Exception("Unclosed macro from line " + macroStart.line);
                                }
                                buffer.append(currMacroToken.string);
                            }
                        }
                        File includedFile = new File(directory, buffer.toString().trim());
                        CodeFile includedCode = Functions.readFile(includedFile.getParent(), includedFile.getName());

                        List<Token> includedTokens = Lexing.tokenize(includedCode.getCode(), includedFile.getPath(), debugMode);
                        includedTokens = processIncludes(includedTokens, includedFile.getParent(), debugMode);

                        newTokenList.addAll(includedTokens);

                    } else {
                        throw new Exception("Expecting macro, found \"" + macroKeyword + "\" instead");
                    }

                } else {
                    throw new Exception("Unknown macro keyword \"" + macroKeyword + "\"");
                }
            } else {
                newTokenList.add(token);
            }

        }
        return newTokenList;
    }
}
