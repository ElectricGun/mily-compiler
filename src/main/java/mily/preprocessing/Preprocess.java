package mily.preprocessing;

import mily.constants.*;
import mily.structures.structs.*;
import mily.tokens.*;

import java.io.File;
import java.util.*;

import static mily.constants.Keywords.*;

public class Preprocess {

    public static List<Token> processIncludes(List<Token> tokenList, String directory, boolean debugMode) throws Exception {
        List<Token> newTokenList = new ArrayList<>();

        while (!tokenList.isEmpty()) {
            Token token = tokenList.remove(0);

            if (token.equalsKey(KEY_HASH)) {
                Token macroKeyword = tokenList.remove(0);

                while (Functions.isWhiteSpace(macroKeyword))
                    macroKeyword = tokenList.remove(0);

                if (macroKeyword.equalsKey(KEY_INCLUDE)) {
                    StringBuilder buffer = new StringBuilder();

                    Token currMacroToken;
                    while (true) {
                        currMacroToken = tokenList.remove(0);

                        if (currMacroToken.equalsKey(KEY_SEMICOLON)) {
                            break;

                        } else {
                            if (tokenList.isEmpty()) {
                                throw new Exception(String.format("Unclosed \"%s\" statement", KEY_INCLUDE) + token.line);
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
                    throw new Exception("Unknown macro keyword \"" + macroKeyword + "\"");
                }
            } else {
                newTokenList.add(token);
            }

        }
        return newTokenList;
    }
}
