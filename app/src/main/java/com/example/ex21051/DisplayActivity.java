package com.example.ex21051;

import static com.example.ex21051.FBref.refExpenses;

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
    ValueEventListener VEL;
    int selectedMonthToSum;
    ArrayList<String> expenseList = new ArrayList<String>();
    ArrayList<Expense> expenseValues = new ArrayList<Expense>();
    ArrayList<String> keyList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        connectJavaXml();
        configureVEL();
        readDataFromDb();
    }

    private void readDataFromDb() {
        refExpenses.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                expenseList.clear();
                expenseValues.clear();
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

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add("delete expense");
        menu.add("update expense");
    }

    public boolean onContextItemSelected(MenuItem item){
        String func = item.getTitle().toString();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        String key = keyList.get(position);
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
            //TODO: add update functions
        }
        return super.onContextItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedMonthToSum = i;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        totalMonthTv.setText("you need to select a month in order to see the month's total expenses");
    }
}