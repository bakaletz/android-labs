package com.example.figurevaluesfinder;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MainActivity extends AppCompatActivity {

    private RadioGroup shapeGroup;
    private CheckBox perimeterCheckbox, areaCheckbox;
    private EditText dimensionInput;
    private TextView resultText;
    private Button okButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shapeGroup = findViewById(R.id.shapeGroup);
        perimeterCheckbox = findViewById(R.id.perimeterCheckBox);
        areaCheckbox = findViewById(R.id.areaCheckBox);
        dimensionInput = findViewById(R.id.dimensionInput);
        resultText = findViewById(R.id.resultText);
        okButton = findViewById(R.id.okButton);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleOkButtonClick();
            }
        });

    }

    private void handleOkButtonClick(){
        int selectedShapeId = shapeGroup.getCheckedRadioButtonId();
        if (selectedShapeId == -1 || dimensionInput.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please insert all the necessary data", Toast.LENGTH_LONG).show();
            return;
        }

        RadioButton selectedShape = findViewById(selectedShapeId);
        String shape = selectedShape.getText().toString();
        double dimension = Double.parseDouble(dimensionInput.getText().toString());
        StringBuilder result = new StringBuilder("Shape: " + shape + "\n");

        if (areaCheckbox.isChecked()) {
            double area = calculateArea(shape, dimension);
            BigDecimal roundedArea = new BigDecimal(area).setScale(2, RoundingMode.HALF_UP);
            result.append("Area: ").append(roundedArea).append("\n");
        }
        if (perimeterCheckbox.isChecked()){
            double perimeter = calculatePerimeter(shape,dimension);
            BigDecimal roundedPerimeter = new BigDecimal(perimeter).setScale(2, RoundingMode.HALF_UP);
            result.append("Perimeter: ").append(roundedPerimeter).append("\n");
        }
        if (!areaCheckbox.isChecked() && !perimeterCheckbox.isChecked()){
            result.append("No calculations selected");
        }
        resultText.setText(result.toString());
    }

    private double calculateArea(String shape, double dimension){
        switch(shape){
            case "Square":
                return dimension*dimension;
            case "Circle":
                return Math.PI* dimension * dimension;
            case "Triangle":
                return (dimension*dimension * Math.pow(dimension, 1.0/3.0))/4;
            default: return 0;
        }
    }
    private double calculatePerimeter(String shape, double dimension){
        switch(shape){
            case "Square":
                return dimension*4;
            case "Circle":
                return Math.PI * dimension * 2;
            case "Triangle":
                return dimension * 3;
            default: return 0;
        }
    }
}