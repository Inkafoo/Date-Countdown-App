package com.pac.komputer.daysleftuntilacertaindate.RecyclerView;

public class NewDateClass {

     final String stringTitle, stringDate, stringDescription;


    public NewDateClass(String stringTitle, String stringDate, String stringDescpition){

        this.stringTitle = stringTitle;
        this.stringDate = stringDate;
        this.stringDescription = stringDescpition;
    }

    public String getStringTitle(){
        return  stringTitle;
    }

    public String getStringDate(){
        return  stringDate;
    }

    public String getStringDescription(){
        return  stringDescription;
    }


}
