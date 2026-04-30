package com.example.ex21051;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    String category = "";
    String [] categories;
    Spinner catSpin;
    EditText nameEt, eTDesc, eTPrice, dateInputEt;
    int selectedYear, selectedMonth, selectedDay;
    boolean dateSelected = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectJavaXml();
        catSpin.setOnItemSelectedListener(this);
        fillSpinner();
        dateInputEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
    }

    private void fillSpinner() {
        categories = getResources().getStringArray(R.array.categories);
        ArrayAdapter<String> adp = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, categories);
        catSpin.setAdapter(adp);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(i != 0) {
            category = categories[i];
            Log.i("Spinner", "category selected is -> " + category);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.i("Spinner", "Nothing selected");
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        int year, month, day;
        if(dateSelected)
        {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;
        }
        else {
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, y, monthOfYear, dayOfMonth) -> {
                    selectedDay = dayOfMonth;
                    selectedMonth = monthOfYear;
                    selectedYear = y;
                    dateSelected = true;
                    String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + y;
                    dateInputEt.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void connectJavaXml() {
        nameEt = findViewById(R.id.nameEt);
        eTPrice = findViewById(R.id.eTPrice);
        eTDesc = findViewById(R.id.eTDesc);
        dateInputEt = findViewById(R.id.dateInputEt);
        catSpin = findViewById(R.id.catSpin);
    }

    public void addToDb(View view) {
        if(eTDesc.getText().toString().isEmpty() || eTPrice.getText().toString().isEmpty()  || !dateSelected || category.isEmpty()) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Error");
            adb.setMessage("Please fill all the fields before confirming");
            adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog ad = adb.create();
            ad.show();
        }
        else
        {
            //TODO: add to FB

            nameEt.setText("");
            eTDesc.setText("");
            eTPrice.setText("");
            catSpin.setSelection(0);
            dateInputEt.setText("");
            dateSelected = false;
        }
    }
}