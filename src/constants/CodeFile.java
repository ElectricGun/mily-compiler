package src.constants;

public class CodeFile {

    String code;
    String name;

    public CodeFile(String name, String code) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}