package com.pac.komputer.daysleftuntilacertaindate.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.pac.komputer.daysleftuntilacertaindate.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    List<NewDateClass> listDates;
    Context context;

    public MyAdapter(List<NewDateClass> listDates, Context context) {
        this.listDates = listDates;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_date_layout, parent ,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final NewDateClass newDate = listDates.get(position);

        holder.textViewTitle.setText(newDate.getStringTitle());
        holder.textViewDate.setText(newDate.getStringDate());
        holder.textViewDescription.setText(newDate.getStringDescpition());
        holder.textViewDays.setText("");

        holder.textViewPoMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popupMenu = new PopupMenu(context, holder.textViewPoMenu);
                popupMenu.inflate(R.menu.menu_recyclerview);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.menu_item_edit:
                                    editItem(holder.textViewTitle, holder.textViewDate, holder.textViewDescription, position);
                                break;

                            case R.id.menu_item_delete:
                                    deleteItem(position);
                                break;

                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });


        try {
            daysUntil(holder.textViewDate.getText().toString(), holder.textViewDays);
        }catch (Exception e){}

    }

    @Override
    public int getItemCount() {
        return listDates.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public  TextView textViewTitle,textViewDate,textViewDescription, textViewPoMenu, textViewDays;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.date_title);
            textViewDate = itemView.findViewById(R.id.date_date);
            textViewDescription = itemView.findViewById(R.id.date_description);
            textViewPoMenu = itemView.findViewById(R.id.textViewMenu);
            textViewDays = itemView.findViewById(R.id.days_to_left);

        }
    }

    private void deleteItem(int position) {

        listDates.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();

    }

    private void editItem(TextView textViewTitle, TextView textViewDate, TextView textViewDescription, final int position){

        //creation and display AlertDialog
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.new_date_dialog, null);
        dialogBuilder.setView(view);
        final AlertDialog alertDialog =  dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();


        //find elements in AlertDialog view
        final EditText editTextTitle = (EditText) alertDialog.findViewById(R.id.edit_text_title);
        final EditText editTextDate = (EditText) alertDialog.findViewById(R.id.edit_text_date);
        final EditText editTextDescription = (EditText) alertDialog.findViewById(R.id.edit_text_description);
        final Button buttonCancel = (Button) alertDialog.findViewById(R.id.button_cancel);
        final Button buttonSet = (Button) alertDialog.findViewById(R.id.button_set);

        //editTexts in AlertDialog view
        editTextTitle.setText(textViewTitle.getText().toString());
        editTextTitle.setSelection(editTextTitle.getText().length());       //set cursor at the end of text
        editTextDate.setText(textViewDate.getText().toString());
        editTextDescription.setText(textViewDescription.getText().toString());


        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    buildDatePicker(editTextDate);
                }catch (Exception E){}
            }
        });

        buttonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    checkSetButton(editTextDate, editTextTitle, editTextDescription, alertDialog, position);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    private void buildDatePicker(final EditText editTextDate) throws ParseException {

        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat simpleDateFormatLong = new SimpleDateFormat("d MMMM yyyy");
        final SimpleDateFormat simpleDateFormatShort = new SimpleDateFormat("dd.MM.yyyy");

        String myString = editTextDate.getText().toString();
        Date myDate = simpleDateFormatLong.parse(myString);
        String formattedDate = simpleDateFormatShort.format(myDate);

        formattedDate = formattedDate.replace(".", " ");                          //dd MM yyyy
        String [] part = formattedDate.split(" ");
        final int mDay = Integer.parseInt(part[0]);        //dd
        final int mMonth = Integer.parseInt(part[1]);      //MM
        final int mYear = Integer.parseInt(part[2]);       //yyyy

       DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
           @Override
           public void onDateSet(DatePicker datePicker, int year, int month, int day)  {
               calendar.set(Calendar.YEAR, year);
               calendar.set(Calendar.MONTH, month);
               calendar.set(Calendar.DAY_OF_MONTH, day);

               editTextDate.setText(simpleDateFormatLong.format(calendar.getTime()));
           }
       };

       showDatePicker(dateSetListener, mYear, mMonth, mDay);
   }

    private void showDatePicker(DatePickerDialog.OnDateSetListener dateSetListener, int mYear, int mMonth, int mDay ){

        new DatePickerDialog(context, dateSetListener, mYear, mMonth, mDay).show();
    }

    private void addItem(int position, NewDateClass date){

        listDates.add(position, date);
        notifyDataSetChanged();

    }

    private void daysUntil(String edtDate, TextView textViewDays) throws ParseException {
        
        long days = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy");
        Date today = Calendar.getInstance().getTime();
        Date futureDate = dateFormat.parse(edtDate);

        if(today.before(futureDate)){
            long differenceTime = futureDate.getTime() - today.getTime();
            days = differenceTime/(24*60*60*1000) + 1;                          //number of days until event
        }

        textViewDays.setText(String.valueOf(days));
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

    private void checkSetButton(EditText editTextDate,EditText editTextTitle, EditText editTextDescription, AlertDialog alertDialog, int position) throws ParseException {

        if(!editTextTitle.getText().toString().isEmpty() && dateIsOk(editTextDate)){

            final String stringTitle = editTextTitle.getText().toString();
            final String stringDate = editTextDate.getText().toString();
            final String stringDescription = editTextDescription.getText().toString();

            deleteItem(position);

            NewDateClass newDate = new NewDateClass(stringTitle, stringDate, stringDescription);
            addItem(position, newDate);

            alertDialog.dismiss();

        }else if(editTextTitle.getText().toString().isEmpty() && dateIsOk(editTextDate)){           //editTextTitle is empty
            Toasty.info(context, "Enter the title ", Toast.LENGTH_SHORT).show();
        }else if(!editTextTitle.getText().toString().isEmpty() && !dateIsOk(editTextDate)){         //date is wrong
            Toasty.info(context, "Enter the future date ", Toast.LENGTH_SHORT).show();
        }else if(editTextTitle.getText().toString().isEmpty() && !dateIsOk(editTextDate)){
            Toasty.error(context, "Enter the title and future date ", Toast.LENGTH_SHORT).show();     //editTextTitle and date are wrong
        }

    }

}























