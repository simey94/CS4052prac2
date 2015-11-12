package lsv.core;

import lsv.grammar.Formula;
import lsv.model.Model;
import lsv.model.State;
import lsv.model.Transition;

import java.util.ArrayList;
import java.util.Arrays;

public class SimpleModelChecker implements ModelChecker {


    private ArrayList<String> globHistory;

    public boolean check(Model model, Formula constraint, Formula formula) {


        // if constraint evaluates to false, don't consider this path

        // analyse formula - what does it mean?


        return false;
    }

    public String[] getTrace() {
        return (String[]) globHistory.toArray();
    }

    /**
     * test sub parts of formula
     * @param f
     * @return
     */
    private boolean testSubForm(Formula f, Model m) {

        Object[] vals = new Object[2];
        boolean[] valid = new boolean[2];
        if (f.getAp()[0] != null) {
            vals[0] = f.getAp()[0];
        } else if (f.getTautology()[0] != null) {
            vals[0] = f.getTautology()[0];
        } else if (f.getNestedCTL()[0] != null) {
            vals[0] = f.getNestedCTL()[0];
        } else if (f.getActions()[0] != null) {
            vals[0] = f.getActions()[0];
        }

        if (f.getAp()[1] != null) {
            vals[1] = f.getAp()[1];
        } else if (f.getTautology()[1] != null) {
            vals[1] = f.getTautology()[1];
        } else if (f.getNestedCTL()[1] != null) {
            vals[1] = f.getNestedCTL()[1];
        } else if (f.getActions()[1] != null) {
            vals[1] = f.getActions()[1];
        }


        for (int i = 0; i < vals.length; i++) {
            if (vals[i] == null) {
                valid[i] = true;
            }
            if (vals[i] instanceof Formula) {
                valid[i] = testSubForm((Formula) vals[i], m);
            } else {
                valid[i] = evaluate(f.getQuantifier(), (String) vals[i], m);
            }
        }

        switch (f.getOperator()) {

            case ("||"):
                return (valid[0] || valid[1]);
            case ("&&"):
                return (valid[0] && valid[1]);
            case ("!"):
                return !(valid[0] && valid[1]);
            case ("=>"):
                return (!valid[0] || valid[1]);
            case ("<=>"):
                break;
            default:
                break;
        }

        return false;
    }


    private boolean traverseModel(Model model, Formula constraint, Formula formula, boolean cont) throws NotValidException {
        boolean trueAtSomePoint = false;
        ArrayList<Transition> transitions = (ArrayList<Transition>) Arrays.asList(model.getTransitions());

        for (State state : model.getStates()) {
            if (state.isInit()) {
                ArrayList<String> history = new ArrayList<String>();
                history.add(state.getName());
                if (!helper(transitions, state.getName(), constraint, formula, history, cont)) {
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

    private boolean helper(ArrayList<Transition> transitions, String stateName, Formula constraint, Formula formula, ArrayList<String> history, boolean cont) {

        boolean trueAtSomePoint = false;
        if (transitions.isEmpty()) {
            return true;
        }
//        if constraint is true, return false - can't use this branch
//        if formula is false, return false - can't use this branch
//        if always, break at first error -
        history.add(stateName);
        for (Transition t : transitions) {
            if (t.getSource() == stateName) {

                history.add(t.toString());

                String next = t.getTarget();
                transitions.remove(t);
                if (!helper(transitions, next, constraint, formula, history, cont)) {

                    if (!cont)
                        return false;
                    else {
                        continue;
                    }
                } else {
                    trueAtSomePoint = true;
                }
            }
        }

        return trueAtSomePoint;
    }

    private boolean evaluate(String quantifier, String toEval, Model model) {
        switch (quantifier) {
//            Globally - Has to hold entire subsequent path
            case ("G"):
                break;
//            Always Globally - from here on (all paths), true no matter what happens
            case ("AG"):
                break;
//            always finally - always
            case ("AF"):
//            always all paths
            case ("A"):
                break;
//            finally globally
            case ("FG"):
                break;
//            finally -- at some point, this is true
            case ("F"):
                break;
//            Eventually finally - might be true at some point
            case ("EF"):
                break;
//            Eventually globally
            case ("EG"):
                break;
//            Next - next this happens
            case ("X"):
                break;

//            always the next branch holds
            case ("AX"):
                break;

//            sometimes the next branch holds
            case ("EX"):
                break;

//            Until - x holds until q holds
            case ("U"):
                break;
//            Weak Until - x holds until y holds or  G x
            case ("W"):
                break;
            default:
                break;
        }


        return false;
    }
}
