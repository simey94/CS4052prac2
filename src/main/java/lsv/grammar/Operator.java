package lsv.grammar;

/**
 * Class to represent an operator within an asCTL formula.
 */

public class Operator {

    private String operator;

    /**
     * Constructor
     * @param operator
     */
    public Operator(String operator) {
        this.operator = operator;
    }

    /**
     * Getter
     * @return String representation of an Operator object.
     */
    public String getOperator() {
        return operator;
    }


}
