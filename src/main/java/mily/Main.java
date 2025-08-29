package mily;

import java.io.File;
import java.util.*;

import mily.codegen.*;
import mily.structures.errors.*;
import mily.structures.structs.*;
import mily.utils.*;

import static mily.constants.Functions.*;

public class Main {

    private final static String
            FLAG_PREFIX = "-",
            FLAG_DEBUG = "--debug",
            FLAG_QUIET = "--quiet",
            FLAG_PRINT_AST = "--print-ast",
            FLAG_PRINT_OUTPUT = "--print-output",
            FLAG_BENCHMARK = "--benchmark",
            FLAG_OUTPUT = "--output",
            FLAG_NO_CONFIRMATION = "--no-confirm",
            FLAG_HELP = "--help",
            FLAG_GENERATE_COMMENTS = "--generate-comments";

    public static void main(String[] args) throws Exception {
        ArgParser argParser = new ArgParser(FLAG_PREFIX);
        argParser.addFlag(FLAG_HELP, ArgParser.ArgTypes.BOOLEAN, "Print this page");
        argParser.addFlag(FLAG_DEBUG, ArgParser.ArgTypes.BOOLEAN, "Print very convoluted logs");
        argParser.addFlag(FLAG_QUIET, ArgParser.ArgTypes.BOOLEAN, "Disable descriptive prints");
        argParser.addFlag(FLAG_BENCHMARK, ArgParser.ArgTypes.BOOLEAN, "Print benchmark");
        argParser.addFlag(FLAG_OUTPUT, ArgParser.ArgTypes.STRING, "Output directory (folder)");
        argParser.addFlag(FLAG_PRINT_AST, ArgParser.ArgTypes.BOOLEAN, "Print final AST");
        argParser.addFlag(FLAG_PRINT_OUTPUT, ArgParser.ArgTypes.BOOLEAN, "Print compiled output");
        argParser.addFlag(FLAG_NO_CONFIRMATION, ArgParser.ArgTypes.BOOLEAN, "Disable confirmations");
        argParser.addFlag(FLAG_GENERATE_COMMENTS, ArgParser.ArgTypes.BOOLEAN, "Enabled system generated comments in compiled code");
        argParser.processFlags(args);

        final boolean debugMode = argParser.getBoolean(FLAG_DEBUG);
        final boolean isQuiet = argParser.getBoolean(FLAG_QUIET);
        final boolean printBenchmark = argParser.getBoolean(FLAG_BENCHMARK);
        final boolean printAst = argParser.getBoolean(FLAG_PRINT_AST);
        final boolean printOutput = argParser.getBoolean(FLAG_PRINT_OUTPUT);
        final boolean noConfirmation = argParser.getBoolean(FLAG_NO_CONFIRMATION);
        final boolean help = argParser.getBoolean(FLAG_HELP);
        final boolean generateComments = argParser.getBoolean(FLAG_GENERATE_COMMENTS);

        if (help) {
            argParser.printHelp();
            return;
        }

        File toRead;
        try {
            toRead = new File(argParser.getPositionalArgument());

        } catch (NullPointerException e) {
            System.out.println("Input file unspecified");
            System.exit(2);
            return;
        }

        // read code
        String fileName = toRead.getName();
        String cwd = toRead.getParent();
        CodeFile code = readFile(cwd, fileName);
        MilyWrapper wrapper = new MilyWrapper(debugMode, isQuiet);
        CompilerOutput output;
        try {
            output = wrapper.compile(code, cwd, generateComments);

        } catch (JavaMilyException e) {
            System.out.println(e.getMessage());
            System.exit(1);
            return;
        }

        if (printAst) {
            output.getAST().printRecursive();
        }
        IRCode irCode = output.getOutputCode();

        if (argParser.hasStringFlag(FLAG_OUTPUT)) {
            Scanner scanner = new Scanner(System.in);

            String outputFileNameWithoutExt;
            String outputFileName = code.getFilename();

            if (outputFileName.indexOf(".") > 0) {
                outputFileNameWithoutExt = outputFileName.substring(0, outputFileName.lastIndexOf("."));

            } else {
                outputFileNameWithoutExt = outputFileName;
            }

            String finalOutputPath = outputFileNameWithoutExt + ".mlog";
            File fullOutputPath = new File(argParser.getString(FLAG_OUTPUT), finalOutputPath);

            // for user overwrite confirmation
            if (fullOutputPath.exists() && !noConfirmation) {
                System.out.printf("File \"%s\" already exists%n", fullOutputPath.getAbsolutePath());
                System.out.println("Overwrite? (y/N)");
                while (true) {
                    String userInput = scanner.nextLine();
                    if (userInput.equalsIgnoreCase("y")) {
                        break;

                    } else if (userInput.equalsIgnoreCase("n") || userInput.isEmpty()) {
                        System.out.println("Cancelling");
                        System.exit(2);
                        return;
                    }
                }
            }
            scanner.close();
            writeFile(fullOutputPath.getParent(), fullOutputPath.getName(), irCode.generateMlog());

            if (!isQuiet) {
                System.out.println("Successfully written to " + fullOutputPath.getAbsolutePath());
            }
        }

        if (printOutput) {
            if (!isQuiet) {
                System.out.println("# Output:");
            }
            irCode.printMlog();
        }

        if (printBenchmark) {
            if (printOutput || printAst) {
                System.out.println();
            }
            System.out.printf(
                    "Lexing time: %sms%n" +
                            "AST building time: %sms%n" +
                            "Optimisation time: %sms%n" +
                            "Code generation time: %sms%n" +
                            "Total compile time: %sms%n",
                    output.getLexingDuration() / 1000000,
                    output.getAstBuildDuration() / 1000000,
                    output.getOptimizationDuration() / 1000000,
                    output.getCodeGenerationDuration() / 1000000,
                    output.getCompileDuration() / 1000000
            );
        }
    }
}
