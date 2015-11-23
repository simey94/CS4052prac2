package lsv.core;

import lsv.model.PointOfExecution;
import lsv.model.State;
import lsv.model.Transition;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Exception class to handle when a path is not valid. Used to create the counter-example trace of model satisfiability.
 */
class NotValidException extends Exception
{

    private ArrayList<Transition> transitions;
    private ArrayList<State> states;

    /**
     * Constructor to initalise a counter-example trace.
     * @param poe
     */
    public NotValidException(PointOfExecution poe) {
        transitions = new ArrayList<>();
        states = new ArrayList<>();
        if (poe != null) {
            states = poe.getPreviousStates();
            states.add(poe.getCurrentState()); // add current state on error
            transitions = poe.getPreviousTransitions();
        }
    }

    /**
     * Getter for Transistions
     * @return ArrayList of Transistions from current POE
     */
    public ArrayList<Transition> getTransitions() {
        return transitions;
    }

    /**
     * Getter for States
     * @return ArrayList of Transistions from current POE
     */
    public ArrayList<State> getStates() {
        return states;
    }

    /**
     * Converts history to String so it can be returned in SimpleModelChecker.getTrace().
     * @return ArrayList of Strings
     */
    public ArrayList<String> getExceptionHistory() {
        ArrayList<String> temp = new ArrayList<String>();
        Queue<Transition> transitionsQueue = new LinkedList<>(transitions);
        Queue<State> statesQueue = new LinkedList<>(states);

        while (!(transitionsQueue.isEmpty()) || !(statesQueue.isEmpty())) {
            if (!statesQueue.isEmpty()) {
                temp.add(statesQueue.remove().getName());
            }
            if (!transitionsQueue.isEmpty()) {
                temp.add(transitionsQueue.remove().toString());
            }
        }

        return temp;
    }
}

