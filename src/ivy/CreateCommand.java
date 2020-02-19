/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivy;

import java.awt.Point;
import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author arrouisa
 */
public class CreateCommand implements Command {

    private final String shape;
    private Point position;
    private String color;

    public CreateCommand(String shape, Point position, String color) {
        this.shape = shape;
        this.position = position;
        this.color = color;
    }

    public CreateCommand(String shape) {
        this.shape = shape;
        this.position = new Point(0, 0);
        this.color = "red";
    }

    public String getShape() {
        return shape;
    }

    
    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public boolean isWellFormated() {
        return !Objects.isNull(shape);
    }

    void setPosition(String position) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
