package com.adoo.escom.photoresistor;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class ResultsActivity extends AppCompatActivity {

    private TextView color1;
    private TextView color2;
    private TextView color3;
    private TextView multiplier;
    private TextView tolerance;
    private TextView tempco;
    private TextView result;

    Resistor resistor;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Get results labels.
        color1 = findViewById(R.id.lblColor1);
        color2 = findViewById(R.id.lblColor2);
        color3 = findViewById(R.id.lblColor3);
        multiplier = findViewById(R.id.lblMultiplier);
        tolerance = findViewById(R.id.lblTolerance);
        tempco = findViewById(R.id.lblTempco);
        result = findViewById(R.id.lblResult);

        // Retrieve data.
        Bundle data = getIntent().getExtras();

        assert data != null;
        resistor = (Resistor) data.get("RESISTOR");

        // Set results.
        setLabels();
        assert resistor != null;
        Integer value = resistor.getValue();
        result.setText(value.toString() + "ohms");
    }

    private void setLabels() {
        int numberColors = resistor.getNumberColors();
        String[] colors = resistor.getColors();

        if(colors != null)
        {
            switch (numberColors)
            {
                case 4:

                    color1.setText(colors[0]);
                    color2.setText(colors[1]);
                    multiplier.setText(colors[2]);
                    tolerance.setText(colors[3]);

                    break;

                case 5:

                    color1.setText(colors[0]);
                    color2.setText(colors[1]);
                    color3.setText(colors[2]);
                    multiplier.setText(colors[3]);
                    tolerance.setText(colors[4]);

                    break;

                case 6:

                    color1.setText(colors[0]);
                    color2.setText(colors[1]);
                    color3.setText(colors[2]);
                    multiplier.setText(colors[3]);
                    tolerance.setText(colors[4]);
                    tempco.setText(colors[5]);

                    break;
            }
        } else{
            Toast.makeText(ResultsActivity.this, "No entró", Toast.LENGTH_SHORT).show();
        }
    }
}
