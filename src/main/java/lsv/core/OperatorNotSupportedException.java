package lsv.core;

/**
 * Created by davidwilson on 21/11/2015.
 */
public class OperatorNotSupportedException extends Exception {


    //Parameterless Constructor
    public OperatorNotSupportedException(String operator) {
        super("Operator not supported: " + operator);


    }

}





