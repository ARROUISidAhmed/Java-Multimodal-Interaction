/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivy;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author arrouisa
 */
public class MoveCommand implements Command {

    private ArrayList<String> shapes;
    private Point position;

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public MoveCommand() {
        this.shapes = new ArrayList<>();
        
    }

    public ArrayList<String> getShapes() {
        return shapes;
    }

    public void setShapes(ArrayList<String> shapes) {
        this.shapes = shapes;
    }

    @Override
    public boolean isWellFormated() {
        return !(Objects.isNull(shapes) || this.shapes.isEmpty() || Objects.isNull(position));
    }
}
