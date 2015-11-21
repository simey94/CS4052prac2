package lsv.core;

import lsv.model.PointOfExecution;
import lsv.model.State;
import lsv.model.Transition;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by davidwilson on 07/11/2015.
 */



class NotValidException extends Exception
{

    private ArrayList<Transition> transitions;
    private ArrayList<State> states;

    public NotValidException(PointOfExecution poe) {
        states = poe.getPreviousStates();
        states.add(poe.getCurrentState()); // add current state on error
        transitions = poe.getPreviousTransitions();

    }

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

