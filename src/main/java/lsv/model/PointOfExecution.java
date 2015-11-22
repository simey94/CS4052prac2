package lsv.model;

import java.util.ArrayList;

public class PointOfExecution {

    private State currentState;
    private ArrayList<State> previousStates;
    private ArrayList<Transition> previousTransitions;
    private ArrayList<Transition> futureTransitions;


    public PointOfExecution(State currentState, PointOfExecution prev, Transition currentTransition, Model model) throws CycleException {
        this.currentState = currentState;
        if (prev != null) {
            ArrayList<State> temp = prev.getPreviousStates();
            temp.add(prev.getCurrentState()); // add previous state to the list of states it had
            previousStates = temp;

            //        check for cycle
            if (previousStates.contains(currentState)) {
                throw new CycleException();
            }
            ArrayList<Transition> tempTransitions = prev.getPreviousTransitions();

            if (currentTransition != null) {
                tempTransitions.add(currentTransition);
            }
            previousTransitions = tempTransitions;
        } else {
            previousTransitions = new ArrayList<Transition>();
            previousStates = new ArrayList<State>();
            futureTransitions = new ArrayList<Transition>();
        }
        for (Transition t : model.getTransitions()) {
            if (t.getSource() == currentState.getName()) {
                futureTransitions.add(t);
            }

        }

    }


    public ArrayList<Transition> getFutureTransitions() {
        return futureTransitions;
    }

    public ArrayList<State> getPreviousStates() {
        return previousStates;
    }

    public State getCurrentState() {
        return currentState;
    }

    public ArrayList<Transition> getPreviousTransitions() {
        return previousTransitions;
    }

    public Transition getLastTransition() {
        if (!previousTransitions.isEmpty()) {
            return previousTransitions.get(previousTransitions.size() - 1);
        }
        return null;
    }

}
