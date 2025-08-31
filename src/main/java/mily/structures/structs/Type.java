package mily.structures.structs;

import java.util.*;

public class Type {

    public String typeString;
    public List<String> diamondStrings = new ArrayList<>(); // such as the String in List<String>

    public Type(String typeString) {
        this.typeString = typeString;
    }

    public Type(String typeString, List<String> diamondStrings) {
        this(typeString);
        this.diamondStrings = diamondStrings;
    }

    public boolean equals(String string) {
        return diamondStrings.isEmpty() && string.equals(typeString);
    }

    public Type create() {
        return new Type(typeString, diamondStrings);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Type type = (Type) o;
        return Objects.equals(typeString, type.typeString) && Objects.equals(diamondStrings, type.diamondStrings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeString, diamondStrings);
    }

    @Override
    public String toString() {
        String out = typeString;

        if (!diamondStrings.isEmpty()) {
            out += "<" + String.join(", ", diamondStrings) + ">";
        }

        return out;
    }
}
