package lsv.core;

public class OperatorNotSupportedException extends Exception {


    //Parameterless Constructor
    public OperatorNotSupportedException(String operator) {
        super("Operator not supported: " + operator);


    }

}





