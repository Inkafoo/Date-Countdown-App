package com.pac.komputer.daysleftuntilacertaindate;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pac.komputer.daysleftuntilacertaindate.RecyclerView.MyAdapter;
import com.pac.komputer.daysleftuntilacertaindate.RecyclerView.NewDateClass;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    public Context context;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<NewDateClass> newDates;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F44336")));            //set background color
        actionBar.setTitle(Html.fromHtml("<font color='#FAFAFA'>Days - Date Countdown</font>"));    //set textColor and text



        loadData();
        buildRecyclerView();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newDateDialog();
            }});

    }


    private void buildRecyclerView(){

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(newDates, MainActivity.this);
        recyclerView.setAdapter(adapter);

    }

    public void newDateDialog(){

        //creation and display AlertDialog
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        View viewDialog = LayoutInflater.from(MainActivity.this).inflate(R.layout.new_date_dialog, null);
        dialogBuilder.setView(viewDialog);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        //elements in AlertDialog view
        final EditText editTextTitle = (EditText) alertDialog.findViewById(R.id.edit_text_title);
        final EditText editTextDate = (EditText) alertDialog.findViewById(R.id.edit_text_date);
        final EditText editTextDescription = (EditText) alertDialog.findViewById(R.id.edit_text_description);
        final Button buttonCancel = (Button) alertDialog.findViewById(R.id.button_cancel);
        final Button buttonSet = (Button) alertDialog.findViewById(R.id.button_set);

        chooseDate(editTextDate);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        buttonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    checkSetButton(editTextDate, editTextTitle, editTextDescription, alertDialog);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void chooseDate(final EditText editTextDate){

        final  Calendar myCalendar = Calendar.getInstance();

        final  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMMM yyyy");
        long currentDate = System.currentTimeMillis();                                               //returns the current time in milliseconds.
        String dateStringCalendar = simpleDateFormat.format(currentDate);                            //format currentDate to string in simpleDataFormat ("d MMMM yyyy")
        editTextDate.setText(dateStringCalendar);                                                    //set editTextDate current Date


        final DatePickerDialog.OnDateSetListener  date = new DatePickerDialog.OnDateSetListener() {         //the listener used to indicate the user has finished selecting a date
            //detektor użyty do wskazania, że ​​użytkownik zakończył wybieranie daty
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {     // set calendar date and update editDate
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

               editTextDate.setText(simpleDateFormat.format(myCalendar.getTime()));
            }
        };


        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(MainActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void addNewRecycler(String stringTitle, String stringDate, String stringDescription) {

        NewDateClass date =  new NewDateClass(stringTitle, stringDate, stringDescription);
        newDates.add(0, date);
        adapter.notifyDataSetChanged();

        saveData();
    }

    private void loadData() {

      SharedPreferences mPrefs = getSharedPreferences("objects", context.MODE_PRIVATE);
      Gson gson = new Gson();
      String json = mPrefs.getString("myJson", null);
      Type type = new TypeToken<ArrayList<NewDateClass>>() {}.getType();
      newDates = gson.fromJson(json, type);

      if(newDates == null){
          newDates = new ArrayList<>();}
  }

    private void saveData(){

        SharedPreferences mPrefs = getSharedPreferences("objects", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(newDates);
        prefsEditor.putString("myJson", json);
        prefsEditor.apply();

    }

    private boolean dateIsOk(EditText editTextDate) throws ParseException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMMM yyyy");
        Date today = Calendar.getInstance().getTime();
        String todayDateS = simpleDateFormat.format(today);
        Date todayDate = simpleDateFormat.parse(todayDateS);
        Date futureDate = simpleDateFormat.parse(editTextDate.getText().toString());

        boolean dateIsOk = false;

        if(todayDate.before(futureDate)){
            dateIsOk = true;
        }else if(todayDate.after(futureDate)){
            dateIsOk = false;
        }else if(todayDate.equals(futureDate)){
            dateIsOk = true;
        }

        return dateIsOk;
    }

    private void checkSetButton(EditText editTextDate,EditText editTextTitle, EditText editTextDescription, AlertDialog alertDialog) throws ParseException {

         if(!editTextTitle.getText().toString().isEmpty() && dateIsOk(editTextDate)){

            final String stringTitle = editTextTitle.getText().toString();
            final String stringDate = editTextDate.getText().toString();
            final String stringDescription = editTextDescription.getText().toString();

            addNewRecycler(stringTitle, stringDate, stringDescription);
            alertDialog.dismiss();

         }else if(editTextTitle.getText().toString().isEmpty() && dateIsOk(editTextDate)){                            //editTextTitle is empty
                Toasty.info(MainActivity.this, "Enter the title", Toast.LENGTH_SHORT).show();
         }else if(!editTextTitle.getText().toString().isEmpty() && !dateIsOk(editTextDate)){                          //date is wrong
               Toasty.info(MainActivity.this, "Enter the future date ", Toast.LENGTH_SHORT).show();
         }else if(editTextTitle.getText().toString().isEmpty() && !dateIsOk(editTextDate)){
               Toasty.error(MainActivity.this, "Enter the title and future date ", Toast.LENGTH_SHORT).show();          //title and date is wrong
         }

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        saveData();
    }

    @Override
    protected void onPause() {
        super.onPause();

        saveData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        saveData();
    }
}
