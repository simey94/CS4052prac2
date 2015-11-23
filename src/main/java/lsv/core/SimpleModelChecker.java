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
        while(true) {
            Model copy = new Model(model);
        try {
            String temp = constraintPrime.getQauntifier();
            switch (temp.charAt(0)) {
                case ('E'):
                    cont = true;
                    break;
                case ('A'):
                    cont = false;
                    break;
                default:
                    throw new QuantifierNotFoundException(constraintPrime.getQauntifier());
            }
            return checkInitStates(model, constraintPrime, cont);
        } catch (NotValidException e) {
            model.removeFromModel(e.getStates(), e.getTransitions());
            if (copy.equals(model)) {
                break;
            }
        } catch (OperatorNotSupportedException e) {
            e.printStackTrace();
        } catch (QuantifierNotFoundException e) {
            e.printStackTrace();
        }
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

            return checkInitStates(model, formulaPrime, cont);
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


    private boolean checkInitStates(Model model, FormulaPrime formulaPrime, boolean cont) throws NotValidException, QuantifierNotFoundException, OperatorNotSupportedException {

        PointOfExecution next = null;
        boolean trueAtSomePoint = false;
        ArrayList<Transition> transitions = new ArrayList<>(Arrays.asList(model.getTransitions()));


        for (State state : model.getStates()) {
            if (state.isInit()) {

                try {
                    next = new PointOfExecution(state, null, null, model);
                } catch (CycleException e) {
                    e.printStackTrace();
                    System.err.println("This should not occur");
                }
                if (!helper(model, next, formulaPrime, cont)) {
                    if (!cont) {
                        throw new NotValidException(next);
                    }
                } else {
                    trueAtSomePoint = true;
                }
            }
        }

        if (cont) {
            return trueAtSomePoint;
        } else if (!trueAtSomePoint) {
            throw new NotValidException(next);
        }

        return trueAtSomePoint;
    }


    public boolean checkOperators(String operator, FormulaPrime formula, PointOfExecution poe, Model model, boolean cont) throws OperatorNotSupportedException, QuantifierNotFoundException, NotValidException {

        boolean temp = false;
        FormulaElement[] vals = formula.getVals();
        switch (operator) {

            case ("||"):
                temp = (poe.getCurrentState().isTrue(vals[0]) || poe.getCurrentState().isTrue(vals[1]));
                break;
            case ("&&"):
                temp = (poe.getCurrentState().isTrue(vals[0]) && poe.getCurrentState().isTrue(vals[1]));
                break;
            case ("!"):
                temp = !(poe.getCurrentState().isTrue(vals[0]));
                break;
            case ("=>"):
                temp = (!poe.getCurrentState().isTrue(vals[0]) || poe.getCurrentState().isTrue(vals[1]));
                break;
            case ("<=>"):
                temp = ((!poe.getCurrentState().isTrue(vals[0]) && !poe.getCurrentState().isTrue(vals[1])) || (poe.getCurrentState().isTrue(vals[0]) && poe.getCurrentState().isTrue(vals[1])));
            case ("U"):
                if (share(poe.getLastTransition().getActions(), (formula.getActions()[0]))) {
                    if (!poe.getCurrentState().isTrue(vals[0])) {
                        if (cont) {
                            return temp;
                        } else if (!temp) {
                            throw new NotValidException(poe);
                        }
                    } else {
                        traverse(model, formula, poe, cont);
                    }
                } else if (share(poe.getLastTransition().getActions(), (formula.getActions()[1]))) {
//                    temp = poe.getCurrentState().isTrue(vals[1]);
                    if  (vals[1] != null) {
                        temp = poe.getCurrentState().isTrue(vals[1]);
                    }
                    else {
                        temp = true;
                    }
                }
                break;
            default:
                throw new OperatorNotSupportedException(operator);
        }

        if (cont) {
            return temp;
        } else if (!temp) {
            throw new NotValidException(poe);
        }

        return true;
    }

    private boolean helper(Model model, PointOfExecution poe, FormulaPrime formulaPrime, boolean cont) throws QuantifierNotFoundException, OperatorNotSupportedException, NotValidException {
        boolean trueAtSomePoint = false;
        if (!(formulaPrime.isMostNestedCTL())) {
            for (int i = 0; i < 2; i++) {
                FormulaElement fe = formulaPrime.getVals()[i];
                if (fe instanceof FormulaPrime) {
                    boolean val = helper(model, poe, (FormulaPrime) fe, cont);
                    if (!val) {
                        throw new NotValidException(poe);
                    } else {
                        formulaPrime.setTautology(i);
                    }
                }
            }
            return checkOperators(formulaPrime.getOperator(), formulaPrime, poe, model, cont);
        }

        char stateQuantifier = 0;
        if (formulaPrime.getQauntifier().length() > 1)
            stateQuantifier = formulaPrime.getQauntifier().charAt(1);
        switch (stateQuantifier) {
            case ('X'):
                Transition last = poe.getLastTransition();
                if (last == null) { // means this is an initial transition
                    return traverse(model, formulaPrime, poe, cont);
                } else if (share(formulaPrime.getActions()[1], (poe.getLastTransition()).getActions())) {
                    for (Transition t : poe.getFutureTransitions()) {
                        State nextState = model.getStateFromName(t.getTarget());
                        PointOfExecution next;
                        try {
                            next = new PointOfExecution(nextState, poe, t, model);
                        } catch (CycleException e) {
                            continue;
                        }
                        if (share(formulaPrime.getActions()[1], t.getActions())) {
                            if (checkOperators(formulaPrime.getOperator(), formulaPrime, next, model, cont)) {
                                trueAtSomePoint = traverse(model, formulaPrime, next, cont);
                            }
                        }

                    }

                    if (trueAtSomePoint) {
                        return true;
                    } else {
                        if (cont) {
                            return false;
                        } else {
                            throw new NotValidException(poe);
                        }
                    }
                } else {
                    if (cont) { //means this hasn't occured at this point, but will occur at some point.
                        return false;
                    } else { // means this hasn't occured - so return erro
                        throw new NotValidException(poe);
                    }
                }
            case ('G'):
                last = poe.getLastTransition();
                if (last == null) {
                    traverse(model, formulaPrime, poe, cont);
                }
                if (share(formulaPrime.getActions()[1], (poe.getLastTransition()).getActions())) {
                    if (!checkOperators(formulaPrime.getOperator(), formulaPrime, poe, model, cont)) {
                        return false;
                    }
                    if (cont) {
                        return false;
                    } else {
                        throw new NotValidException(poe);
                    }
                } else {
                    if (traverse(model, formulaPrime, poe, cont))
                        trueAtSomePoint = true;
                }
                break;
            case ('F'):
                last = poe.getLastTransition();
                if (last == null) {
                    traverse(model, formulaPrime, poe, cont);
                } else if (share(formulaPrime.getActionsAt(1), (last.getActions()))) { // if Fb , b has tow occurred
                    return checkOperators(formulaPrime.getOperator(), formulaPrime, poe, model, cont);
                } else { // if AaFb, check a has occured
                    if (formulaPrime.getActionsAt(0) != null) { // if AaFb, check that something is at a position
                        if (!cont) { //check if A
                            if (!share(formulaPrime.getActionsAt(0), (last.getActions()))) {
                                throw new NotValidException(poe);
                            }
                        }
                    }
                    if (traverse(model, formulaPrime, poe, cont)) {
                        trueAtSomePoint = true;
                    }
                }
                break;

            default: // could just be A or E
                last = poe.getLastTransition();
                if (last == null) { // means this is an initial transition
                    return traverse(model, formulaPrime, poe, cont);
                } else {
                    boolean check = checkOperators(formulaPrime.getOperator(), formulaPrime, poe, model, cont);
                    return check;
                }
        }
        return trueAtSomePoint;
    }


    private boolean traverse(Model model, FormulaPrime formulaPrime, PointOfExecution poe, boolean cont) throws QuantifierNotFoundException, OperatorNotSupportedException, NotValidException {
        boolean trueAtSomePoint = false;

        for (Transition t : poe.getFutureTransitions()) {
            State state = model.getStateFromName(t.getTarget());
            PointOfExecution next;
            try {
                next = new PointOfExecution(state, poe, t, model);
            } catch (CycleException e) {
                continue; // if we have reached a cycle, return false, this path canot be taken
            }
            if (helper(model, next, formulaPrime, cont)) {
                trueAtSomePoint = true;
            }
        }

        if (cont) {
            return trueAtSomePoint;
        } else if (!trueAtSomePoint) {
            throw new NotValidException(poe);
        }
        return trueAtSomePoint;
    }


    private boolean share(String[] one, String[] two) {
        if (one == null || two == null) {
            return false;
        }

        for (String anOne : one) {
            for (String aTwo : two) {
                if (anOne.equals(aTwo)) return true;
            }
        }
        return false;
    }


}



