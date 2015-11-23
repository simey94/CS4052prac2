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
    public void checkTrue() {

	Model model = Builder.buildModel("src/test/resources/model.json");

	Formula query = Builder.buildFormula("src/test/resources/true.json");
	
	ModelChecker mc = new SimpleModelChecker();
    assertTrue(mc.check(model, query, query));




    }

    @Test
    public void testMutualExclusion() {
        ModelChecker mc = new SimpleModelChecker();
//        Test mutual Exclusion
        Formula trueConstraint = Builder.buildFormula("src/test/resources/true.json");
        Model mutualExclusionModel = Builder.buildModel("src/test/resources/mutualExclusion2.json");
        Formula mutualExclusionFormula = Builder.buildFormula("src/test/resources/mutualExclusionFormula.json");
        assertTrue(mc.check(mutualExclusionModel, trueConstraint, mutualExclusionFormula));

        mutualExclusionFormula.setNegation(true);
        assertTrue(!(mc.check(mutualExclusionModel, trueConstraint, mutualExclusionFormula)));
    }


    @Test
    public void ourModelTest(){

    Model model = Builder.buildModel("src/test/resources/ourModel.json");
    Formula fairnessConstraint = Builder.buildFormula("src/test/resources/modelFormula.json");
    Formula query = Builder.buildFormula("src/test/resources/modelFormula.json");
    ModelChecker mc = new SimpleModelChecker();

    assertTrue(!(mc.check(model, fairnessConstraint, query)));

    }

}
