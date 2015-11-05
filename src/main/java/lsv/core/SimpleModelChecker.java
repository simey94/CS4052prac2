package lsv.core;

import lsv.grammar.Formula;
import lsv.model.Model;
import lsv.model.State;
import lsv.model.Transition;

import java.util.ArrayList;
import java.util.Arrays;

public class SimpleModelChecker implements ModelChecker {

    public boolean check(Model model, Formula constraint, Formula formula) {


        // if constraint evaluates to false, don't consider this path

        // analyse formula - what does it mean?



	return false;
    }

    public String[] getTrace() {
	// TO IMPLEMENT
	return null;
    }


//private boolean testForm(Formula f) {
//    switch(f.getOperator()) {
//
//        case ("||"):
//            break;
//        case ("&&"):
//            break;
//        case ("!"):
//            break;
//        case ("=>"):
//            break;
//        case ("<=>"):
//            break;
//        default:
//            break;
//    }
//
//    return  false;
//}

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
        }

        else if (f.getTautology()[0] !=null) {
            vals[0] = f.getTautology()[0];
        }
        else if (f.getNestedCTL()[0] !=null) {
            vals[0] = f.getNestedCTL()[0];
        }
        else if (f.getActions()[0] != null) {
            vals[0] = f.getActions()[0];
        }

        if (f.getAp()[1] != null) {
            vals[1] = f.getAp()[1];
        }

        else if (f.getTautology()[1] !=null) {
            vals[1] = f.getTautology()[1];
        }
        else if (f.getNestedCTL()[1] !=null) {
            vals[1] = f.getNestedCTL()[1];
        }
        else if (f.getActions()[1] != null) {
            vals[1] = f.getActions()[1];
        }


        for (int i =0; i<vals.length; i++) {
            if  (vals[i] == null) {
                valid[i] = true;
            }
            if (vals[i] instanceof Formula) {
                valid[i] = testSubForm((Formula) vals[i],m);
            }
            else {
               valid[i] = evaluate(f.getQuantifier(),(String) vals[i], m);
            }
        }
        //TODO deal

        switch(f.getOperator()) {

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


    private boolean traverseModel(Model model) {
        ArrayList<Transition> transitions = (ArrayList<Transition>) Arrays.asList(model.getTransitions());
            for (State state : model.getStates()) {
                if (state.isInit()) {
                    if (!helper(transitions, state.getName()))
                        return false;
                }
            }
        return true;
        }

    private boolean helper(ArrayList<Transition> transitions, String stateName) {
        if (transitions.isEmpty()) {
            return true;
        }

        for (Transition t : transitions) {
            if (t.getSource() == stateName) {
                String next = t.getTarget();
                transitions.remove(t);
                if (!helper(transitions, next))
                    return false;
            }
        }

        return true;
    }

    private boolean evaluate(String quantifier, String toEval, Model model) {
        switch (quantifier){
//            Always Globally
            case ("AG"):
                break;
//            always finally
            case ("AF"):
//            always
            case ("A"):
                break;
//            finally globally
            case ("FG"):
                break;
//            finally
            case ("F"):
                break;
//            Eventually finally
            case ("EF"):
                break;
//            Eventually globally
            case ("EG"):
                break;
//            Next
            case ("X"):
                break;

//            Until
            case ("U"):
                break;
//            Weak Until
            case ("W"):
                break;
            default:
                break;
        }



        return false;
    }
}
