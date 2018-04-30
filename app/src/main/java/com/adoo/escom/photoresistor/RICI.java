package com.adoo.escom.photoresistor;

class RICI implements ColorIdentifier {

    private final int[] BROWN = {165, 42, 42};
    private final int[] BLUE = {0, 0, 255};
    private final int[] RED = {255, 0, 0};
    private final int[] ORANGE = {255, 165, 0};
    private final int[] YELLOW = {255, 255, 0};
    private final int[] GREEN = {0, 255, 0};
    private final int[] VIOLET = {238, 130, 238};
    private final int[] GRAY = {128, 128, 128};
    private final int[] WHITE = {255, 255, 255};
    private final int[] BLACK = {0, 0, 0};
    private final int[] GOLD = {255, 215, 0};
    private final int[] SILVER = {192, 192, 192};
    private final int [][] colors = {
            BROWN, BLUE, RED, ORANGE, YELLOW,
            GREEN, VIOLET, GRAY, WHITE, BLACK,
            GOLD, SILVER
    };
    private final String[] colorNames = {
            "BROWN", "BLUE", "RED", "ORANGE", "YELLOW",
            "GREEN", "VIOLET", "GRAY", "WHITE", "BLACK",
            "GOLD", "SILVER"
    };


    @Override
    public String getColor(int[] rgb) {

        String answer = null;
        Boolean val = false;

        for (int j = 0; j < colors.length; j++)
        {

            int[] color = colors[j];

            for (int i = 0; i < 3; i++)
            {
                int ref = Math.abs(rgb[i] - color[i]);
                int ref2 = (int) (color[i] * .8);
                if (ref > ref2)
                {
                    val = false;
                    break;
                }else{
                    val = true;
                }
            }

            if (val)
            {
                answer = colorNames[j];
                break;
            }
        }

        return answer;
    }
}
