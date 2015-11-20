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

        try {
            FormulaPrime formulaPrime = new FormulaPrime(formula);
            FormulaPrime constraintPrime = new FormulaPrime(constraint);
            evaluate(model, constraintPrime, formulaPrime);
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
                if (!helper(transitions, state.getName(), constraint, formulaPrime, history, cont)) {
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
     * @param transitions
     * @param stateName
     * @param history
     * @param cont
     * @return trueAtSomePoint
     * @throws QuantifierNotFoundException
     */

    private boolean helper(ArrayList<Transition> transitions, String stateName, FormulaPrime constraint, FormulaPrime formulaPrime, ArrayList<String> history, boolean cont) throws QuantifierNotFoundException {

    /*  TODO change these traversal methods to have some concept of what should be expected.

    E.g. NEXT A, then if not A at this point return  error

    e.g. Always A until act Q then B: A expected, if Q occurs, then B is expected.

    e.g Finally A, A is expected, but if it is not A, we can continue

    This likely needs to be work recursively with some sort of FIFO operation

    So  E ( b pUq a ), would be b is expected with action p, until action q, which changes expected to a.

    AzFb(g && AG( A ( True cUd EF ( p || q ) ) ))

        TODO this should also only be for state opperators
        TODO maybe we need expected action too?
        TODO what does EF and AG actually mean? How are these different ?
     so here label g is expected, and Always( action c occurs (need expected action, until action d, then at some point state label has to contain p or q
    */
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


//                lets do constraint first


//                TODO evaluate if formula nad constraint are true at this point
//                Maybe we want to split up model and just pass in current execution
//                path specific quantifier case
                switch (formulaPrime.getQauntifier().charAt(1)) {

//                    TODO need to actually deal with what we want to do - so figure out what we are evaluating
                    case ('X'):
                        // next
                        break;
                    case ('G'):
                        break;
                    case ('F'):
                        break;
                    case ('U'):
                        break;
                    case ('W'):
                        break;
                    default:
                        throw new QuantifierNotFoundException(formulaPrime.getQauntifier());

                }

                if (!helper(transitions, next, constraint, formulaPrime, history, cont)) {

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


    //     TODO DO WE NEED THIS?
    private void evaluate(Model model, FormulaPrime constraint, FormulaPrime formulaPrime) throws NotValidException, QuantifierNotFoundException {
        switch (formulaPrime.getQauntifier().charAt(0)) {
//            Globally - Has to hold entire subsequent path
            case ('E'):
                switch (formulaPrime.getQauntifier()) {
//            Eventually finally - might be true at some point
                    case ("E"):
                        // traverseModel()
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
                switch (formulaPrime.getQauntifier()) {
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
                throw new QuantifierNotFoundException(formulaPrime.getQauntifier());
        }
    }

}



