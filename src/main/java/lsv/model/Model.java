package lsv.model;

/**
 * A model is consist of states and transitions
 * */
public class Model {
    State [] states;
    Transition [] transitions;
		
    /**
     * Returns the list of the states
     * @return list of state for the given model
     * */
    public State[] getStates() {
	return states;
    }
		
    /**
     * Returns the list of transitions
     * @return list of transition for the given model
     * */
    public Transition[] getTransitions() {
	return transitions;
    }


    /**
     * Gets a state with a given name
     *
     * @param stateName
     * @return state with corresponding name
     */
    public State getStatefromName(String stateName) {
        for (State s : states) {
            if (s.getName().equals(stateName)) {
                return s;
            }
        }
        return null;
    }
}


