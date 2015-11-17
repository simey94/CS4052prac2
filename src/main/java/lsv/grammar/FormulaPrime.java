package lsv.grammar;

/**
 * A more OOP approach to the supplied Formula class.
 */
public class FormulaPrime {

    private Formula parent;

    private boolean negation;

    private String quantifier;

    private String operator;


    private Object[] ap;
    private boolean[] apNeg;
    private boolean singleAp;


    private Object[] tautology;
    private boolean singleTt;

    private Formula[] nestedCTL;
    private String[][] actions;

    /**
     * Conyahstructor
     */
    public FormulaPrime() {

    }

}
