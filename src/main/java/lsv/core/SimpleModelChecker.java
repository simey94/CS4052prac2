package lsv.core;

import lsv.grammar.Formula;
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


    public boolean checkOperators(String operator, String[] vals, ArrayList<Transition> transitions, State state) throws OperatorNotSupportedException {
        switch (operator) {

            case ("||"):
                return (state.getLabelAsList().contains(vals[0]) || state.getLabelAsList().contains(vals[1]));
            case ("&&"):
                return (state.getLabelAsList().contains(vals[0]) && state.getLabelAsList().contains(vals[1]));
            case ("!"):
                return !(state.getLabelAsList().contains(vals[0]));
            case ("=>"):
                return (!state.getLabelAsList().contains(vals[0]) || state.getLabelAsList().contains(vals[1]));
            case ("<=>"):
                return ((!state.getLabelAsList().contains(vals[0]) && !state.getLabelAsList().contains(vals[1])) || (state.getLabelAsList().contains(vals[0]) && state.getLabelAsList().contains(vals[1])));
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

    private boolean traverseModel(Model model, FormulaPrime formulaPrime, FormulaPrime constraint, boolean cont) throws NotValidException, QuantifierNotFoundException {
        boolean trueAtSomePoint = false;
        ArrayList<Transition> transitions = (ArrayList<Transition>) Arrays.asList(model.getTransitions());


        for (State state : model.getStates()) {
            if (state.isInit()) {
                ArrayList<String> history = new ArrayList<String>();
                history.add(state.getName());
//                TODO evaluate if formula nad constraint are true at this point
//                Maybe we want to split up model and just pass in current execution
                // expected[0] = string
                // expected[1] = array of actions
                Object[] expected = new Object[2];
                if (!helper(model, null, transitions, state, formulaPrime, history)) {
                    if (!cont) {
                        throw new NotValidException(history);
                    } else {
                        continue;
                    }
                } else {
                    trueAtSomePoint = true;
                }
            }
        }
        return trueAtSomePoint;
    }


    /*  TODO change these traversal methods to have some concept of what should be expected.

    E.g. NEXT A, then if not A at this point return  error

    e.g. Always A until act Q then B: A expected, if Q occurs, then B is expected.

    e.g Finally A, A is expected, but if it is not A, we can continue

    This likely needs to be work recursively with some sort of FIFO operation

    So  E ( b pUq a ), would be b is expected with action p, until action q, which changes expected to a.

    AzFb(g && AG( A ( True cUd EF ( p || q ) ) ))

        TODO this should also only be for state operators
        TODO maybe we need expected action too?
        TODO what does EF and AG actually mean? How are these different ?
     so here label g is expected, and Always( action c occurs (need expected action, until action d, then at some point state label has to contain p or q
    */
    private boolean helper(Model model, Transition prev, ArrayList<Transition> transitions, State state, FormulaPrime formulaPrime, ArrayList<String> history) throws QuantifierNotFoundException {
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
//                                    TODO check if this is valid
                            if (share(formulaPrime.getActions()[1], t.getActions()) || (!(next.getLabelAsList().contains(formulaPrime.getVals()[1].toString())))) {
                                return true;
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
                if (!share(formulaPrime.getActions()[1], (prev.getActions())) || (!(state.getLabelAsList().contains(formulaPrime.getVals()[1].toString())))) {
                    return false;
                } else {
                    if (traverse(model, transitions, state, formulaPrime, history))
                        trueAtSomePoint = true;

                }
                break;
            case ('F'):
                if (share(formulaPrime.getActions()[1], (prev.getActions())) || (state.getLabelAsList().contains(formulaPrime.getVals()[1].toString()))) {
                    return true;
                } else {
                    if (traverse(model, transitions, state, formulaPrime, history)) {
                        trueAtSomePoint = true;
                    }
                }
                break;
            case ('U'):
                if (share(prev.getActions(), (formulaPrime.getActions()[0]))) {
                    if (!state.containsLabel(formulaPrime.getVals()[0])) {
                        return false;
                    } else {
                        traverse(model, transitions, state, formulaPrime, history);
                    }
                } else if (share(prev.getActions(), (formulaPrime.getActions()[1]))) {
                    return state.containsLabel(formulaPrime.getVals()[1]);
                }
                break;
            default:
                throw new QuantifierNotFoundException(formulaPrime.getQauntifier());
        }

        return trueAtSomePoint;
    }


    private boolean traverse(Model model, ArrayList<Transition> transitions, State state, FormulaPrime formulaPrime, ArrayList<String> history) throws QuantifierNotFoundException {
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
        for (int i = 0; i < one.length; i++) {
            for (int h = 0; h < two.length; h++) {
                if (one[i].equals(two[h])) {
                    return true;
                }
            }
        }
        return false;
    }


}



