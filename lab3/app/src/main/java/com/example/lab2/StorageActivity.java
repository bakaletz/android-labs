package com.example.lab2;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class StorageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        TextView storageText = findViewById(R.id.storageText);
        ResultDatabaseHelper dbHelper = new ResultDatabaseHelper(this);
        List<String> results = dbHelper.getAllResults();

        if (results.isEmpty()) {
            storageText.setText("Storage is empty");
        } else {
            StringBuilder builder = new StringBuilder();
            for (String res : results) {
                builder.append(res).append("\n\n");
            }
            storageText.setText(builder.toString());
        }
    }
}
