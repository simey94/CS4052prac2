package lsv.core;

import lsv.grammar.Formula;
import lsv.model.Model;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;


public class ModelCheckerTest {

    /*
     * An example of how to set up and call the model building methods and make a call to the
     * model checker itself.  The contents of model.json, constraint1.json and ctl.json are
     * just examples, you need to add new models and formulas for the mutual exclusion task.
     * */

    @Test
    public void buildAndCheckModel() {

	Model model = Builder.buildModel("src/test/resources/model.json");

	Formula fairnessConstraint = Builder.buildFormula("src/test/resources/mutualExclusionFormula.json");

	Formula query = Builder.buildFormula("src/test/resources/ctl.json");
	
	ModelChecker mc = new SimpleModelChecker();

	// TO IMPLEMENT
//        assertTrue(mc.check(model, fairnessConstraint, query));

        Formula trueConstraint = Builder.buildFormula("src/test/resources/true.json");
//        Model mutualExclusionModel = Builder.buildModel("src/test/resources/mutualExclusion2.json");
        Formula mutualExclusionFormula = Builder.buildFormula("src/test/resources/mutualExclusionFormula.json");
        assertTrue(mc.check(model, trueConstraint, mutualExclusionFormula));
    }

}
