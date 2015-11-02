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
}
