/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivy;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyException;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

/**
 *
 * @author arrouisa
 */
public class Multimodal extends javax.swing.JFrame {

    private int receivedResponse;

    private void handleMouseClicked(int x, int y) {
        switch (applicationState) {
            case Init:
                /**
                 * Do nothing
                 */
                break;
            case Créer:
                applicationState = ApplicationState.ClickCréer;
                clickedPoint = new Point(x, y);

                timer.restart();
                break;
            case PositionCréer:
                applicationState = ApplicationState.Créer;

                ((CreateCommand) command).setPosition(new Point(x, y));
                timer.restart();
                break;
            case ClickCréer:
                /**
                 * Ne rien faire
                 */
                break;
            case CouleurCréer:
                applicationState = ApplicationState.InfoCréer;
                 {
                    try {
                        askInfo(x, y);
                    } catch (IvyException ex) {
                        Logger.getLogger(Multimodal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                timer.restart();
                break;
            case Supprimer:
                applicationState = ApplicationState.ClickSupp;
                clickedPoint = new Point(x, y);
                timer.restart();
                break;
            case ObjetSupp:
                applicationState = ApplicationState.InfoSupp;
                clickedPoint = new Point(x, y);
                shapesByColor.clear();
                 {
                    try {
                        askInfo(x, y);
                    } catch (IvyException ex) {
                        Logger.getLogger(Multimodal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                timer.restart();
                break;
            case Déplacer:
                applicationState = ApplicationState.ClickDéplacer;
                clickedPoint = new Point(x, y);
                timer.restart();
                break;
            case ObjetDéplacer:
                applicationState = ApplicationState.InfoDéplacer;
                clickedPoint = new Point(x, y);
                shapesByColor.clear();
                 {
                    try {
                        askInfo(x, y);
                    } catch (IvyException ex) {
                        Logger.getLogger(Multimodal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                timer.restart();
                break;
            case PositionDéplacer:
                applicationState = ApplicationState.Déplacer;
                clickedPoint = new Point(x, y);
                ((MoveCommand) command).setPosition(clickedPoint);
                timer.restart();
                break;
            case CouleurDéplacer:
                applicationState = ApplicationState.ClickDéplacer;
                clickedPoint = new Point(x, y);
                timer.restart();
                break;
            case CouleurSupp:
                applicationState = ApplicationState.ClickSupp;
                clickedPoint = new Point(x, y);
                timer.restart();
                break;

        }
    }

    private void handlePosition(String position) {

        switch (applicationState) {
            case Init:
                /**
                 * Do nothing
                 */
                break;
            case Créer:
                /**
                 * TODO Change position
                 */

                applicationState = ApplicationState.PositionCréer;
                timer.restart();
                break;
            case ClickCréer:
                applicationState = ApplicationState.Créer;
                ((CreateCommand) command).setPosition(clickedPoint);
                timer.restart();
                break;
            case Déplacer:
                applicationState = ApplicationState.PositionDéplacer;
                timer.restart();
                break;
            case ClickDéplacer:
                applicationState = ApplicationState.Déplacer;
                ((MoveCommand) command).setPosition(clickedPoint);
                timer.restart();
                break;
            case CouleurDéplacer:
                applicationState = ApplicationState.PositionDéplacer;
                timer.restart();
                break;   
            default:

                /**
                 * Do nothing
                 */
                break;

        }
    }

    private void handleCouleur(String couleur) {
        switch (applicationState) {
            case Init:
                /**
                 * Do nothing
                 */
                break;
            case Créer:
                if (couleur.equals("de cette couleur")) {
                    applicationState = ApplicationState.CouleurCréer;
                } else {
                    applicationState = ApplicationState.Créer;
                    ((CreateCommand) command).setColor(couleur);
                }
                timer.restart();
                break;
            case ClickCréer:
                if (couleur.equals("de cette couleur")) {
                    applicationState = ApplicationState.InfoCréer;
                    try {;
                        askInfo(clickedPoint.x, clickedPoint.y);
                    } catch (IvyException ex) {
                        Logger.getLogger(Multimodal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                timer.restart();
                break;
            case CouleurCréer:
                /**
                 * Ne rien faire
                 */
                break;
            case CouleurSupp:
                if (!couleur.equals("de cette couleur")) {
                    applicationState = ApplicationState.Supprimer;
                    ((DeleteCommand) command).setShapes(shapesByColor.get(mapColor(couleur)));
                    timer.restart();
                }
                break;
            case InfoCréer:
                applicationState = ApplicationState.Créer;

                ((CreateCommand) command).setColor(couleur);
                timer.restart();
                break;
            case CouleurDéplacer:
                if (!couleur.equals("de cette couleur")) {
                    applicationState = ApplicationState.Déplacer;
                    ((MoveCommand) command).setShapes(shapesByColor.get(mapColor(couleur)));
                    timer.restart();
                }
                break;
        }
    }

    private void handleTimeOut(ActionEvent e) {
        switch (applicationState) {
            case Init:
                /**
                 * Do nothing
                 */
                break;
            case Créer:
                applicationState = ApplicationState.Init;

                if (command.isWellFormated()) {
                    CreateCommand cc = ((CreateCommand) command);
                    String shape = cc.getShape().equals("rectangle")
                            ? "Rectangle" : "Ellipse";
                    String color = mapColor(cc.getColor());
                    try {
                        bus.sendMsg("Palette:Creer" + shape + " x="
                                + cc.getPosition().x
                                + " y="
                                + cc.getPosition().y
                                + " longueur=100 hauteur=50 couleurFond="
                                + color + " couleurContour="
                                + color);
                    } catch (IvyException ex) {
                        Logger.getLogger(Multimodal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                timer.stop();
                break;
            case Supprimer:
                applicationState = ApplicationState.Init;

                if (command.isWellFormated()) {
                    DeleteCommand dc = ((DeleteCommand) command);

                    dc.getShapes().forEach((shape) -> {
                        try {
                            bus.sendMsg("Palette:SupprimerObjet nom=" + shape);
                        } catch (IvyException ex) {
                            Logger.getLogger(Multimodal.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                }

                timer.stop();
                break;
            case ClickCréer:
            case CouleurCréer:
            case InfoCréer:
                applicationState = ApplicationState.Créer;
                timer.restart();
                break;
            case ClickSupp:
            case InfoSupp:
            case CouleurSupp:
           
                applicationState = ApplicationState.Supprimer;
                ArrayList<String> allShapes = new ArrayList<>();
                shapesByColor.values().forEach((list) -> allShapes.addAll(list));
                ((DeleteCommand) command).setShapes(allShapes);
                timer.restart();
                break;
            case Déplacer:
                applicationState = ApplicationState.Init;
                if (command.isWellFormated()) {
                    MoveCommand mc = ((MoveCommand) command);

                    mc.getShapes().forEach((shape) -> {
                        try {
                            bus.sendMsg("Palette:DeplacerObjetAbsolu nom=" + shape + " x=" + mc.getPosition().x + " y=" + mc.getPosition().y);
                        } catch (IvyException ex) {
                            Logger.getLogger(Multimodal.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                }

                timer.stop();
                break;
            case ClickDéplacer:
            case InfoDéplacer:
            case CouleurDéplacer:
            case PositionDéplacer:
                applicationState = ApplicationState.Déplacer;
                ArrayList<String> allShapesToMove = new ArrayList<>();
                shapesByColor.values().forEach((list) -> allShapesToMove.addAll(list));
                System.out.println("shapes " + allShapesToMove);
                ((MoveCommand) command).setShapes(allShapesToMove);
                timer.restart();
                break;

        }
    }

    private double compareTwoPatterns(ArrayList<Point2D.Double> liste1, ArrayList<Point2D.Double> liste2) {
        double distance = 0;
        for (int i = 0; i < liste1.size(); i++) {
            distance += distanceTwoPoints(liste1.get(i), liste2.get(i));

        }

        return distance / liste1.size();
    }

    private double distanceTwoPoints(Point2D.Double p1, Point2D.Double p2) {
        return Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
    }

    private void handleGesture(String name) {

        switch (applicationState) {
            case Init:
                objectType = "O";
                switch (name) {
                    case "supprimer":
                        /**
                         * Go to suppression state
                         */
                        applicationState = ApplicationState.Supprimer;
                        command = new DeleteCommand();

                        timer.start();
                        break;
                    case "deplacer":
                        /**
                         * Go to move state
                         */
                        command = new MoveCommand();
                        applicationState = ApplicationState.Déplacer;
                        timer.start();
                        break;
                    case "rectangle":
                    case "cercle":
                        /**
                         * Go to create state
                         */
                        applicationState = ApplicationState.Créer;
                        command = new CreateCommand(name);
                        timer.start();
                        break;

                }
                break;
            default:
                /**
                 * Ne rien faire
                 */
                break;
        }
    }

    private ApplicationState applicationState;

    private Command command;
    private Point clickedPoint;
    private String objectType;

    private void askInfo(int x, int y) throws IvyException {
        bus.sendMsg("Palette:TesterPoint x=" + x + " y=" + y);

    }

    private String mapColor(String color) {
        switch (color) {
            case "rouge":
                return "red";
            case "vert":
                return "green";
            case "jaune":
                return "yellow";
            default:
                return color;
        }
    }

    private void handleObject(String keyValue) {

        switch (applicationState) {
            case Supprimer:
                applicationState = ApplicationState.ObjetSupp;
                setObjectType(keyValue);
                timer.restart();
                break;
            case ClickSupp:
                applicationState = ApplicationState.InfoSupp;
                shapesByColor.clear();
                setObjectType(keyValue);

                 {
                    try {
                        askInfo(clickedPoint.x, clickedPoint.y);
                    } catch (IvyException ex) {
                        Logger.getLogger(Multimodal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                timer.restart();
                break;
            case Déplacer:
                applicationState = ApplicationState.ObjetDéplacer;
                setObjectType(keyValue);
                timer.restart();
                break;
            case ClickDéplacer:
                applicationState = ApplicationState.InfoDéplacer;
                shapesByColor.clear();
                setObjectType(keyValue);

                 {
                    try {
                        askInfo(clickedPoint.x, clickedPoint.y);
                    } catch (IvyException ex) {
                        Logger.getLogger(Multimodal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                timer.restart();
                break;
        }
    }

    private void setObjectType(String keyValue) {
        switch (keyValue) {
            case "ces ellipses":
                objectType = "E";
                break;
            case "ces rectangles":
                objectType = "R";
                break;
            case "ces objets":
                objectType = "O";
                break;

        }
    }

    private enum ApplicationState {
        Init,
        Créer,
        PositionCréer,
        ClickCréer,
        CouleurCréer,
        InfoCréer,
        Supprimer,
        CouleurSupp,
        ClickSupp,
        ObjetSupp,
        InfoSupp,
        Déplacer,
        ClickDéplacer,
        ObjetDéplacer,
        CouleurDéplacer,
        InfoDéplacer,
        PositionDéplacer,
    }

    private enum GestureState {
        Init,
        Point,
        Draw,
    };

    private Ivy bus;

    private Timer timer;
    private Stroke stroke;
    private HashMap<String, ArrayList<String>> shapesByColor;
    private List<String> shapes;

    /**
     * Creates new form Multimodal
     *
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public Multimodal() throws IOException, ClassNotFoundException {
        initComponents();
        initProperties();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Multimodal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new Multimodal().setVisible(true);
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(Multimodal.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private void initProperties() throws IOException, ClassNotFoundException {

        /**
         * Initialize states
         *
         */
        applicationState = ApplicationState.Init;
        /**
         * Initialize properties
         */
        timer = new Timer(2000, (e) -> {
            handleTimeOut(e);
        });
        stroke = new Stroke();
        objectType = "O";
        shapesByColor = new HashMap<>();
        shapes = new ArrayList<>();
        /**
         * Declare Ivy bus, start it and bind messages to event handlers
         */
        bus = new Ivy("Ivy_Sender", null, null);

        try {
            bus.start("127.255.255.255:2010");
        } catch (IvyException ex) {
            Logger.getLogger(Geste.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        /**
         * Palette events such as Mouse Drag and Click
         */
        try {
            bus.bindMsg("Geste:(.*)", (IvyClient ic, String[] strings) -> {
                String geste = strings[0];
                handleGesture(geste);
            });

        } catch (IvyException ex) {
            Logger.getLogger(Geste.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        try {
            bus.bindMsg("Palette:MouseClicked x=(.*) y=(.*)", (IvyClient ic, String[] strings) -> {
                int x = Integer.parseInt(strings[0]);
                int y = Integer.parseInt(strings[1]);
                handleMouseClicked(x, y);
            });

        } catch (IvyException ex) {
            Logger.getLogger(Geste.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        try {
            bus.bindMsg("Palette:Info nom=(.*) x=(.*) y=(.*) longueur=(.*) hauteur=(.*) couleurFond=(.*) couleurContour=(.*)",
                    (IvyClient ic, String[] strings) -> {

                        String keyColor = strings[5];
                        String shapeName = strings[0];
                        if (objectType.equals("O")) {
                            if (shapesByColor.containsKey(keyColor)) {
                                shapesByColor.get(keyColor).add(shapeName);
                            } else {
                                ArrayList<String> shapes = new ArrayList<>();
                                shapes.add(shapeName);
                                shapesByColor.put(keyColor, shapes);

                            }
                        } else {
                            if (shapeName.startsWith(objectType)) {

                                if (shapesByColor.containsKey(keyColor)) {
                                    shapesByColor.get(keyColor).add(shapeName);
                                } else {
                                    ArrayList<String> shapes = new ArrayList<>();
                                    shapes.add(shapeName);
                                    shapesByColor.put(keyColor, shapes);

                                }
                            }
                        }
                        receivedResponse++;
                        if (receivedResponse == shapes.size()) {
                            switch (applicationState) {
                                case InfoCréer:
                                    timer.restart();
                                    handleCouleur(shapesByColor.entrySet().iterator().next().getKey());
                                    break;
                                case InfoSupp:
                                    applicationState = ApplicationState.CouleurSupp;
                                    timer.restart();
                                    break;
                                case InfoDéplacer:
                                    applicationState = ApplicationState.CouleurDéplacer;
                                    timer.restart();
                                    break;
                            }
                            receivedResponse = 0;
                            shapes.clear();
                        }
                    });

        } catch (IvyException ex) {
            Logger.getLogger(Geste.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        try {
            bus.bindMsg("Palette:FinTesterPoint", (IvyClient ic, String[] strings) -> {
                shapes.forEach((nom) -> {
                    try {
                        bus.sendMsg("Palette:DemanderInfo nom=" + nom);
                    } catch (IvyException ex) {
                        Logger.getLogger(Multimodal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });

            });
        } catch (IvyException ex) {
            Logger.getLogger(Multimodal.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            bus.bindMsg("Palette:ResultatTesterPoint x=(.*) y=(.*) nom=(.*)", (IvyClient ic, String[] strings) -> {
                String nom = strings[2];
                shapes.add(nom);

            });
        } catch (IvyException ex) {
            Logger.getLogger(Geste.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        /**
         * Voice commands events
         */
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        DecimalFormat format = new DecimalFormat();
        format.setDecimalFormatSymbols(symbols);
        try {
            bus.bindMsg("sra5 Parsed=(.*) Confidence=(.*) NP=(.*) Num_A=(.*)", (IvyClient ic, String[] strings) -> {

                String text = strings[0];
                float confidence = 0f;
                try {
                    confidence = format.parse(strings[1]).floatValue();
                } catch (ParseException ex) {
                    Logger.getLogger(Multimodal.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
                if (confidence >= 0.60f) {
                    String[] keyValues = text.split(":");
                    switch (keyValues[0]) {
                        case "Position":
                            handlePosition(keyValues[1]);
                            break;
                        case "Couleur":
                            handleCouleur(keyValues[1]);
                            break;
                        case "Objet":
                            handleObject(keyValues[1]);
                            break;
                    }
                }
            });

        } catch (IvyException ex) {
            Logger.getLogger(Geste.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
