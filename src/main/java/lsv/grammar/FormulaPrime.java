package lsv.grammar;

/**
 * A more OOP approach to the supplied Formula class.
 */

public class FormulaPrime extends FormulaElement {

    private FormulaElement[] vals = new FormulaElement[2];
    private Quantifier qauntifier;
    private Operator operator;
    private String[][] actions = new String[2][];
    private boolean mostNestedCTL = true;
    private boolean negation;

    /**
     * Constructor
     */

    public FormulaPrime(Formula f) {
        //parse formula
        qauntifier = new Quantifier(f.getQuantifier());
        operator = new Operator(f.getOperator());
        String[] ap = f.getAp();
        String[] tauts = f.getTautology();
        boolean[] neg = f.getApNeg();
        Formula[] nestedCTL = f.getNestedCTL();
        negation = f.isNegation();
        if (ap != null) {
            if (ap[0] != null) {
                vals[0] = new AtomicProp(ap[0]);
                if (neg != null) {
                    vals[0].setNegation(neg[0]);
                }
            }
            if (ap[1] != null) {
                vals[1] = new AtomicProp(ap[1]);
                if (neg != null) {
                    vals[0].setNegation(neg[0]);

                }
            }
        }

        if (tauts != null) {

            if (tauts[0] != null) {
                vals[0] = new Tautology();
            }
            if (tauts[1] != null) {
                vals[1] = new Tautology();
            }
        }

        if (nestedCTL != null) {
            if (nestedCTL[0] != null) {
                vals[0] = new FormulaPrime(nestedCTL[0]);
                mostNestedCTL = false;
            }
            if (nestedCTL[1] != null) {
                vals[1] = new FormulaPrime(nestedCTL[1]);
                mostNestedCTL = false;
            }
        }
        if (f.getActions() != null) {
            String[][] act = f.getActions();
            if (act[0] != null) {
                actions[0] = act[0];
            }
            if (act[1] != null) {
                actions[1] = act[1];
            }
        }
    }

    /**
     * Getter for negation
     *
     * @return true if formulaPrime is negation
     */
    public boolean isNegation() {
        return  negation;
    }

    /**
     * Getter
     *
     * @return innermost nestedCTL.
     */

    public boolean isMostNestedCTL() {
        return mostNestedCTL;
    }

    /**
     * Getter
     *
     * @return String representation of an Operator object.
     */

    public String getOperator() {
        return operator.getOperator();
    }

    /**
     * Getter
     *
     * @return String representation of a Quantifier object.
     */

    public String getQauntifier() {
        return qauntifier.getQuantifier();
    }

    /**
     * Getter
     *
     * @return FormulaElement array of size 2.
     */

    public FormulaElement[] getVals() {
        return vals;
    }

    /**
     * Getter
     *
     * @return Array of actions for current point of execution.
     */

    public String[][] getActions() {
        return actions;
    }

    /**
     * Setter
     *
     * @param position
     */

    public void setTautology(int position) {
        vals[position] = new Tautology();
    }

    /**
     * Get a value at a specific index.
     *
     * @param index
     * @return The FormulaElement object of the value at vals[index].
     */

    public FormulaElement getValuesAt(int index) {
        if (vals == null || index > (vals.length - 1)) {
            return null;
        } else {
            return vals[index];
        }
    }

    /**
     * Get String representation of actions at specific index.
     *
     * @param index
     * @return String represenation of action at actions[index]
     */

    public String[] getActionsAt(int index) {
        if (actions == null || index > (actions.length - 1)) {
            return null;
        } else {
            return actions[index];
        }
    }

}