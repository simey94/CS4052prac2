package lsv.model;

import java.util.ArrayList;

/**
 * Class to represent the current point of execution whilst conducting Model verification.
 */

public class PointOfExecution {

    private State currentState;
    private ArrayList<State> previousStates;
    private ArrayList<Transition> previousTransitions;
    private ArrayList<Transition> futureTransitions = new ArrayList<Transition>();


    /**
     * Constructor to generate the current point of execution.
     *
     * @param currentState
     * @param prev
     * @param currentTransition
     * @param model
     * @throws CycleException
     */
    public PointOfExecution(State currentState, PointOfExecution prev, Transition currentTransition, Model model) throws CycleException {

        this.currentState = currentState;
        if (prev != null) {
            ArrayList<State> temp = prev.getPreviousStates();
            temp.add(prev.getCurrentState()); // add previous state to the list of states it had
            previousStates = temp;

            // check for cycle
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
        }
        for (Transition t : model.getTransitions()) {
            if (t.getSource().equals(currentState.getName())) {
                this.futureTransitions.add(t);
            }

        }

    }

    /**
     * Getter
     * @return ArrayList of type Transition representing the transitions available from current state that have not been used before.
     */

    public ArrayList<Transition> getFutureTransitions() {
        return futureTransitions;
    }

    /**
     * Getter
     * @return ArrayList of type State that model checker has encounter previously.
     */

    public ArrayList<State> getPreviousStates() {
        return previousStates;
    }

    /**
     * Getter
     * @return State object Model is currently in.
     */
    public State getCurrentState() {
        return currentState;
    }

    /**
     * Getter
     * @return ArrayList of type Transition representing the transitions the model has previously used.
     */
    public ArrayList<Transition> getPreviousTransitions() {
        return previousTransitions;
    }

    /**
     * Getter
     * @return Last Transition the Model Checker conducted.
     */
    public Transition getLastTransition() {
        if (!previousTransitions.isEmpty()) {
            return previousTransitions.get(previousTransitions.size() - 1);
        }
        return null;
    }

}
