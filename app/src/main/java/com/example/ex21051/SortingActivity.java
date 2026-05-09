/**
 * @author shaked hazan shaked1246@gmail.com
 * @version 1.0
 * @since 9/05/2026
 * Activity to sort expenses by date or amount in ascending or descending order.
 */
package com.example.ex21051;

import static com.example.ex21051.FBref.refExpenses;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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

public class SortingActivity extends AppCompatActivity {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch datePriceSwitch, ascDescSwitch;
    ListView sortingLv;
    String key;

    /**
     * Initializes the activity, sets the content view, and connects UI elements.
     * <p>
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sorting);
        connectJavaXml();
    }

    /**
     * Connects UI elements to Java variables and initializes the ListView adapter.
     * <p>
     */
    private void connectJavaXml() {
        datePriceSwitch = findViewById(R.id.datePriceSwitch);
        ascDescSwitch = findViewById(R.id.ascDescSwitch);
        sortingLv = findViewById(R.id.sortingLv);
        sortingLv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        sortingLv.setAdapter(adp);
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
            datePriceSwitch.setChecked(false);
            ascDescSwitch.setChecked(false);
        }else if(id == R.id.menuFilter) {
            Intent it = new Intent(this, FilteringActivity.class);
            startActivity(it);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Performs sorting based on the selected criteria (date/amount) and order (asc/desc).
     * <p>
     *
     * @param view The view that was clicked.
     */
    public void sort(View view) {
        if(!datePriceSwitch.isChecked())
        {
            key = "date";
        }
        else
        {
            key = "amount";
        }
        Query query = refExpenses.orderByChild(key);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Expense> expenseList = new ArrayList<>();
                for(DataSnapshot data : snapshot.getChildren())
                {
                    Expense expense = data.getValue(Expense.class);
                    expenseList.add(expense);
                }
                if(ascDescSwitch.isChecked()) {
                    Collections.reverse(expenseList);
                }
                ArrayAdapter<Expense> adp = new ArrayAdapter<Expense>(SortingActivity.this, android.R.layout.simple_list_item_1, expenseList);
                sortingLv.setAdapter(adp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("realtime database", error.toString());
            }
        });
    }
}
