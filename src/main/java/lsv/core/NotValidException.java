package lsv.core;

import java.util.ArrayList;

/**
 * Created by davidwilson on 07/11/2015.
 */


class NotValidException extends Exception
{
    //Parameterless Constructor
    public NotValidException(ArrayList<String> history) {
        super(history.toString());

    }

    //Constructor that accepts a message

}

