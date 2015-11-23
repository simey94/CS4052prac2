package lsv.core;

import lsv.grammar.Formula;
import lsv.grammar.FormulaElement;
import lsv.grammar.FormulaPrime;
import lsv.model.*;

import java.util.ArrayList;

/**
 * Class to implement asCTL model checking.
 */

public class SimpleModelChecker implements ModelChecker {

    private ArrayList<String> globHistory;

    /**
     * Performs verification of a model based on a formula and constraints. Calls checkInitStates to begin verification.
     *
     * @param model
     * @param constraint
     * @param formula
     * @return true if the formula and constraint can be satisifed in the model. False otherwise.
     */
    public boolean check(Model model, Formula constraint, Formula formula) {
        FormulaPrime constraintPrime = new FormulaPrime(constraint);
        boolean cont = false;
        while (true) {
            Model copy = new Model(model);
            try {
                String temp = constraintPrime.getQauntifier();

                if (temp != null) {
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
                }
                checkInitStates(model, constraintPrime, cont);
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

            boolean test = checkInitStates(model, formulaPrime, cont);
            return test;
        } catch (OperatorNotSupportedException e) {
            e.printStackTrace();
        } catch (QuantifierNotFoundException e) {
            e.printStackTrace();
        } catch (NotValidException e) {
            globHistory = e.getExceptionHistory();
        }
        return false;
    }

    /**
     * Returns a counter-example trace of an unsatisfiable path.
     *
     * @return String[] of the transistions
     */
    public String[] getTrace() {
        return globHistory.toArray(new String[globHistory.size()]);
    }

    /**
     * Sets current POE to an init state. Calls helper to begin model checking process from this POE.
     *
     * @param model
     * @param formulaPrime
     * @param cont
     * @return True if path is verified. False if path is not valid.
     * @throws NotValidException
     * @throws QuantifierNotFoundException
     * @throws OperatorNotSupportedException
     */
    private boolean checkInitStates(Model model, FormulaPrime formulaPrime, boolean cont) throws NotValidException, QuantifierNotFoundException, OperatorNotSupportedException {

        PointOfExecution next = null;
        boolean trueAtSomePoint = false;

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

    /**
     * Conducts all comparisons on a per operator basis at the current point of execution.
     *
     * @param operator
     * @param formula
     * @param poe
     * @param model
     * @param cont
     * @return True if the boolean relation evaluates to true. False if the relation evaluates to false.
     * @throws OperatorNotSupportedException
     * @throws QuantifierNotFoundException
     * @throws NotValidException
     */
    public boolean checkOperators(String operator, FormulaPrime formula, PointOfExecution poe, Model model, boolean cont) throws OperatorNotSupportedException, QuantifierNotFoundException, NotValidException {

        boolean temp = false;
        FormulaElement[] vals = formula.getVals();
        if (operator == null) {
            operator = "~"; //Tilde is case if null string is passed
        }
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
                if (share(poe.getLastTransition().getActions(), (formula.getActions()[0]))) { //if current action is action before the U
                    if (!poe.getCurrentState().isTrue(vals[0])) {  // if the value that is before the U is not current
                        if (cont) {
                            return temp;
                        } else if (!temp) {
                            throw new NotValidException(poe);
                        }
                    } else { // if the value that is before the U is current value for state
                        traverse(model, formula, poe, cont);
                    }
                } else if (share(poe.getLastTransition().getActions(), (formula.getActions()[1]))) { //if actions are action after the U
                    if (vals[1] != null) { // if there is value after U, check this is correct
                        temp = poe.getCurrentState().isTrue(vals[1]);
                    } else {
                        temp = true;
                    }
                }
                break;
            case ("~"): //Tilde is case if null string is passed
                if (vals[0] != null) {
                    temp = poe.getCurrentState().isTrue(vals[0]);
                } else {
                    temp = true;
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

    /**
     * @param model
     * @param poe
     * @param formulaPrime
     * @param cont
     * @return True if formula is satisfiable in the path. False if formula is invalid in the path.
     * @throws QuantifierNotFoundException
     * @throws OperatorNotSupportedException
     * @throws NotValidException
     */
    private boolean helper(Model model, PointOfExecution poe, FormulaPrime formulaPrime, boolean cont) throws QuantifierNotFoundException, OperatorNotSupportedException, NotValidException {
        boolean trueAtSomePoint = false;
        if (!(formulaPrime.isMostNestedCTL())) {
            for (int i = 0; i < 2; i++) {
                FormulaElement fe = formulaPrime.getVals()[i];
                if (fe instanceof FormulaPrime) {
                    boolean val = helper(model, poe, (FormulaPrime) fe, cont);

                    if (((FormulaPrime) fe).isNegation()) {
                        val = !val;
                    }
                    if (!val) {
                        throw new NotValidException(poe);
                    } else {
                        formulaPrime.setTautology(i);
                    }
                }
            }
            trueAtSomePoint = checkOperators(formulaPrime.getOperator(), formulaPrime, poe, model, cont);
        }
        else {

            char stateQuantifier = 0;
            String qauntifier = formulaPrime.getQauntifier();
            if (qauntifier == null) {
                stateQuantifier = 0;
            } else if (qauntifier.length() > 1) {
                stateQuantifier = formulaPrime.getQauntifier().charAt(1);
            }
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

                        trueAtSomePoint ^= formulaPrime.isNegation();
                        if (trueAtSomePoint) {
                            return trueAtSomePoint;
                        } else if (cont) {
                            return trueAtSomePoint;
                        } else {
                            throw new NotValidException(poe);
                        }
                    } else {
                        if (cont) { //means this hasn't occurred at this point, but will occur at some point.
                            return (false ^ formulaPrime.isNegation());
                        } else { // means this hasn't occurred - so return error
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
                            if (!formulaPrime.isNegation()) {
                                if (cont) {
                                    return false;
                                } else {
                                    throw new NotValidException(poe);
                                }
                            }
                        }
                        if (traverse(model, formulaPrime, poe, cont))
                            trueAtSomePoint = true;

                    } else if (!formulaPrime.isNegation()) {
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
                        return (checkOperators(formulaPrime.getOperator(), formulaPrime, poe, model, cont) ^ formulaPrime.isNegation());
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
                        return (formulaPrime.isNegation() ^ check);
                    }
            }
        }

        return (trueAtSomePoint^ formulaPrime.isNegation());
    }

    /**
     * @param model
     * @param formulaPrime
     * @param poe
     * @param cont
     * @return
     * @throws QuantifierNotFoundException
     * @throws OperatorNotSupportedException
     * @throws NotValidException
     */
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

    /**
     * Checks whether two arrays contain the same element.
     *
     * @param one
     * @param two
     * @return True if the two arrays share an element. False if they do not.
     */
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



