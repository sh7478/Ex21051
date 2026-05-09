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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtering);
        connectJavaXml();
        fillSpinner();
    }

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

    private void fillSpinner() {
        categories = getResources().getStringArray(R.array.categories);
        ArrayAdapter<String> adp = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, categories);
        filterSpinCat.setAdapter(adp);

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