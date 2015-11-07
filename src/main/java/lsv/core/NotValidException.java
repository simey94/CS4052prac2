package lsv.core;

import java.util.ArrayList;

/**
 * Created by davidwilson on 07/11/2015.
 */



class NotValidException extends Exception
{

    private ArrayList<String> history;

    //Parameterless Constructor
    public NotValidException(ArrayList<String> history) {
        super(history.toString());
        this.history = history;

    }

    public ArrayList<String> getExceptionHistory() {
        return history;
    }
}

