package mily.preprocessing;

import mily.constants.*;
import mily.structures.dataobjects.*;
import mily.tokens.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static mily.constants.Keywords.*;

public class Preprocess {

    public static List<Token> processIncludes(List<Token> tokenList, String directory, boolean isInternal, boolean debugMode) throws Exception {
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

                    String libraryName = buffer.toString().trim();

                    List<Token> includedTokens;
                    // if the directory already exists in the internal library, use it instead
                    if (InternalLibraries.internalLibraryMap.containsKey(libraryName) || isInternal) {

                        if (isInternal) {
                            Path fullPath = Paths.get(directory).resolve(libraryName);
                            libraryName = fullPath.toString();
                        }

                        String internalDirectory = InternalLibraries.internalLibraryMap.get(libraryName);

                        if (internalDirectory == null) {
                            throw new Exception("Internal library not found " + '"' + libraryName + '"' + " on line " + token.line);
                        }

                        InputStream stream;
                        Scanner sc;

                        try {
                            stream = Preprocess.class.getResourceAsStream(internalDirectory);

                            sc = new Scanner(stream);

                        } catch (Exception e) {
                            throw e;
                        }

                        StringBuilder output = new StringBuilder();
                        while (sc.hasNextLine())
                            output.append(sc.nextLine() + "\n");

                        includedTokens = Lexing.tokenize(output.toString(), libraryName, debugMode);
                        includedTokens = processIncludes(includedTokens, "std/", true, debugMode);

                    } else {
                        File includedFile = new File(directory, libraryName);
                        CodeFile includedCode = Functions.readFile(includedFile.getParent(), includedFile.getName());

                        includedTokens = Lexing.tokenize(includedCode.code(), includedFile.getPath(), debugMode);
                        includedTokens = processIncludes(includedTokens, includedFile.getParent(), false, debugMode);
                    }


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
