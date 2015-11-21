package lsv.grammar;

/**
 * A more OOP approach to the supplied Formula class.
 */
public class FormulaPrime {

    private Object[] vals = new Object[2];
    private String qauntifier;
    private String operator;
    private String[][] actions;


    /**
     * Conyahstructor
     */
    public FormulaPrime(Formula f) {
        //parse formula
        qauntifier = f.getQuantifier();
        operator = f.getOperator();

        boolean[] valid = new boolean[2];
        if (f.getAp()[0] != null) {
            vals[0] = f.getAp()[0];
        } else if (f.getTautology()[0] != null) {
            vals[0] = true;
        } else if (f.getNestedCTL()[0] != null) {
            vals[0] = new FormulaPrime(f.getNestedCTL()[0]);
        } else if (f.getActions()[0] != null) {
            actions[0] = f.getActions()[0];
        }

        if (f.getAp()[1] != null) {
            vals[1] = f.getAp()[1];
        } else if (f.getTautology()[1] != null) {
            vals[1] = true;
        } else if (f.getNestedCTL()[1] != null) {
            vals[1] = new FormulaPrime(f.getNestedCTL()[1]);
        } else if (f.getActions()[1] != null) {
            actions[1] = f.getActions()[1];
        }

    }

    public String getOperator() {
        return operator;
    }

    public String getQauntifier() {
        return qauntifier;
    }

    public Object[] getVals() {
        return vals;

    }

    public String[][] getActions() {
        return actions;
    }
}
