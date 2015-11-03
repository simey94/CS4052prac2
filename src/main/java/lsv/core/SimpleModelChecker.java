package lsv.core;

import lsv.model.Model;
import lsv.grammar.Formula;

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







private boolean testForm(Formula f) {
    switch(f.getOperator()) {

        case ("||"):
            break;
        case ("&&"):
            break;
        case ("!"):
            break;
        case ("=>"):
            break;
        case ("<=>"):
            break;
        default:
            break;
    }

    return  false;
}

    /**
     * test sub parts of formula
     * @param f
     * @return
     */
    private boolean testSubForm(Formula f) {

        if (f.getAp()[0] != null) {


        } else if (f.getAp()[1] != null) {

        }
        return false;
    }
}
