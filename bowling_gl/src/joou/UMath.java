
package joou;


public class UMath {

    public static UByte max(UByte a, UByte b) {
        return a.compareTo(b) > 0 ? a : b;
    }

    public static UInt max(UInt a, UInt b) {
        return a.compareTo(b) > 0 ? a : b;
    }

    public static ULong max(ULong a, ULong b) {
        return a.compareTo(b) > 0 ? a : b;
    }

    public static UShort max(UShort a, UShort b) {
        return a.compareTo(b) > 0 ? a : b;
    }

    public static UByte min(UByte a, UByte b) {
        return a.compareTo(b) < 0 ? a : b;
    }

    public static UInt min(UInt a, UInt b) {
        return a.compareTo(b) < 0 ? a : b;
    }

    public static ULong min(ULong a, ULong b) {
        return a.compareTo(b) < 0 ? a : b;
    }

    public static UShort min(UShort a, UShort b) {
        return a.compareTo(b) < 0 ? a : b;
    }
}
