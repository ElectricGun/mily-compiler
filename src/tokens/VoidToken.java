package src.tokens;

import static src.constants.Keywords.*;

/**
 * A token used for voids within expressions
 * @author ElectricGun
 */

public class VoidToken extends TypedToken {

    public VoidToken(String string, int line) {
        super(string, line, KEY_DATA_VOID);
    }
}
