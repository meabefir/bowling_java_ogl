
package joou;

import java.math.BigInteger;


 // A base type for unsigned numbers.
 
public abstract class UNumber {

    /**
     * Generated UID
     */
    private static final long serialVersionUID = -7666221938815339843L;

    /**
     * Get this number as a {@link BigInteger}. This is a convenience method for
     * calling <code>new BigInteger(toString())</code>
     * @return 
     */
    public BigInteger toBigInteger() {
        return new BigInteger(toString());
    }
}
