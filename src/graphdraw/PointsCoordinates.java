package graphdraw;

import java.util.ArrayList;

/**
 *
 * @author havra
 */
public class PointsCoordinates {

    private ArrayList<Integer> axisX= new ArrayList<>();
    private ArrayList<Integer> axisY = new ArrayList<>();

    public PointsCoordinates(ArrayList<Integer> axisX, ArrayList<Integer> axisY) {
        this.axisX = axisX;
        this.axisY = axisY;
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
