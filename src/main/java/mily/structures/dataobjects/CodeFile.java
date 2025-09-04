package mily.structures.dataobjects;

/**
 * <h1> Class CodeFile </h1>
 * Simple data structure to store a code string and its file name.
 *
 * @author ElectricGun
 */

public class CodeFile {

    protected final String code;
    protected final String filename;
    protected final String directory;

    public CodeFile(String directory, String filename, String code) {
        this.directory = directory;
        this.code = code;
        this.filename = filename;
    }

    public String getCode() {
        return code;
    }

    public String getFilename() {
        return filename;
    }

    public String getDirectory() {
        return directory;
    }

    @Override
    public String toString() {
        return "CodeFile: filename \"\"\"\n" + code + "\"\"\"";
    }
}