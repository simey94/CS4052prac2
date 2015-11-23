package lsv.grammar;

/**
 * Class to represent a Quantifier within an asCTL formula.
 */

public class Quantifier {

    private String quantifier;

    /**
     * Constructor.
     * @param quantifier
     */
    public Quantifier(String quantifier) {
        this.quantifier = quantifier;
    }

    /**
     * Getter
     * @return String representation of Quantifier.
     */
    public String getQuantifier() {
        return quantifier;
    }


}
