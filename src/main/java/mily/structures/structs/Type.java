package mily.structures.structs;

import java.util.*;

public class Type {

    public String typeString;
    public List<Type> diamondTypes = new ArrayList<>(); // such as the String in List<String>

    public Type(String typeString) {
        this.typeString = typeString;
    }

    public Type(String typeString, List<Type> diamondTypes) {
        this(typeString);
        this.diamondTypes = diamondTypes;
    }

    public boolean equals(String string) {
        return diamondTypes.isEmpty() && string.equals(typeString);
    }

    public Type create() {
        return new Type(typeString, diamondTypes);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Type type = (Type) o;
        return Objects.equals(typeString, type.typeString) && Objects.equals(diamondTypes, type.diamondTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeString, diamondTypes);
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder(typeString);

        if (!diamondTypes.isEmpty()) {
            out.append("<");
            for (int i = 0; i < diamondTypes.size(); i++) {
                out.append(diamondTypes.get(i).toString());
                if (i < diamondTypes.size() - 1) {
                    out.append(", ");
                }
            }
            out.append(">");
        }

        return out.toString();
    }
}
