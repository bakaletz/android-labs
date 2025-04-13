package com.example.lab2;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class InputFragment extends Fragment {

    private RadioGroup shapeGroup;
    private CheckBox perimeterCheckbox, areaCheckbox;
    private EditText dimensionInput;
    private Button okButton;

    public InputFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_input, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        shapeGroup = view.findViewById(R.id.shapeGroup);
        perimeterCheckbox = view.findViewById(R.id.perimeterCheckBox);
        areaCheckbox = view.findViewById(R.id.areaCheckBox);
        dimensionInput = view.findViewById(R.id.dimensionInput);
        okButton = view.findViewById(R.id.okButton);

        okButton.setOnClickListener(v -> handleOkButtonClick());
    }

    private void handleOkButtonClick() {
        int selectedShapeId = shapeGroup.getCheckedRadioButtonId();
        String input = dimensionInput.getText().toString();

        if (selectedShapeId == -1 || TextUtils.isEmpty(input)) {
            Toast.makeText(getContext(), "Please insert all the necessary data", Toast.LENGTH_LONG).show();
            return;
        }

        RadioButton selectedShape = getView().findViewById(selectedShapeId);
        String shape = selectedShape.getText().toString();
        double dimension = Double.parseDouble(input);

        StringBuilder result = new StringBuilder("Shape: " + shape + "\n");

        if (areaCheckbox.isChecked()) {
            double area = calculateArea(shape, dimension);
            result.append("Area: ").append(round(area)).append("\n");
        }
        if (perimeterCheckbox.isChecked()) {
            double perimeter = calculatePerimeter(shape, dimension);
            result.append("Perimeter: ").append(round(perimeter)).append("\n");
        }
        if (!areaCheckbox.isChecked() && !perimeterCheckbox.isChecked()) {
            result.append("No calculations selected");
        }

        // Передаємо результат у ResultFragment
        Bundle bundle = new Bundle();
        bundle.putString("result", result.toString());

        ResultFragment resultFragment = new ResultFragment();
        resultFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, resultFragment)
                .addToBackStack(null)
                .commit();
    }

    private double calculateArea(String shape, double d) {
        switch (shape) {
            case "Square": return d * d;
            case "Circle": return Math.PI * d * d;
            case "Triangle": return (d * d * Math.pow(d, 1.0 / 3.0)) / 4;
            default: return 0;
        }
    }

    private double calculatePerimeter(String shape, double d) {
        switch (shape) {
            case "Square": return d * 4;
            case "Circle": return 2 * Math.PI * d;
            case "Triangle": return d * 3;
            default: return 0;
        }
    }

    private BigDecimal round(double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
    }
}
