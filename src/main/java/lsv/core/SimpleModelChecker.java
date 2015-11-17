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


        String[] vals;
        String[] constraintVals;
        try {
            vals = parseSubForm(formula, model, constraint);
            constraintVals = parseSubForm(formula, model, constraint);
            evaluate(formula.getQuantifier(), constraint.getQuantifier(), constraintVals, vals, model);
        } catch (NotValidException e) {
            e.printStackTrace();
        } catch (QuantifierNotFoundException e) {
            e.printStackTrace();
        }


        // if constraint evaluates to false, don't consider this path

        // analyse formula - what does it mean?


        return false;
    }

    public String[] getTrace() {
        return (String[]) globHistory.toArray();
    }


    /**
     * Function returns string value of formula
     * @param f
     * @param m
     * @param constraint
     * @return
     * @throws NotValidException
     * @throws QuantifierNotFoundException
     */

//    TODO maybe have array for quantifiers and opperators?
    private String[] parseSubForm(Formula f, Model m, Formula constraint) throws NotValidException, QuantifierNotFoundException {

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
                //valid[i] = true;
                // TODO deal with this in evaluate
            }
            if (vals[i] instanceof Formula) {
                vals[i] = parseSubForm((Formula) vals[i], m, constraint);
            }
        }



        // TODO move this shiz
//        switch (f.getOperator()) {
//
//            case ("||"):
//                return (valid[0] || valid[1]);
//            case ("&&"):
//                return (valid[0] && valid[1]);
//            case ("!"):
//                return !(valid[0] && valid[1]);
//            case ("=>"):
//                return (!valid[0] || valid[1]);
//            case ("<=>"):
//                break;
//            default:
//                break;
//        }

        return (String[]) vals;
    }


    /**
     * Identifies init states.
     *
     * @param model
     * @param pathQuantifier
     * @param cont - True if global quantifier is E, False if global quantifier is A
     * @return
     * @throws NotValidException
     */

    private boolean traverseModel(Model model, String constraintQuantifier, String[] parsedConstraintFormula, String pathQuantifier, String[] parsedPathFormula, boolean cont) throws NotValidException, QuantifierNotFoundException {
        boolean trueAtSomePoint = false;
        ArrayList<Transition> transitions = (ArrayList<Transition>) Arrays.asList(model.getTransitions());


        for (State state : model.getStates()) {
            if (state.isInit()) {
                ArrayList<String> history = new ArrayList<String>();
                history.add(state.getName());
                if (!helper(transitions, state.getName(), constraintQuantifier, parsedConstraintFormula, history, pathQuantifier, parsedPathFormula, cont)) {
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
    \     * @param history
     * @param pathQuantifier
     * @param parsedPathFormula
     * @param cont
     * @return trueAtSomePoint
     * @throws QuantifierNotFoundException
     */

    private boolean helper(ArrayList<Transition> transitions, String stateName, String constraintQuantifier, String[] parsedConstraintFormula, ArrayList<String> history, String pathQuantifier, String[] parsedPathFormula, boolean cont) throws QuantifierNotFoundException {

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

                if (!helper(transitions, next, constraintQuantifier, parsedConstraintFormula, history, pathQuantifier, parsedPathFormula, cont)) {

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


    //     TODO maybe no return value, but call traverse model
    private void evaluate(String quantifier, String constraintQuantifier, String[] constraintPath, String[] toEval, Model model) throws NotValidException, QuantifierNotFoundException {
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
            default:
                throw new QuantifierNotFoundException(quantifier);
        }
    }

}



