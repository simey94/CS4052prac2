package lsv.core;

import lsv.grammar.Formula;
import lsv.grammar.FormulaElement;
import lsv.grammar.FormulaPrime;
import lsv.model.*;

import java.util.ArrayList;
import java.util.Arrays;

public class SimpleModelChecker implements ModelChecker {


    private ArrayList<String> globHistory;


    public boolean check(Model model, Formula constraint, Formula formula) {


        FormulaPrime constraintPrime = new FormulaPrime(constraint);
        boolean cont;
        try {

            switch (constraintPrime.getQauntifier().charAt(0)) {
                case ('E'):
                    cont = true;
                    break;
                case ('A'):
                    cont = false;
                    break;
                default:
                    throw new QuantifierNotFoundException(constraintPrime.getQauntifier());
            }
            traverseModel(model, constraintPrime, cont);
        } catch (NotValidException e) {
            model.removeFromModel(e.getStates(), e.getTransitions());
        } catch (OperatorNotSupportedException e) {
            e.printStackTrace();
        } catch (QuantifierNotFoundException e) {
            e.printStackTrace();
        }

        FormulaPrime formulaPrime = new FormulaPrime(formula);
        try {
            switch (formulaPrime.getQauntifier().charAt(0)) {
                case ('E'):
                    cont = true;
                    break;
                case ('A'):
                    cont = false;
                    break;
                default:
                    throw new QuantifierNotFoundException(formulaPrime.getQauntifier());
            }

            return traverseModel(model, formulaPrime, cont);
        } catch (OperatorNotSupportedException e) {
            e.printStackTrace();
        } catch (QuantifierNotFoundException e) {
            e.printStackTrace();
        } catch (NotValidException e) {
            globHistory = e.getExceptionHistory();
        }
        return false;
    }

    public String[] getTrace() {
        return (String[]) globHistory.toArray();
    }


    private boolean traverseModel(Model model, FormulaPrime formulaPrime, boolean cont) throws NotValidException, QuantifierNotFoundException, OperatorNotSupportedException {

        PointOfExecution next = null;
        boolean trueAtSomePoint = false;
        ArrayList<Transition> transitions = (ArrayList<Transition>) Arrays.asList(model.getTransitions());


        for (State state : model.getStates()) {
            if (state.isInit()) {

                try {
                    next = new PointOfExecution(state, null, null, model);
                } catch (CycleException e) {
                    e.printStackTrace();
                    System.err.println("This should not occur");
                }
                if (!helper(model, next, formulaPrime)) {
                    if (!cont) {
                        throw new NotValidException(next);
                    }
                } else {
                    trueAtSomePoint = true;
                }
            }
        }
        return trueAtSomePoint;
    }


    public boolean checkOperators(String operator, FormulaPrime formula, PointOfExecution poe, Model model) throws OperatorNotSupportedException, QuantifierNotFoundException, NotValidException {

        FormulaElement[] vals = formula.getVals();
        switch (operator) {

            case ("||"):
                return (poe.getCurrentState().isTrue(vals[0]) || poe.getCurrentState().isTrue(vals[1]));
            case ("&&"):
                return (poe.getCurrentState().isTrue(vals[0]) && poe.getCurrentState().isTrue(vals[1]));
            case ("!"):
                return !(poe.getCurrentState().isTrue(vals[0]));
            case ("=>"):
                return (!poe.getCurrentState().isTrue(vals[0]) || poe.getCurrentState().isTrue(vals[1]));
            case ("<=>"):
                return ((!poe.getCurrentState().isTrue(vals[0]) && !poe.getCurrentState().isTrue(vals[1])) || (poe.getCurrentState().isTrue(vals[0]) && poe.getCurrentState().isTrue(vals[1])));
            case ("U"):
                if (share(poe.getLastTransition().getActions(), (formula.getActions()[0]))) {
                    if (!poe.getCurrentState().containsLabel(formula.getVals()[0])) {
                        return false;
                    } else {
                        traverse(model, formula, poe);
                    }
                } else if (share(poe.getLastTransition().getActions(), (formula.getActions()[1]))) {
                    return poe.getCurrentState().containsLabel(formula.getVals()[1]);
                }
                break;
            default:
                throw new OperatorNotSupportedException(operator);
        }
        return false;
    }


    //    TODO deal with PATH QUANTIFIERS
//    TODO Take in cont and work from there
    private boolean helper(Model model, PointOfExecution poe, FormulaPrime formulaPrime) throws QuantifierNotFoundException, OperatorNotSupportedException, NotValidException {
        boolean trueAtSomePoint = false;


        if (!(formulaPrime.isMostNestedCTL())) {
            for (int i = 0; i < 2; i++) {
                FormulaElement fe = formulaPrime.getVals()[i];
                if (fe instanceof FormulaPrime) {
                    boolean val = helper(model, poe, (FormulaPrime) fe);
                    if (!val) {
                        throw new NotValidException(poe);
                    } else {
                        formulaPrime.setTautology(i);
                    }
                }
            }
            return checkOperators(formulaPrime.getOperator(), formulaPrime, poe, model);
        } else switch (formulaPrime.getQauntifier().charAt(1)) {
            case ('X'):
                if (poe.getCurrentState().getLabelAsList().contains(formulaPrime.getVals()[0])) {
                    for (Transition t : poe.getFutureTransitions()) {
                        State nextState = model.getStateFromName(t.getTarget());
                        PointOfExecution next;
                        try {
                            next = new PointOfExecution(nextState, poe, t, model);
                        } catch (CycleException e) {
                            continue;
                        }
                        if (share(formulaPrime.getActions()[1], t.getActions())) {
                            if (checkOperators(formulaPrime.getOperator(), formulaPrime, next, model)) {
                                return true;
                            }
                        }

                    }
                    return false;
                } else {
                    if (traverse(model, formulaPrime, poe))
                        trueAtSomePoint = true;
                }

                break;
            case ('G'):
                if (!share(formulaPrime.getActions()[1], (poe.getLastTransition()).getActions()) || (!checkOperators(formulaPrime.getOperator(), formulaPrime, poe, model))) {
                    return false;
                } else {
                    if (traverse(model, formulaPrime, poe))
                        trueAtSomePoint = true;

                }
                break;
            case ('F'):
                if (share(formulaPrime.getActions()[1], (poe.getLastTransition()).getActions())) {
                    if (checkOperators(formulaPrime.getOperator(), formulaPrime, poe, model))
                        return true;
                } else {
                    if (traverse(model, formulaPrime, poe)) {
                        trueAtSomePoint = true;
                    }
                }
                break;

            default:
                throw new QuantifierNotFoundException(formulaPrime.getQauntifier());
        }
        return trueAtSomePoint;
    }


    private boolean traverse(Model model, FormulaPrime formulaPrime, PointOfExecution poe) throws QuantifierNotFoundException, OperatorNotSupportedException, NotValidException {
        boolean trueAtSomePoint = false;

        for (Transition t : poe.getFutureTransitions()) {
            State state = model.getStateFromName(t.getTarget());
            PointOfExecution next;
            try {
                next = new PointOfExecution(state, poe, t, model);
            } catch (CycleException e) {
                return true; // if we have reached a cycle, return true
            }
            if (helper(model, next, formulaPrime)) {
                trueAtSomePoint = true;
            }
        }
        return trueAtSomePoint;
    }


    private boolean share(String[] one, String[] two) {
        for (String anOne : one) {
            for (String aTwo : two) {
                if (anOne.equals(aTwo)) return true;
            }
        }
        return false;
    }


}



