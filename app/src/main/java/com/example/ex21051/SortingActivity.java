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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sorting);
        connectJavaXml();
    }

    private void connectJavaXml() {
        datePriceSwitch = findViewById(R.id.datePriceSwitch);
        ascDescSwitch = findViewById(R.id.ascDescSwitch);
        sortingLv = findViewById(R.id.sortingLv);
        sortingLv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);
        sortingLv.setAdapter(adp);
    }

    @Override
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