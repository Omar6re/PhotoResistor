package com.adoo.escom.photoresistor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

public class ResistorImage {

    private Bitmap image;
    private String[][] colorMatrix;
    private int width;
    private int height;

    public ResistorImage(Bitmap image){
//        loadImage(imagePath);
        this.image = image;
        width = image.getWidth() - 1;
        height = image.getHeight() - 1;
        createMatrix();
    }

    private void createMatrix() {
        colorMatrix = new String[width][height];
        RICI identifier = new RICI();
        int[] aux = null;

        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                int[] RGB  = getPixelRGB(i, j);
                String color = identifier.getColor(RGB);
                colorMatrix[i][j] = color;
                if(j == 150){
                    aux = RGB;
                }
            }
            int[] g = aux;
        }
    }

    private void loadImage(String imagePath) {
        File f = new File(imagePath);
        image = BitmapFactory.decodeFile(imagePath);
    }

    public int[] getPixelRGB(int x, int y){
        int pixel = image.getPixel(x, y);
        int R = (pixel >> 16) & 0xff;
        int G = (pixel >> 8) & 0xff;
        int B = pixel & 0xff;
        int[] RGB = new int[] {R, G, B};

        return RGB;
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getPixelColor(int x, int y) {
        return colorMatrix[x][y];
    }
}
