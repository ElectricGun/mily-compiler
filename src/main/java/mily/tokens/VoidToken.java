package mily.tokens;

import static mily.constants.Keywords.*;

/**
 * A token used for voids within expressions
 *
 * @author ElectricGun
 */

public class VoidToken extends TypedToken {

    public VoidToken(String string, String source, int line) {
        super(string, source, KEY_DATA_VOID, line);
    }
}
