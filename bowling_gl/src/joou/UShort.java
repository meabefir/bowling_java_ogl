
package joou;

import java.math.BigInteger;


public final class UShort extends UNumber implements Comparable<UShort> {

   
    private static final long serialVersionUID = -6821055240959745390L;

    public static final int MIN_VALUE = 0x0000;

   
    public static final int MAX_VALUE = 0xffff;

   
    public short value;

    public UShort() {
        value = 0;
    }

    
    public UShort(byte value) {
        this.value = value;
    }

  
    public UShort(short value) {
        this.value = value;
    }

   
    public UShort(int value) {
        this.value = (short) value;
    }

   
    public UShort(long value) {
        this.value = (short) value;
    }

   
    public UShort(BigInteger value) {
        this.value = value.shortValue();
    }

   
    private UShort(String value) {
        this.value = Short.parseShort(value);
    }

    public UShort(UShort uShort) {
        this.value = uShort.value;
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(value).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UShort) {
            return value == ((UShort) obj).value;
        }
        return false;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public int compareTo(UShort o) {
        int a = value, b = o.value;
        return a < b ? -1 : (a == b ? 0 : 1);
    }
    
    public int intValue() {
        return value & 0xffff;
    }
    
    public long longValue() {
        return value & 0xffff;
    }

   
    public static short checkSigned(byte value) throws ArithmeticException {
        if (value < 0) {
            throw new ArithmeticException("Value is out of range : " + value);
        }
        return value;
    }

   
    public static short checkSigned(short value) throws ArithmeticException {
        if (value < 0) {
            throw new ArithmeticException("Value is out of range : " + value);
        }
        return value;
    }

  
    public static short checkSigned(int value) throws ArithmeticException {
        if (value < 0 || value > MAX_VALUE) {
            throw new ArithmeticException("Value is out of range : " + value);
        }
        return (short) value;
    }

  
    public static short checkSigned(long value) throws ArithmeticException {
        if (value < 0 || value > MAX_VALUE) {
            throw new ArithmeticException("Value is out of range : " + value);
        }
        return (short) value;
    }

    /**
     * Throw exception if value out of range (long version)
     *
     * @param value Value to check
     * @return value if it is in range
     * @throws ArithmeticException if value is out of range
     */
    public static short checkSigned(BigInteger value) throws ArithmeticException {
        if (value.compareTo(BigInteger.ZERO) < 0 || value.intValue() > MAX_VALUE) {
            throw new ArithmeticException("Value is out of range : " + value);
        }
        return value.shortValue();
    }

    /**
     * Throw exception if value out of range (long version)
     *
     * @param value Value to check
     * @return value if it is in range
     * @throws ArithmeticException if value is out of range
     */
    public static short checkSigned(String value) throws ArithmeticException {
        if (value.startsWith("-")) {
            throw new ArithmeticException("Value is out of range : " + value);
        }
        return Short.parseShort(value);
    }
}
