package com.adoo.escom.photoresistor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.io.Serializable;

import ImageAnalysis.Resistor;

public class ManualInputActivity extends AppCompatActivity {

    private Spinner color1;
    private Spinner color2;
    private Spinner color3;
    private Spinner multiplier;
    private Spinner tolerance;
    private Spinner tempco;

    private int numberStripes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_input);

        numberStripes = 4; // Set default value.

        // Get spinners.
        color1 = findViewById(R.id.selectionColor1);
        color2 = findViewById(R.id.selectionColor2);
        color3 = findViewById(R.id.selectionColor3);
        multiplier = findViewById(R.id.selectionMultiplier);
        tolerance = findViewById(R.id.selectionTolerance);
        tempco = findViewById(R.id.selectionTempco);

        // Get calculate button.
        Button calculate = findViewById(R.id.btnCalculate);

        // Listener for calculate button.
        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] colors = getColors();
                Resistor r = new Resistor(colors, numberStripes);
                Intent result = new Intent(ManualInputActivity.this, ResultsActivity.class);
                result.putExtra("RESISTOR", (Serializable) r);
                startActivity(result);
            }
        });

        // Disable color 5 and 6 spinners.
        color3.setEnabled(false);
        tempco.setEnabled(false);

        // Initialize listener to select number of spinners.
        final RadioGroup numberColors = findViewById(R.id.radioBtnNumColor);
        numberColors.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Spinner color3 = findViewById(R.id.selectionColor3);
                Spinner tempco = findViewById(R.id.selectionTempco);

                switch (i){

                    case R.id.radioBtnColors4:

                        color3.setEnabled(false);
                        tempco.setEnabled(false);
                        numberStripes = 4;

                        break;

                    case R.id.radioBtnColors5:

                        color3.setEnabled(true);
                        tempco.setEnabled(false);
                        numberStripes = 5;

                        break;

                    case R.id.radioBtnColors6:

                        color3.setEnabled(true);
                        tempco.setEnabled(true);
                        numberStripes = 6;

                        break;
                }
            }
        });


    }

    private String[] getColors() {
        RadioGroup selectionNumberColors = findViewById(R.id.radioBtnNumColor);
        int numberColors = selectionNumberColors.getCheckedRadioButtonId();
        String[] colors = null;

        switch (numberColors){

            case R.id.radioBtnColors4:

                colors = new String[4];
                colors[0] = color1.getSelectedItem().toString().toUpperCase();
                colors[1] = color2.getSelectedItem().toString().toUpperCase();
                colors[2] = multiplier.getSelectedItem().toString().toUpperCase();
                colors[3] = tolerance.getSelectedItem().toString().toUpperCase();

                break;

            case R.id.radioBtnColors5:

                colors = new String[5];
                colors[0] = color1.getSelectedItem().toString().toUpperCase();
                colors[1] = color2.getSelectedItem().toString().toUpperCase();
                colors[2] = color3.getSelectedItem().toString().toUpperCase();
                colors[3] = multiplier.getSelectedItem().toString().toUpperCase();
                colors[4] = tolerance.getSelectedItem().toString().toUpperCase();

                break;

            case R.id.radioBtnColors6:

                colors = new String[6];
                colors[0] = color1.getSelectedItem().toString().toUpperCase();
                colors[1] = color2.getSelectedItem().toString().toUpperCase();
                colors[2] = color3.getSelectedItem().toString().toUpperCase();
                colors[3] = multiplier.getSelectedItem().toString().toUpperCase();
                colors[4] = tolerance.getSelectedItem().toString().toUpperCase();
                colors[5] = tempco.getSelectedItem().toString().toUpperCase();

                break;
        }

        return colors;
    }
}
