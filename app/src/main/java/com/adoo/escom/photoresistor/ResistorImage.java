package com.adoo.escom.photoresistor;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import ImageAnalysis.Area;
import ImageAnalysis.ColorIdentifier;

public class ResistorImage {

    private int[][] imageMatrix;
    private int[][] regions;
    private int width;
    private int height;
    private Bitmap imageMutable;
    private Bitmap imageOriginal;
    private Bitmap resultBitmap;
    private ArrayList<Area> areas = new ArrayList<>();

    ResistorImage(Bitmap image) {
        applyFilters(image);
    }

    private void applyFilters(Bitmap bitmap) {

        if (OpenCVLoader.initDebug()) {

            Mat rgba = new Mat();
            Utils.bitmapToMat(bitmap, rgba);

            Imgproc.cvtColor(rgba, rgba, Imgproc.COLOR_BGRA2BGR);

            Mat bilateral = rgba.clone();

            Imgproc.bilateralFilter(rgba, bilateral, 20, 80, 30);
//            Imgproc.cvtColor(bilateral, bilateral, Imgproc.COLOR_BGR2GRAY);

            Mat open = bilateral.clone();
            Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 20));
            Imgproc.morphologyEx(bilateral, open, Imgproc.MORPH_CLOSE, element);
            Imgproc.morphologyEx(open, open, Imgproc.MORPH_ERODE, element);

//            Imgproc.cvtColor(open, open, Imgproc.COLOR_RGB2GRAY);
//            Imgproc.Canny(open, open, 80, 100);
//            Imgproc.cvtColor(open, open, Imgproc.COLOR_BGRA2BGR);
//            Mat thresholding = open.clone();
//            Imgproc.adaptiveThreshold(open, thresholding, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 10, 0);

            Mat result = open.clone();
            result.convertTo(result, CvType.CV_8UC3);

            resultBitmap = Bitmap.createBitmap(result.cols(), result.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(result, resultBitmap);
        }

    }

    private void adf(int iterations) {
        float lambda = (float) 0.35;
        int kappa = 100;
        int[][] temp = new int[101][101];

        while (iterations-- > 0) {

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int pixel = imageMatrix[i][j];
                    int N = imageMatrix[i - 1][j] - imageMatrix[i][j];
                    int S = imageMatrix[i + 1][j] - imageMatrix[i][j];
                    int E = imageMatrix[i][j + 1] - imageMatrix[i][j];
                    int W = imageMatrix[i][j - 1] - imageMatrix[i][j];

                    int newPixel = (int) (pixel + lambda * (dif(N, kappa) * N + dif(S, kappa) * S + dif(E, kappa) * E + dif(W, kappa) * W));

                    temp[i - 100][j - 100] = newPixel;
                }
            }

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    imageMatrix[i][j] = temp[i - 100][j - 100];
                }
            }
        }
    }

    private float dif(int color, int filter) {
        int k = (Math.abs(color) / filter);
        return 1 / (1 + k * k);
    }

    private void filterMedia() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                int sum = 0;

                sum += imageMatrix[i - 1][j - 1];
                sum += imageMatrix[i - 1][j];
                sum += imageMatrix[i - 1][j + 1];
                sum += imageMatrix[i][j - 1];
                sum += imageMatrix[i][j];
                sum += imageMatrix[i][j + 1];
                sum += imageMatrix[i + 1][j - 1];
                sum += imageMatrix[i + 1][j];
                sum += imageMatrix[i + 1][j + 1];

                int media = sum / 9;

                imageMatrix[i][j] = media;
            }
        }
    }

    private void filterBlue(int portion) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int[] rgb = getPixelRGB(i, j);
                int gray = (imageMatrix[i][j] >> 16) & (0xff);
                if (rgb[2] < portion) {
                    imageMatrix[i][j] = 0xff000000;
                } else {
                    imageMatrix[i][j] = colorFromRGB(new int[]{255, 255, 255});
                }
            }
        }
    }

    public void filterRed(int portion) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int[] rgb = getPixelRGB(i, j);
                if (rgb[0] < portion) {
                    imageMatrix[i][j] = 0xff000000;
                } else {
                    imageMatrix[i][j] = colorFromRGB(new int[]{255, 255, 255});
                }
            }
        }
    }

    public void filterGreen(int portion) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int[] rgb = getPixelRGB(i, j);
                if (rgb[1] < portion) {
                    imageMatrix[i][j] = 0xff000000;
                } else {
                    imageMatrix[i][j] = colorFromRGB(new int[]{255, 255, 255});
                }
            }
        }
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
                int color = imageOriginal.getPixel(j, i) | (0xff000000);
                imageMatrix[i][j] = (i <= 200 && i >= 100 && j <= 200 && j >= 100) ? color : 0xffffffff;
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
        int R = pixel & 0x00ff0000;
        int G = pixel & 0x0000ff00;
        int B = pixel & 0x000000ff;

        return new int[]{R, G, B};
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private void filterGray(int portion) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int gray = (imageMatrix[i][j] >> 16) & (0xff);
                if (gray < portion) {
                    imageMatrix[i][j] = 0xff000000;
                } else {
                    imageMatrix[i][j] = colorFromRGB(new int[]{255, 255, 255});
                }
            }
        }
    }

    public Bitmap getImage() {
//        filterGray(portion);
//        filterBlue(portion);
//        for (int i = 0; i < height; i++) {
//            for (int j = 0; j < width; j++) {
//                int color = imageMatrix[i][j];
//                imageMutable.setPixel(j, i, color);
//            }
//        }

        return resultBitmap;
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
//