package mily.structures.dataobjects;

/**
 * <h1> Class CodeFile </h1>
 * Simple data structure to store a code string and its file name.
 *
 * @author ElectricGun
 */

public record CodeFile(String directory, String filename, String code) {

    @Override
    public String toString() {
        return "CodeFile: filename \"\"\"\n" + code + "\"\"\"";
    }
}