package graphdraw;

import java.util.ArrayList;

/**
 *
 * @author havra
 */
public class PointsCoordinates {

    private final ArrayList<Integer> axisX;
    private final ArrayList<Integer> axisY;
    //private final int zoom;

    public PointsCoordinates(ArrayList<Integer> axisX, ArrayList<Integer> axisY) {
        this.axisX = axisX;
        this.axisY = axisY;
    //    this.zoom = zoom;
    }

    public void AddToMap(double x, double y) {
            axisX.add((int) (x));
            axisY.add((int) (-y));
    }

    public Integer getAxisXForI(int i) {
        return axisX.get(i);
    }

    public Integer getAxisYForI(int i) {
        return axisY.get(i);
    }
    
    public Integer getArrayListLenght() {
        return axisY.size();
    }
}
