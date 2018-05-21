package ImageAnalysis;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ColorIdentifier {

    private final int[] BROWN = {165, 42, 42};
    private final int[] BLUE = {0, 0, 240};
    private final int[] RED = {240, 0, 0};
    private final int[] ORANGE = {240, 165, 0};
    private final int[] YELLOW = {240, 240, 0};
    private final int[] GREEN = {0, 240, 0};
    private final int[] VIOLET = {238, 130, 238};
    private final int[] GRAY = {128, 128, 128};
    private final int[] WHITE = {240, 240, 240};
    private final int[] BLACK = {0, 0, 0};
    private final int[] GOLD = {207, 181, 59};
    private final int[] SILVER = {192, 192, 192};
    private final int[][] colors = {
            BROWN, BLUE, RED, ORANGE, YELLOW,
            GREEN, VIOLET, GRAY, WHITE, BLACK,
            GOLD, SILVER
    };
    private final String[] colorNames = {
            "BROWN", "BLUE", "RED", "ORANGE", "YELLOW",
            "GREEN", "VIOLET", "GRAY", "WHITE", "BLACK",
            "GOLD", "SILVER"
    };

    public int getColor(int[] rgb) {

        ArrayList<Pair<Integer, Integer>> candidates = new ArrayList<>();

        for (int i = 0; i < colors.length; i++) {

            int difference = 0;
            int variation = 0;

            for (int j = 0; j < 3; j++) {

                variation += Math.abs(rgb[j] - colors[i][j]);
                difference += variation;

//                if (variation > 20) {
//                    isEqual = false;
//                }
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

        return colorFromRGB(colors[p.first]);
    }

    private int colorFromRGB(int[] rgb) {
        return (0xff) << 24 | (rgb[1] & 0x0ff) << 8 | ((rgb[0] & 0x0ff) << 16) | (rgb[2] & 0x0ff);
    }
}
