package mily.utils;

import java.util.*;

public class HashCodeSimplifier {

    protected List<Integer> keys = new ArrayList<>();
    protected List<Integer> values = new ArrayList<>();

    public int simplifyHash(int hashCode) {
        if (!keys.contains(hashCode)) {
            if (keys.isEmpty()) {
                keys.add(hashCode);
                values.add(0);
                return 0;

            } else {
                int val = values.get(values.size() - 1) + 1;

                keys.add(hashCode);
                values.add(val);
                return val;
            }
        } else {
            return values.get(keys.indexOf(hashCode));
        }
    }
}
