package ImageAnalysis;

import android.graphics.Bitmap;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ResistorImage {

    private Bitmap filteredImg, copy;
    private int height, width;

    public ResistorImage(Bitmap image) {
        applyFilters(image);
        height = filteredImg.getHeight();
        width = filteredImg.getWidth();
        resetCopy();
    }

    private void resetCopy() {
        copy = filteredImg.copy(filteredImg.getConfig(), true);
    }

    private void applyFilters(Bitmap bitmap) {

        if (OpenCVLoader.initDebug()) {

            Mat rgba = new Mat();
            Utils.bitmapToMat(bitmap, rgba);

            Imgproc.cvtColor(rgba, rgba, Imgproc.COLOR_BGRA2BGR);

            Mat bilateral = rgba.clone();

            Imgproc.bilateralFilter(rgba, bilateral, 20, 80, 30);

            Mat open = bilateral.clone();
            Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 20));
            Imgproc.morphologyEx(bilateral, open, Imgproc.MORPH_CLOSE, element);
            Imgproc.morphologyEx(open, open, Imgproc.MORPH_ERODE, element);

            Mat result = open.clone();
            result.convertTo(result, CvType.CV_8UC3);

            filteredImg = Bitmap.createBitmap(result.cols(), result.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(result, filteredImg);
        }

    }

    private int[] getPixelRGB(int x, int y) {
        int p = filteredImg.getPixel(x, y);
        int R = (p >> 16) & 0xff;
        int G = (p >> 8) & 0xff;
        int B = p & 0xff;

        return new int[]{R, G, B};
    }

    public void setStripeLine(int x) {
        resetCopy();
        for (int i = 0; i < height; i++) {
            copy.setPixel(x, i, 0xff000000);
        }
    }

    public Bitmap getImage() {
        int y = (int) ((height / 2) - (height * 0));
        for (int i = 0; i < width; i++) {
            copy.setPixel(i, y, 0xff000000);
        }
        return copy;
    }

    public int getWidth() {
        return width;
    }

    public String[] getStripesColors(int stripes, int[] coords) {
        String[] colors = new String[stripes];

        for (int i = 0; i < stripes; i++) {
            colors[i] = ColorIdentifier.getColor(getPixelRGB(coords[i], (int) (height / 2 - height * 0)));
        }

        return colors;
    }
}
