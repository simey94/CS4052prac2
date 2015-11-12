package lsv.core;

/**
 * Created by davidwilson on 12/11/2015.
 */
public class QuantifierNotFoundException extends Exception {


    //Parameterless Constructor
    public QuantifierNotFoundException(String quantifier) {
        super("Quantifier not found: " +quantifier);


    }

}



