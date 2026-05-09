/**
 * @author shaked hazan shaked1246@gmail.com
 * @version 1.0
 * @since 9/05/2026
 * Main activity for adding or updating an expense in the Firebase database.
 */
package com.example.ex21051;

import static com.example.ex21051.FBref.refExpenses;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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
    Intent gi;

    /**
     * Initializes the activity, sets up UI connections, and handles intent data for updates.
     * <p>
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
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
        gi = getIntent();
        if(gi.hasExtra("name"))
        {
            nameEt.setText(gi.getStringExtra("name"));
            eTDesc.setText(gi.getStringExtra("description"));
            double amount = gi.getDoubleExtra("amount", -1);
            if(amount != -1)
            {
                eTPrice.setText(amount +"");
            }
            String categoryGi = gi.getStringExtra("category");
            for(int i = 0; i < categories.length; i++)
            {
                if(categories[i].equals(categoryGi))
                {
                    catSpin.setSelection(i);
                }
            }
            String date = gi.getStringExtra("date");
            date = date.replaceAll("-", "/");
            dateInputEt.setText(date);
            dateSelected = true;
        }
    }

    /**
     * Populates the category spinner with values from resources.
     * <p>
     */
    private void fillSpinner() {
        categories = getResources().getStringArray(R.array.categories);
        ArrayAdapter<String> adp = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, categories);
        catSpin.setAdapter(adp);
    }

    /**
     * Inflates the options menu for the activity.
     * <p>
     *
     * @param menu The options menu in which you place your items.
     * @return boolean You must return true for the menu to be displayed; if you return false it will not be shown.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Handles navigation between activities from the options menu.
     * <p>
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();
        if(id == R.id.menuInpu)
        {
            eTDesc.setText("");
            eTPrice.setText("");
            catSpin.setSelection(0);
            dateSelected = false;
            dateInputEt.setText("");
            category = "";
        }else if(id == R.id.menuExpens) {
            Intent it = new Intent(this, DisplayActivity.class);
            startActivity(it);
        }else if(id == R.id.menuSort) {
            Intent it = new Intent(this, SortingActivity.class);
            startActivity(it);
        }else if(id == R.id.menuFilter) {
            Intent it = new Intent(this, FilteringActivity.class);
            startActivity(it);
        }else if(id == R.id.menuCred) {
            Intent it = new Intent(this, CreditsActivity.class);
            startActivity(it);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Updates the selected category when a spinner item is selected.
     * <p>
     *
     * @param adapterView The AdapterView where the selection happened.
     * @param view The view within the AdapterView that was clicked.
     * @param i The position of the view in the adapter.
     * @param l The row id of the item that is selected.
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(i != 0) {
            category = categories[i];
            Log.i("Spinner", "category selected is -> " + category);
        }
    }

    /**
     * Handles cases where no category is selected in the spinner.
     * <p>
     *
     * @param adapterView The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.i("Spinner", "Nothing selected");
    }

    /**
     * Shows a DatePickerDialog to allow the user to select an expense date.
     * <p>
     */
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

    /**
     * Connects UI elements to Java variables.
     * <p>
     */
    private void connectJavaXml() {
        nameEt = findViewById(R.id.nameEt);
        eTPrice = findViewById(R.id.eTPrice);
        eTDesc = findViewById(R.id.eTDesc);
        dateInputEt = findViewById(R.id.dateInputEt);
        catSpin = findViewById(R.id.catSpin);
    }

    /**
     * Adds a new expense to Firebase or returns updated data if in update mode.
     * <p>
     *
     * @param view The view that was clicked.
     */
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
        else if(Double.parseDouble(eTPrice.getText().toString()) <= 0)
        {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Error");
            adb.setMessage("The amount of the expense should be higher then 0");
            adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog ad = adb.create();
            ad.show();
        }
        else if(gi.hasExtra("name"))
        {
            String name = nameEt.getText().toString();
            String description = eTDesc.getText().toString();
            double amount = Double.parseDouble(eTPrice.getText().toString());
            String date = dateInputEt.getText().toString();
            date = date.replaceAll("/", "-");
            gi.putExtra("name", name);
            gi.putExtra("description", description);
            gi.putExtra("amount", amount);
            gi.putExtra("category", category);
            gi.putExtra("date", date);
            setResult(RESULT_OK, gi);
            finish();
        }
        else
        {
            String name = nameEt.getText().toString();
            String description = eTDesc.getText().toString();
            double amount = Double.parseDouble(eTPrice.getText().toString());
            String date = dateInputEt.getText().toString();
            date = date.replaceAll("/", "-");
            String dateSave = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
            Expense expense = new Expense(name, description, amount, category, dateSave);
            refExpenses.push().setValue(expense);
            nameEt.setText("");
            eTDesc.setText("");
            eTPrice.setText("");
            catSpin.setSelection(0);
            dateInputEt.setText("");
            dateSelected = false;
        }
    }
}
