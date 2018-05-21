package ImageAnalysis;

public class Area {
    private int id;
    // Total pixels in the area.
    private int area;
    // x and y coords that reference areas location.
    private int[] coords = new int[2];

    public Area(int id, int area, int x, int y){
        this.id = id;
        this.area = area;
        this.coords[0] = x;
        this.coords[1] = y;
    }

    public int getArea() {
        return area;
    }

    public int[] getCoords() {
        return coords;
    }

    public int getId() {
        return id;
    }
}
