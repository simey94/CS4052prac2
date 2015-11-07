package lsv.core;

/**
 * Created by davidwilson on 07/11/2015.
 */


class NotValidException extends Exception
{
    //Parameterless Constructor
    public NotValidException(ArrayList<String> history) {


    }

    //Constructor that accepts a message
    public WordContainsException(String message)
    {
        super(message);
    }
}

