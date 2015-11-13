package lsv.core;

import lsv.grammar.Formula;
import lsv.model.Model;
import lsv.model.State;
import lsv.model.Transition;

import java.util.ArrayList;
import java.util.Arrays;

public class SimpleModelChecker implements ModelChecker {


    private ArrayList<String> globHistory;

    /**
     *
     * @param model
     * @param constraint
     * @param formula
     * @return
     */

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


    /**
     * Identifies init states.
     *
     * @param model
     * @param constraint
     * @param formula
     * @param pathQuantifier
     * @param cont - True if global quantifier is E, False if global quantifier is A
     * @return
     * @throws NotValidException
     */

    private boolean traverseModel(Model model, Formula constraint, Formula formula, String pathQuantifier,  boolean cont) throws NotValidException, QuantifierNotFoundException {
        boolean trueAtSomePoint = false;
        ArrayList<Transition> transitions = (ArrayList<Transition>) Arrays.asList(model.getTransitions());

        for (State state : model.getStates()) {
            if (state.isInit()) {
                ArrayList<String> history = new ArrayList<String>();
                history.add(state.getName());
                if (!helper(transitions, state.getName(), constraint, formula, history,pathQuantifier, cont)) {
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

    /**
     *
     * @param transitions
     * @param stateName
     * @param constraint
     * @param history
     * @param pathQuantifier
     * @param parsedPathFormula
     * @param cont
     * @return trueAtSomePoint
     * @throws QuantifierNotFoundException
     */

    private boolean helper(ArrayList<Transition> transitions, String stateName, Formula constraint, ArrayList<String> history,  String pathQuantifier, String[] parsedPathFormula, boolean cont) throws QuantifierNotFoundException {

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

//                path specific quantifier case
                switch(pathQuantifier.toUpperCase()) {

//                    TODO need to actually deal with what we want to do - so figure out what we are evaluating
                    case ("X"):
                        // next
                        break;
                    case ("G"):
                        break;
                    case ("F"):
                        break;
                    case ("U"):
                        break;
                    case ("W"):
                        break;
                    default:
                        throw new QuantifierNotFoundException(pathQuantifier);

                }

                if (!helper(transitions, next, constraint, history, pathQuantifier,  cont)) {

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

    /**
     *
     * @param quantifier
     * @param toEval
     * @param model
     * @return
     */

    private boolean evaluate(String quantifier, String toEval, Model model) {



//            Until - x holds until q holds
//            case ("U"):
//                break;
////            Weak Until - x holds until y holds or  G x
//            case ("W"):
//                break;
//            default:
//                break;

        switch (quantifier.charAt(0)) {
//            Globally - Has to hold entire subsequent path
            case ('E'):
                switch (quantifier) {
//            Eventually finally - might be true at some point
                    case ("E"):
                        break;

                    case ("EF"):
                        break;
//            Eventually globally
                    case ("EG"):
                        break;
//            Next - next this happens
                    case ("EX"):
                        break;
                }
                break;
            case ('A'):
                switch (quantifier) {


//            always finally - always
                    case ("AF"):
//            always all paths
                    case ("A"):
                        break;
//            Always Globally - from here on (all paths), true no matter what happens
                    case ("AG"):
                        break;
//            always the next branch holds
                    case ("AX"):
                        break;
                }
                break;
////            finally globally
//            case ("FG"):
//                break;
////            finally -- at some point, this is true
//            case ("F"):
//                break;



        }


        return false;
    }
}
