/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivy;

import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author boura
 */
public class DeleteCommand implements Command {

    private ArrayList<String> shapes;


    public DeleteCommand() {
       
    }

    public ArrayList<String> getShapes() {
        return shapes;
    }

    public void setShapes(ArrayList<String> shapes) {
        this.shapes = shapes;
    }


    @Override
    public boolean isWellFormated() {
        return !( Objects.isNull(shapes) || this.shapes.isEmpty() );
    }

}
