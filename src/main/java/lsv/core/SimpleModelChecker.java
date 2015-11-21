package lsv.core;

import lsv.grammar.Formula;
import lsv.grammar.FormulaElement;
import lsv.grammar.FormulaPrime;
import lsv.model.Model;
import lsv.model.State;
import lsv.model.Transition;

import java.util.ArrayList;
import java.util.Arrays;

public class SimpleModelChecker implements ModelChecker {


    private ArrayList<String> globHistory;

    /**
     * @param model
     * @param constraint
     * @param formula
     * @return
     */

    public boolean check(Model model, Formula constraint, Formula formula) {


        FormulaPrime formulaPrime = new FormulaPrime(formula);
        FormulaPrime constraintPrime = new FormulaPrime(constraint);
//            call traverse model


        return false;
    }

    public String[] getTrace() {
        return (String[]) globHistory.toArray();
    }


    public boolean checkOperators(String operator, FormulaPrime formula, State state, Transition prev, Model model, ArrayList<Transition> transitions, ArrayList<String> history) throws OperatorNotSupportedException, QuantifierNotFoundException {
        FormulaElement[] vals = formula.getVals();
        switch (operator) {

            case ("||"):
                return (state.isTrue(vals[0]) || state.isTrue(vals[1]));
            case ("&&"):
                return (state.isTrue(vals[0]) && state.isTrue(vals[1]));
            case ("!"):
                return !(state.isTrue(vals[0]));
            case ("=>"):
                return (!state.isTrue(vals[0]) || state.isTrue(vals[1]));
            case ("<=>"):
                return ((!state.isTrue(vals[0]) && !state.isTrue(vals[1])) || (state.isTrue(vals[0]) && state.isTrue(vals[1])));
            case ("U"):
                if (share(prev.getActions(), (formula.getActions()[0]))) {
                    if (!state.containsLabel(formula.getVals()[0])) {
                        return false;
                    } else {
                        traverse(model, transitions, state, formula, history);
                    }
                } else if (share(prev.getActions(), (formula.getActions()[1]))) {
                    return state.containsLabel(formula.getVals()[1]);
                }
                break;
            default:
                throw new OperatorNotSupportedException(operator);
        }
    }


    /**
     * Identifies init states.
     *
     * @param model
     * @param cont  - True if global quantifier is E, False if global quantifier is A
     * @return
     * @throws NotValidException
     */

    private boolean traverseModel(Model model, FormulaPrime formulaPrime, FormulaPrime constraint, boolean cont) throws NotValidException, QuantifierNotFoundException, OperatorNotSupportedException {
        boolean trueAtSomePoint = false;
        ArrayList<Transition> transitions = (ArrayList<Transition>) Arrays.asList(model.getTransitions());


        for (State state : model.getStates()) {
            if (state.isInit()) {
                ArrayList<String> history = new ArrayList<String>();
                history.add(state.getName());
                Object[] expected = new Object[2];
                if (!helper(model, null, transitions, state, formulaPrime, history)) {
                    if (!cont) {
                        throw new NotValidException(history);
                    }
                } else {
                    trueAtSomePoint = true;
                }
            }
        }
        return trueAtSomePoint;
    }


    private boolean helper(Model model, Transition prev, ArrayList<Transition> transitions, State state, FormulaPrime formulaPrime, ArrayList<String> history) throws QuantifierNotFoundException, OperatorNotSupportedException {
        boolean trueAtSomePoint = false;
        if (transitions.isEmpty()) {
            return true;
        }
        // avoid cycles
        if (history.contains(state.getName())) {
            return true;
        }
        history.add(state.getName());

        switch (formulaPrime.getQauntifier().charAt(1)) {
            case ('X'):
                if (formulaPrime.getVals()[0].equals(state.getName())) {
                    for (Transition t : transitions) {
                        if (t.getSource().equals(state.getName())) {
                            transitions.remove(t);
                            history.add(t.toString());
                            State next = model.getStateFromName(t.getTarget());
                            if (share(formulaPrime.getActions()[1], t.getActions())) {
                                if (checkOperators(formulaPrime.getOperator(), formulaPrime.getVals(), next)) {
                                    return true;
                                }
                            }
                        }
                    }
                    return false;
                } else {
                    if (traverse(model, transitions, state, formulaPrime, history))
                        trueAtSomePoint = true;
                }

                break;
            case ('G'):
                if (!share(formulaPrime.getActions()[1], (prev.getActions())) || (!checkOperators(formulaPrime.getOperator(), formulaPrime.getVals(), state))) {
                    return false;
                } else {
                    if (traverse(model, transitions, state, formulaPrime, history))
                        trueAtSomePoint = true;

                }
                break;
            case ('F'):
                if (share(formulaPrime.getActions()[1], (prev.getActions()))) {
                    if (checkOperators(formulaPrime.getOperator(), formulaPrime.getVals(), state))
                        return true;
                } else {
                    if (traverse(model, transitions, state, formulaPrime, history)) {
                        trueAtSomePoint = true;
                    }
                }
                break;
//            TODO handle until properly

            default:
                throw new QuantifierNotFoundException(formulaPrime.getQauntifier());
        }

        return trueAtSomePoint;
    }


    private boolean traverse(Model model, ArrayList<Transition> transitions, State state, FormulaPrime formulaPrime, ArrayList<String> history) throws QuantifierNotFoundException, OperatorNotSupportedException {
        boolean trueAtSomePoint = false;

        for (Transition t : transitions) {
            if (t.getSource() == state.getName()) {
                transitions.remove(t);
                history.add(t.toString());
                State next = model.getStateFromName(t.getTarget());
                if (helper(model, t, transitions, next, formulaPrime, history)) {
                    trueAtSomePoint = true;
                }
            }
        }
        return trueAtSomePoint;
    }


    private boolean share(String[] one, String[] two) {
        for (String anOne : one) {
            for (int h = 0; h < two.length; h++) {
                if (anOne.equals(two[h])) return true;
            }
        }
        return false;
    }


}



