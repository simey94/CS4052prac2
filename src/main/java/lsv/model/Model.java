package lsv.model;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A model is consist of states and transitions
 * */
public class Model {
    State [] states;
    Transition [] transitions;



    public Model(State [] states,Transition [] transitions) {
        this.states = states;
        this.transitions = transitions;
    }

    public Model(Model m) {
        this.states = m.getStates();
        this.transitions = m.getTransitions();
    }
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
    public State getStateFromName(String stateName) {
        for (State s : states) {
            if (s.getName().equals(stateName)) {
                return s;
            }
        }
        return null;
    }


    public void removeFromModel(ArrayList<State> statesToRemove, ArrayList<Transition> transitionsToRemove) {
        ArrayList<State> tempStates = new ArrayList<State>(Arrays.asList(states));
        ArrayList<Transition> tempTransitions = new ArrayList<Transition>(Arrays.asList(transitions));
        tempStates.removeAll(statesToRemove);
        tempTransitions.removeAll(transitionsToRemove);
        if (tempStates != null) {
            states = tempStates.toArray(new State[tempStates.size()]);
        }
        if (tempTransitions != null) {
            transitions = tempTransitions.toArray(new Transition[tempTransitions.size()]);
        }
    }


    public boolean equals(Model model) {
        for (int i=0; i<states.length; i++ ) {
            if (!(states[i].equals(model.getStates()[i]))) return false;
        }
        for (int i=0; i<transitions.length; i++ ) {
            if (!(transitions[i].equals(model.getStates()[i]))) return false;
        }

        return false;

    }
}


