package com.adoo.escom.photoresistor;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Resistor implements Serializable{
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

    public Resistor(Bitmap image, int numStripes){
        super();

        this.numStripes = numStripes;
        ResistorImage img = new ResistorImage(image);
        int width = img.getWidth();
        int height = img.getHeight();
        RICI identifier = new RICI();

        colors = new String[numStripes];

        for(int i = 0; i < numStripes; i++){
            int x = (int) ((width / (numStripes - i)) - (width * 0.04));
            int y = height / 2;

//            int[] rgb = img.getPixelRGB(x, y);
//            String color = identifier.getColor(rgb);
            String color = img.getPixelColor(x, y);
            colors[i] = color;
        }

        calculateValues();
    }

    private void calculateValues() {

        value = 0;

        int numColors = numStripes==6 || numStripes==5 ? 3 : 2;

        for(int i = 0;  i < numColors; i++){

            value += ColorCode.getColorValue(colors[i]) * (int)Math.pow(10, numColors - i - 1);

        }

        int index = numColors;

        value *= (int)Math.pow(10, ColorCode.getColorValue(colors[index++]));

        tolerance = ColorCode.getColorValue(colors[index++]);

        tempco = numStripes==6 ? ColorCode.getColorValue(colors[index]) : 0;

    }

    public int getValue() {
        return value;
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
