package ImageAnalysis;

import java.io.IOException;
import java.io.Serializable;

public class Resistor implements Serializable {
    private String[] colors;
    private int value;
    private int tolerance;
    private int tempco;
    private int numStripes;

    public Resistor(String[] colors, int numStripes) {
        super();
        this.colors = colors;
        this.numStripes = numStripes;
        calculateValues();
    }

    public Resistor(ResistorImage image, int stripes, int[] coords) throws IOException {
        this.colors = image.getStripesColors(stripes, coords);
        this.numStripes = stripes;
        calculateValues();
    }

    private void calculateValues() {

        value = 0;

        int numColors = numStripes == 6 || numStripes == 5 ? 3 : 2;

        for (int i = 0; i < numColors; i++) {

            value += ColorCode.getColorValue(colors[i]) * (int) Math.pow(10, numColors - i - 1);

        }

        int index = numColors;

        value *= (int) Math.pow(10, ColorCode.getColorValue(colors[index++]));

        tolerance = ColorCode.getColorValue(colors[index++]);

        tempco = numStripes == 6 ? ColorCode.getColorValue(colors[index]) : 0;

    }

    public String getValue() {
        String valueStr;

        if (value >= 1e3 && value < 1e6)
            valueStr = String.valueOf(value / 1e3) + " KOhm";
        else if (value >= 1e6) {
            valueStr = String.valueOf(value / 1e6) + " MOhm";
        } else {
            valueStr = String.valueOf(value) + " Ohm";
        }

        return valueStr;
    }

    public int getTolerance() {
        return tolerance;
    }

    public int getTempco() {
        return tempco;
    }

    public int getNumberColors() {
        return numStripes;
    }

    public String[] getColors() {
        return colors;
    }
}
