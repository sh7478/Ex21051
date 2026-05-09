/**
 * @author shaked hazan shaked1246@gmail.com
 * @version 1.0
 * @since 9/05/2026
 * Activity for filtering expenses by price range or category.
 */
package com.example.ex21051;

import static com.example.ex21051.FBref.refExpenses;

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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class FilteringActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Switch priceCatSwitch;
    Spinner filterSpinCat;
    EditText eTMinPrice, eTMaxPrice;
    ListView filteringLv;
    String [] categories;
    String category = "";
    Query query;

    /**
     * Initializes the activity and sets up UI elements.
     * <p>
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtering);
        connectJavaXml();
        fillSpinner();
    }

    /**
     * Connects UI elements to Java variables and sets listeners.
     * <p>
     */
    private void connectJavaXml() {
        priceCatSwitch = findViewById(R.id.priceCatSwitch);
        filterSpinCat = findViewById(R.id.filterSpinCat);
        eTMinPrice = findViewById(R.id.eTMinPrice);
        eTMaxPrice = findViewById(R.id.eTMaxPrice);
        filteringLv = findViewById(R.id.filteringLv);
        filteringLv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ArrayAdapter<Expense> adp = new ArrayAdapter<Expense>(FilteringActivity.this, android.R.layout.simple_list_item_1);
        filteringLv.setAdapter(adp);
        filterSpinCat.setOnItemSelectedListener(this);
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
            Intent it = new Intent(this, MainActivity.class);
            startActivity(it);
        }else if(id == R.id.menuExpens) {
            Intent it = new Intent(this, DisplayActivity.class);
            startActivity(it);
        }else if(id == R.id.menuSort) {
            Intent it = new Intent(this, SortingActivity.class);
            startActivity(it);
        }else if(id == R.id.menuFilter) {
            priceCatSwitch.setChecked(false);
            filterSpinCat.setSelection(0);
            eTMinPrice.setText("");
            eTMaxPrice.setText("");
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Filters expenses based on the selected mode (price range or category).
     * <p>
     *
     * @param view The view that was clicked.
     */
    public void filter(View view) {
        if(!priceCatSwitch.isChecked())
        {
            if(eTMinPrice.getText().toString().isEmpty() || eTMaxPrice.getText().toString().isEmpty())
            {
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setTitle("Error");
                adb.setMessage("Please fill all the fields before filtering");
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
                double max = Double.parseDouble(eTMaxPrice.getText().toString());
                double min = Double.parseDouble(eTMinPrice.getText().toString());
                query = refExpenses.orderByChild("amount").startAt(min).endAt(max);
                setListenerToQuery();
            }
        }
        else
        {
            if(category.isEmpty())
            {
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setTitle("Error");
                adb.setMessage("Please select a category before filtering");
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
                query = refExpenses.orderByChild("category").equalTo(category);
                setListenerToQuery();
            }
        }
    }

    /**
     * Switches the visibility of UI components based on the filtering mode.
     * <p>
     *
     * @param view The view that was clicked (the switch).
     */
    public void switchMod(View view) {
        if(priceCatSwitch.isChecked())
        {
            filterSpinCat.setVisibility(View.VISIBLE);
            filterSpinCat.setSelection(0);
            eTMaxPrice.setVisibility(View.INVISIBLE);
            eTMinPrice.setVisibility(View.INVISIBLE);
        }
        else
        {
            filterSpinCat.setVisibility(View.INVISIBLE);
            eTMaxPrice.setVisibility(View.VISIBLE);
            eTMinPrice.setVisibility(View.VISIBLE);
            eTMinPrice.setText("");
            eTMaxPrice.setText("");
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
        filterSpinCat.setAdapter(adp);

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
     * Adds a listener to the current query and updates the ListView with results.
     * <p>
     */
    public void setListenerToQuery()
    {
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Expense> expenseList = new ArrayList<>();
                for(DataSnapshot data : snapshot.getChildren())
                {
                    Expense expense = data.getValue(Expense.class);
                    expenseList.add(expense);
                }
                ArrayAdapter<Expense> adp = new ArrayAdapter<Expense>(FilteringActivity.this, android.R.layout.simple_list_item_1, expenseList);
                filteringLv.setAdapter(adp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("realtime database", error.toString());
            }
        });
    }
}
