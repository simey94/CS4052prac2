package lsv.grammar;

/**
 * Class to represent an Atomic Proposistion element of an asCTL formula.
 */

public class AtomicProp extends FormulaElement {

    private String ap;

    /**
     * Constructor.
     * @param ap
     */
    public AtomicProp(String ap) {
        this.ap = ap;
    }

    /**
     * Getter
     * @return A string representation of the Atomoic Proposistion.
     */
    public String getAp() {
        return ap;
    }
}
