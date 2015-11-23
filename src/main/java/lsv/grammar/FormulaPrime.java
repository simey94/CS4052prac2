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

    /**
     * Constructor
     */

    public FormulaPrime(Formula f) {
        //parse formula
        qauntifier = new Quantifier(f.getQuantifier());
        operator = new Operator(f.getOperator());
        String[] ap = f.getAp();
        String[] tauts = f.getTautology();
        Formula[] nestedCTL = f.getNestedCTL();

        if (ap != null) {
            if (ap[0] != null) {
                vals[0] = new AtomicProp(ap[0]);
            }
            if (ap[1] != null) {
                vals[1] = new AtomicProp(ap[1]);
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

    public boolean isMostNestedCTL() {
        return mostNestedCTL;
    }

    public String getOperator() {
        return operator.getOperator();
    }

    public String getQauntifier() {
        return qauntifier.getQuantifier();
    }

    public FormulaElement[] getVals() {
        return vals;
    }

    public String[][] getActions() {
        return actions;
    }

    public void setTautology(int position) {
        vals[position] = new Tautology();
    }

    public FormulaElement getValuesAt(int index) {
        if (vals == null || index > (vals.length - 1)) {
            return null;
        } else {
            return vals[index];
        }
    }

    public String[] getActionsAt(int index) {
        if (actions == null || index > (actions.length - 1)) {
            return null;
        } else {
            return actions[index];
        }
    }

}