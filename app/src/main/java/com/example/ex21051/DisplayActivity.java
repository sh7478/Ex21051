/**
 * @author shaked hazan shaked1246@gmail.com
 * @version 1.0
 * @since 9/05/2026
 * Activity to display, search, and manage (delete/update) expenses from Firebase.
 */
package com.example.ex21051;

import static com.example.ex21051.FBref.refExpenses;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.List;

public class DisplayActivity extends AppCompatActivity implements View.OnCreateContextMenuListener, AdapterView.OnItemSelectedListener{

    ListView lv;
    Spinner monthSpin;
    TextView totalMonthTv, descTv;
    ArrayAdapter<String> lvAdp;
    EditText eTDescSearch;
    String key;
    ValueEventListener VEL;
    int selectedMonthToSum;
    ArrayList<String> expenseList = new ArrayList<String>();
    ArrayList<Expense> expenseValues = new ArrayList<Expense>();
    ArrayList<String> keyList = new ArrayList<String>();
    int REQUEST_CODE = 100;

    /**
     * Initializes the activity, sets the content view, and sets up UI connections and listeners.
     * <p>
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        connectJavaXml();
        configureVEL();
        readDataFromDb();
    }

    /**
     * Reads all expense data from Firebase once and populates the ListView.
     * <p>
     */
    private void readDataFromDb() {
        refExpenses.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                expenseList.clear();
                expenseValues.clear();
                keyList.clear();
                for(DataSnapshot data : snapshot.getChildren()){
                    String str1 = (String) data.getKey();
                    keyList.add(str1);
                    Expense expenseTmp = data.getValue(Expense.class);
                    expenseValues.add(expenseTmp);
                    String str = expenseTmp.toString();
                    expenseList.add(str);
                }
                lvAdp = new ArrayAdapter<String>(DisplayActivity.this, android.R.layout.simple_list_item_1, expenseList);
                lv.setAdapter(lvAdp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("realtime database : ", error.toString());
            }
        });
    }

    /**
     * Configures the ValueEventListener used for displaying search results in a TextView.
     * <p>
     */
    private void configureVEL() {
        VEL = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String str = "";
                for (DataSnapshot data : snapshot.getChildren()) {
                    Expense expense = data.getValue(Expense.class);
                    if (expense != null) {
                        str += "Name: " + expense.getName() +  ", Amount: " + expense.getAmount() + "\nCategory: " + expense.getCategory() + "\n";
                    }
                }
                if (str.isEmpty()) {
                    descTv.setText("No results found");
                } else {
                    descTv.setText(str);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("realtime database : " , error.toString());
            }
        };
    }

    /**
     * Connects UI elements to Java variables and initializes adapters and listeners.
     * <p>
     */
    private void connectJavaXml() {
        lv = findViewById(R.id.lv);
        monthSpin = findViewById(R.id.monthSpin);
        totalMonthTv = findViewById(R.id.totalMonthTv);
        descTv = findViewById(R.id.descTv);
        eTDescSearch = findViewById(R.id.eTDescSearch);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        registerForContextMenu(lv);
        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        lv.setAdapter(adp);
        String[] months = getResources().getStringArray(R.array.months);
        monthSpin.setOnItemSelectedListener(this);
        ArrayAdapter<String> adpSpin = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, months);
        monthSpin.setAdapter(adpSpin);
    }

    /**
     * Triggered by search button; performs a Firebase query to find expenses by description.
     * <p>
     *
     * @param view The view that was clicked.
     */
    public void searchDesc(View view) {
        if(eTDescSearch.getText().toString().isEmpty())
        {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Error");
            adb.setMessage("Please fill the description search before confirming");
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
            String description = eTDescSearch.getText().toString();
            Query query = refExpenses.orderByChild("description").equalTo(description);
            query.addListenerForSingleValueEvent(VEL);
        }
    }

    /**
     * Calculates and displays the total sum of expenses for the selected month.
     * <p>
     *
     * @param view The view that was clicked.
     */
    public void calcTotal(View view) {
        Query query = refExpenses.orderByChild("date");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double sum = 0;
                for(DataSnapshot data : snapshot.getChildren())
                {
                    Expense expense = data.getValue(Expense.class);
                    if(expense.monthMatch(selectedMonthToSum)) {
                        sum += expense.getAmount();
                    }
                }
                totalMonthTv.setText("The total sum of the selected month is --> " + sum);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("realtime database", error.toString());
            }
        });
    }

    /**
     * Creates the context menu for ListView items.
     * <p>
     *
     * @param menu The context menu that is being built.
     * @param v The view for which the context menu is being built.
     * @param menuInfo Extra information about the item for which the context menu should be shown.
     */
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add("delete expense");
        menu.add("update expense");
    }

    /**
     * Handles selection of context menu items (delete or update).
     * <p>
     *
     * @param item The context menu item that was selected.
     * @return boolean Return false to allow normal context menu processing to proceed, true to consume it here.
     */
    public boolean onContextItemSelected(MenuItem item){
        String func = item.getTitle().toString();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        key = keyList.get(position);
        if(func.contains("delete"))
        {
            refExpenses.child(key).removeValue();
            expenseList.remove(position);
            expenseValues.remove(position);
            keyList.remove(position);
            lvAdp.notifyDataSetChanged();
        }
        else
        {
            Intent si = new Intent(this, MainActivity.class);
            Expense expense = expenseValues.get(position);
            si.putExtra("name", expense.getName());
            si.putExtra("description", expense.getDescription());
            si.putExtra("amount", expense.getAmount());
            si.putExtra("category", expense.getCategory());
            si.putExtra("date", expense.getDate());
            startActivityForResult(si, REQUEST_CODE);
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Inflates the options menu for the activity.
     * <p>
     *
     * @param menu The options menu in which you place your items.
     * @return boolean You must return true for the menu to be displayed; if you return false it will not be shown.
     */
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();
        if(id == R.id.menuInpu)
        {
            Intent it = new Intent(this, MainActivity.class);
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
     * Updates the selected month for sum calculation when a spinner item is selected.
     * <p>
     *
     * @param adapterView The AdapterView where the selection happened.
     * @param view The view within the AdapterView that was clicked.
     * @param i The position of the view in the adapter.
     * @param l The row id of the item that is selected.
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedMonthToSum = i;
    }

    /**
     * Handles cases where no month is selected in the spinner.
     * <p>
     *
     * @param adapterView The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        totalMonthTv.setText("you need to select a month in order to see the month's total expenses");
    }

    /**
     * Updates an expense in Firebase when returning from MainActivity (edit mode).
     * <p>
     *
     * @param source The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     * @param result The integer result code returned by the child activity through its setResult().
     * @param data_back An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    protected void onActivityResult(int source, int result, @Nullable Intent data_back)
    {
        super.onActivityResult(source, result, data_back);
        if(source == REQUEST_CODE)
        {
            if(Activity.RESULT_OK == result)
            {
                if(data_back != null)
                {
                    String name = data_back.getStringExtra("name");
                    String description = data_back.getStringExtra("description");
                    double amount = data_back.getDoubleExtra("amount", -1);
                    String category = data_back.getStringExtra("category");
                    String date = data_back.getStringExtra("date");
                    Expense expense = new Expense(name, description, amount, category, date);
                    refExpenses.child(key).setValue(expense);
                    readDataFromDb();
                }
            }
        }
    }
}
