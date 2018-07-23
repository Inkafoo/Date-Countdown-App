package com.pac.komputer.daysleftuntilacertaindate.RecyclerView;

/**
 * Created by KOMPUTER on 03.06.2018.
 */

public class NewDateClass {

     final String stringTitle, stringDate, stringDescpition;


    public NewDateClass(String stringTitle, String stringDate, String stringDescpition){

        this.stringTitle = stringTitle;
        this.stringDate = stringDate;
        this.stringDescpition = stringDescpition;
    }

    public String getStringTitle(){
        return  stringTitle;
    }

    public String getStringDate(){
        return  stringDate;
    }

    public String getStringDescpition(){
        return  stringDescpition;
    }


}
