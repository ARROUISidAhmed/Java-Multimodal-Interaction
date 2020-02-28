/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivy;

import java.awt.List;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Objects;
import org.graalvm.compiler.nodes.calc.IsNullNode;

/**
 *
 * @author boura
 */
public class DeleteCommand implements Command {

    private ArrayList<String> shapes;

    public DeleteCommand(ArrayList<String> shapes) {
        this.shapes = shapes;
    }

    public DeleteCommand() {

    }

    public ArrayList<String> getShapes() {
        return shapes;
    }

    public void setShapes(ArrayList<String> shapes) {
        this.shapes = shapes;
    }

    public void addShape(String shape) {
        this.shapes.add(shape);  
    }

    @Override
    public boolean isWellFormated() {
        return !(this.shapes.isEmpty() || Objects.isNull(shapes));
    }

}
