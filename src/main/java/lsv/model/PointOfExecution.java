package lsv.model;

/**
 * Created by 120011995 on 21/11/15.
 */
public class PointOfExecution {

    private State currentState;
    private State[] previousStates;
    private Transition[] previousTransistions;
    private Transition[] futureTransistions;

    public PointOfExecution(State currentState, State[] previousStates, Transition[] previousTransistions, State[] futureStates, Model model) {
        this.currentState = currentState;
        this.previousStates = previousStates;
        this.previousTransistions = previousTransistions;

        for (Transition t : model.getTransitions()) {
            if (t.getSource() ==)

        }

    }
}
