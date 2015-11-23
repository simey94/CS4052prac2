package lsv.core;

/**
 * Exception class to handle erroneous quantifiers.
 */
public class QuantifierNotFoundException extends Exception {

    /**
     * Constructor
     * @param quantifier
     */
    public QuantifierNotFoundException(String quantifier) {
        super("Quantifier not found: " +quantifier);
    }

}



