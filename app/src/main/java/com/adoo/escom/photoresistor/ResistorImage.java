package com.adoo.escom.photoresistor;

import ImageAnalysis.Area;
import ImageAnalysis.ColorIdentifier;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class ResistorImage {

    private int[][] imageMatrix;
    private int[][] regions;
    private int width;
    private int height;
    private Bitmap imageMutable;
    private ArrayList<Area> areas = new ArrayList<>();

    ResistorImage(Bitmap image) throws IOException {
        width = image.getWidth();
        height = image.getHeight();
        imageMutable = getMutableImage(image);

        createImageMatrix();

        // Round pixel values.
        roundPixelValues();
        findRegions();
        roundToColors();
//        findRegions();
    }

    private void roundToColors() {
        ColorIdentifier identifier = new ColorIdentifier();

//        for (int i = 0; i < height; i++) {
//            for (int j = 0; j < width; j++) {
//                int[] rgb = getPixelRGB(i, j);
//                int color = identifier.getColor(rgb);
//                setPixelColor(i, j, color);
//            }
//        }

        for (Area a : areas) {
            int[] coords = a.getCoords();
            int[] rgb = getPixelRGB(coords[0], coords[1]);
            int color = identifier.getColor(rgb);
            int id = a.getId();
            setRegionColor(id, coords[0], coords[1], color);
        }
    }

    private void setRegionColor(int id, int i, int j, int color) {
        if (i >= 0 && i < height && j >= 0 && j < width) {
            if (regions[i][j] != id)
                return;
            // Set region color.
            setPixelColor(i, j, color);
            regions[i][j]--;

            // Recursive call for adjacent cells.
            setRegionColor(id, i - 1, j, color);
            setRegionColor(id, i, j - 1, color);
            setRegionColor(id, i, j + 1, color);
            setRegionColor(id, i + 1, j, color);
        }
    }

    private void createImageMatrix() {
        imageMatrix = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int color = imageMutable.getPixel(j, i);
                imageMatrix[i][j] = color;
            }
        }
    }

    private void findRegions() {
        regions = new int[height][width];
        int region = 2;

//        for (int i = 0; i < height; i++) {
        int i = height / 2;
        for (int j = 0; j < width; j++) {
            int[] rgb = getPixelRGB(i, j);
            int pixelsArea = setRegion(i, j, region, rgb, 0);

            if (pixelsArea > 0) {
                Area area = new Area(region, pixelsArea, i, j);
                areas.add(area);
                region++;
            }
//            }
        }
        System.out.println("SIZE: " + areas.size());
    }

    private int setRegion(int i, int j, int region, int[] rgb, int sum) {
        int area = sum;

        if (i >= 0 && i < height && j >= 0 && j < width) {
            if (regions[i][j] != 0)
                return area;

            boolean isEqual = true;
            int[] rgb2 = getPixelRGB(i, j);

            // Check for variation in rgb minor to 10.
            for (int x = 0; isEqual && x < 3; x++) {
                if (Math.abs(rgb[x] - rgb2[x]) > 20) {
                    isEqual = false;
                }
            }

            if (isEqual) {
                // Set region.
                regions[i][j] = region;
                area++;

                // Recursive call for adjacent cells.
                area = setRegion(i - 1, j, region, rgb, area);
                area = setRegion(i, j - 1, region, rgb, area);
                area = setRegion(i, j + 1, region, rgb, area);
                area = setRegion(i + 1, j, region, rgb, area);
            }
        }

        return area;
    }

    private int colorFromRGB(int[] rgb) {
        return (0xff) << 24 | (rgb[1] & 0x0ff) << 8 | ((rgb[0] & 0x0ff) << 16) | (rgb[2] & 0x0ff);
    }

    private void roundPixelValues() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int[] rgb = getPixelRGB(i, j);

                for (int x = 0; x < 3; x++) {
                    rgb[x] = (rgb[x] / 10) * 10;
                }

                int aux = imageMatrix[i][j];
                int color = colorFromRGB(rgb);
                if (aux != -1 && aux - color != 0) {
                    int o = 0;
                }
                setPixelColor(i, j, color);
            }
        }
    }

    private void setPixelColor(int x, int y, int color) {
        imageMatrix[x][y] = color;
    }

    private int[] getPixelRGB(int x, int y) {
        int pixel = imageMatrix[x][y];
        int R = (pixel >> 16) & 0xff;
        int G = (pixel >> 8) & 0xff;
        int B = pixel & 0xff;

        return new int[]{R, G, B};
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Bitmap getImage() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int color = imageMatrix[i][j];
                imageMutable.setPixel(j, i, color);
            }
        }

        return imageMutable;
    }

    private Bitmap getMutableImage(Bitmap image) throws IOException {

        //this is the file going to use temporally to save the bytes.
        @SuppressLint("SdCardPath") File file = new File("/mnt/sdcard/sample/temp.txt");
        file.getParentFile().mkdirs();

        //Open an RandomAccessFile
        /*Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        into AndroidManifest.xml file*/
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

        // get the width and height of the source bitmap.
        int width = image.getWidth();
        int height = image.getHeight();

        //Copy the byte to the file
        //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
        FileChannel channel = randomAccessFile.getChannel();
        MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, width * height * 4);
        image.copyPixelsToBuffer(map);
        //recycle the source bitmap, this will be no longer used.
        Bitmap imageCopy;
        //Create a new bitmap to load the bitmap again.
        imageCopy = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        map.position(0);
        //load it back from temporary
        imageCopy.copyPixelsFromBuffer(map);
        //close the temporary file and channel , then delete that also
        channel.close();
        randomAccessFile.close();

        return imageCopy;
    }
}
