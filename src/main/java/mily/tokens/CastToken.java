//package mily.tokens;
//
///**
// * A token used to indicate casting in an expression
// *
// * @author ElectricGun
// */
//
//public class CastToken extends Token {
//
//    String type;
//
//    public CastToken(String string, String type, String source, int line) {
//        super(string, source, line);
//
//        this.type = type;
//    }
//
//    public String getType() {
//        return type;
//    }
//
//    @Override
//    public String toString() {
//        return String.format("cast(\"%s\")", type);
//    }
//}
