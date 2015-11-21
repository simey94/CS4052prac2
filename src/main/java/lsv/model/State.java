package lsv.model;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * */
public class State {
    private boolean init;
    private String name;
    private String[] label;

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
//            TODO nested CTL here
        }
        if (((String) val).toLowerCase().equals("true")) {
            return true;
        }
//            TODO deal with nestedCTL type
        return labels.contains(val);

    }
}
