package ImageAnalysis;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ColorIdentifier {

    static final int[] BROWN = {139, 69, 19};
    static final int[] BLUE = {20, 20, 125};
    static final int[] RED = {125, 20, 20};
    static final int[] ORANGE = {255, 165, 20};
    static final int[] YELLOW = {230, 230, 20};
    static final int[] GREEN = {50, 125, 50};
    static final int[] VIOLET = {128, 20, 128};
    static final int[] GREY = {128, 128, 128};
    static final int[] WHITE = {255, 255, 255};
    static final int[] BLACK = {20, 20, 20};
    static final int[] GOLD = {190, 150, 20};
    static final int[] SILVER = {192, 192, 192};
    static final int[][] colors = {
            BROWN, BLUE, RED, ORANGE, YELLOW,
            GREEN, VIOLET, GREY, WHITE, BLACK,
            GOLD, SILVER
    };
    private static final String[] colorNames = {
            "BROWN", "BLUE", "RED", "ORANGE", "YELLOW",
            "GREEN", "VIOLET", "GREY", "WHITE", "BLACK",
            "GOLD", "SILVER"
    };

    public static String getColor(int[] rgb) {

        ArrayList<Pair<Integer, Integer>> candidates = new ArrayList<>();

        for (int i = 0; i < colors.length; i++) {

            int difference = 0;
//            int variation = 0;

            for (int j = 0; j < 3; j++) {
                int variation = Math.abs(rgb[j] - colors[i][j]);
                difference += variation;
            }

            Pair<Integer, Integer> p = new Pair<>(i, difference);
            candidates.add(p);
        }

        Collections.sort(candidates, new Comparator<Pair<Integer, Integer>>() {
            @Override
            public int compare(Pair<Integer, Integer> o1, Pair<Integer, Integer> o2) {
                return o1.second - o2.second;
            }
        });

        Pair<Integer, Integer> p = candidates.get(0);

        return colorNames[p.first];
    }

    public static int colorFromRGB(int[] rgb) {
        return (0xff) << 24 | (rgb[1] & 0x0ff) << 8 | ((rgb[0] & 0x0ff) << 16) | (rgb[2] & 0x0ff);
    }
}
