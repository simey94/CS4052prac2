package lsv.model;

import lsv.grammar.AtomicProp;
import lsv.grammar.FormulaElement;
import lsv.grammar.Tautology;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * */
public class State {
    private boolean init;
    private String name;
    private String[] label;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        State state = (State) o;

        if (init != state.init) return false;
        if (!name.equals(state.name)) return false;
        return Arrays.equals(label, state.label);

    }

    @Override
    public int hashCode() {
        int result = (init ? 1 : 0);
        result = 31 * result + name.hashCode();
        result = 31 * result + Arrays.hashCode(label);
        return result;
    }

    /**
     * Is state an initial state
     *
     * @return boolean init
     */
    public boolean isInit() {
        return init;
    }

    /**
     * Returns the name of the state
     *
     * @return String name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the labels of the state
     *
     * @return Array of string labels
     */
    public String[] getLabel() {
        return label;
    }


    public ArrayList<String> getLabelAsList() {
        return new ArrayList<String>(Arrays.asList(label));
    }


    public Boolean containsLabel(Object val) {
        ArrayList<String> labels = getLabelAsList();
        if (!(val instanceof String)) {
            return false;
        }
        if (((String) val).toLowerCase().equals("true")) {
            return true;
        }
        return labels.contains(val);

    }

    public boolean isTrue(FormulaElement formulaElement) {
        // if instance of ap check if array list contains that
        if (formulaElement instanceof AtomicProp) {
            ArrayList<String> labels = getLabelAsList();
            if (formulaElement.getNegation()) {
                return !(labels.contains(((AtomicProp) formulaElement).getAp()));
            } else return (labels.contains(((AtomicProp) formulaElement).getAp()));
        }

        // if instance of tut is true return true
        if (formulaElement instanceof Tautology) {
            return !formulaElement.getNegation();
        }
        // if nestedCTL recursively eval

        return false;
    }

}
