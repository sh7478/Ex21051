package com.example.ex21051;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FilteringActivity extends AppCompatActivity {

    Switch priceCatSwitch;
    Spinner filterSpinCat;
    EditText eTMinPrice, eTMaxPrice;
    ListView filteringLv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtering);
        connectJavaXml();
    }

    private void connectJavaXml() {
        priceCatSwitch = findViewById(R.id.priceCatSwitch);
        filterSpinCat = findViewById(R.id.filterSpinCat);
        eTMinPrice = findViewById(R.id.eTMinPrice);
        eTMaxPrice = findViewById(R.id.eTMaxPrice);
        filteringLv = findViewById(R.id.filteringLv);
        filteringLv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);
        filteringLv.setAdapter(adp);
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
}