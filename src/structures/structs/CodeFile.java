package src.structures.structs;

/**
 * <h1> Class CodeFile </h1>
 * Simple data structure to store a code string and its file name.
 *
 * @author ElectricGun
 */

public class CodeFile {

    String code;
    String filename;

    public CodeFile(String filename, String code) {
        this.code = code;
        this.filename = filename;
    }

    public String getCode() {
        return code;
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public String toString() {
        return "CodeFile: filename \"\"\"\n" + code + "\"\"\"";
    }
}