package ImageAnalysis;

import java.util.HashMap;
import java.util.Map;

class ColorCode {

    private static final Map<String,Integer> table = createMap();

    private static Map<String, Integer> createMap() {

        Map<String,Integer> map = new HashMap<String,Integer>();

        map.put("BLACK", 0);
        map.put("BROWN", 1);
        map.put("RED", 2);
        map.put("ORANGE", 3);
        map.put("YELLOW", 4);
        map.put("GREEN", 5);
        map.put("BLUE", 6);
        map.put("VIOLET", 7);
        map.put("GREY", 8);
        map.put("WHITE", 9);
        map.put("GOLD", 5);
        map.put("SILVER", 10);

        return map;

    }

    public static int getColorValue(String color){
        return (int) ((Map) table).get(color);
    }

}
