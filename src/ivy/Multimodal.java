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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

/**
 *
 * @author arrouisa
 */
public class Multimodal extends javax.swing.JFrame {

    private void handleMouseDown(int x, int y) {
        switch (applicationState) {
            case Init:
                switch (gestureState) {
                    case Init:
                        stroke = new Stroke();
                        gestureState = GestureState.Point;
                        stroke.addPoint(x, y);
                        break;
                    case Point:
                        // "impossible"
                        break;
                    case Draw:
                        //impossible
                        break;
                }
                break;
        }
    }

    private void handleMouseUp(int x, int y) {
        switch (applicationState) {
            case Init:
                switch (gestureState) {
                    case Init:
                        /**
                         * Impossible
                         */
                        break;
                    case Point:
                        gestureState = GestureState.Init;
                        stroke.getPoints().clear();
                        break;
                    case Draw:
                        gestureState = GestureState.Init;
                        stroke.addPoint(x, y);
                        stroke.normalize();
                        double distance = Double.MAX_VALUE;
                        String name = "";
                        for (Map.Entry<String, ArrayList<Point2D.Double>> entry : gestures.entrySet()) {
                            double d2 = compareTwoPatterns(entry.getValue(), stroke.getPoints());
                            if (d2 < distance) {
                                distance = d2;
                                name = entry.getKey();
                            }
                        }
                        handleGesture(name);
                        break;
                }
                break;
        }

    }

    private void handleMouseDragged(int x, int y) {
        switch (applicationState) {
            case Init:
                switch (gestureState) {
                    case Init:
                        /**
                         * Impossible
                         */
                        break;
                    case Point:
                    case Draw:
                        gestureState = GestureState.Draw;
                        stroke.addPoint(x, y);
                        break;
                }
                break;
        }
    }

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
                if (position.equals("à droite")) {
                    applicationState = ApplicationState.Créer;
                    ((CreateCommand) command).setPosition(new Point(200, 200));
                }
                if (position.equals("à gauche")) {
                    applicationState = ApplicationState.Créer;
                    ((CreateCommand) command).setPosition(new Point(0, 200));
                }
                if (position.equals("ici") || position.equals("la")) {
                    applicationState = ApplicationState.PositionCréer;
                }
                timer.restart();
                break;
            case ClickCréer:
                if (position.equals("ici") || position.equals("la")) {
                    applicationState = ApplicationState.Créer;
                    ((CreateCommand) command).setPosition(clickedPoint);
                    timer.restart();
                }
                break;
            case CouleurCréer:
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
                    try {
                        askInfo(clickedPoint.x, clickedPoint.y);
                    } catch (IvyException ex) {
                        Logger.getLogger(Multimodal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    timer.restart();
                }
                break;
            case CouleurCréer:
                /**
                 * Ne rien faire
                 */
                break;
            case InfoCréer:
                applicationState = ApplicationState.Créer;

                ((CreateCommand) command).setColor(couleur);
                timer.restart();
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
            case ClickCréer:
            case CouleurCréer:
            case InfoCréer:
                applicationState = ApplicationState.Créer;
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
        System.out.println("geste name"+name);
          switch (applicationState) {
            case Init:
                switch (name) {
                    case "supprimer":
                        /**
                         * Go to suppression state
                         */
                        applicationState = ApplicationState.Supprimer;
                        break;
                    case "deplacer":
                        /**
                         * Go to move state
                         */
                        applicationState = ApplicationState.Déplacer;
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
    private GestureState gestureState;

    private Command command;
    private Point clickedPoint;

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


    }

    private enum GestureState {
        Init,
        Point,
        Draw,
    };

    private Ivy bus;

    private Timer timer;
    private Stroke stroke;
    private HashMap<String, ArrayList<Point2D.Double>> gestures;

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
        gestureState = GestureState.Init;
        /**
         * Initialize properties
         */
        timer = new Timer(2000, (e) -> {
            handleTimeOut(e);
        });
        stroke = new Stroke();

        try (FileInputStream fis = new FileInputStream("geste"); ObjectInputStream ois = new ObjectInputStream(fis)) {

            gestures = (HashMap< String, ArrayList<Point2D.Double>>) ois.readObject();

        }
        /**
         * Declare Ivy bus, start it and bind messages to event handlers
         */
        bus = new Ivy("Ivy_Sender", null, null);

        try {
            bus.start("127.255.255.255:2010");
        } catch (IvyException ex) {
            Logger.getLogger(IvyMessages.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        /**
         * Palette events such as Mouse Drag and Click
         */
        try {
            bus.bindMsg("Palette:MousePressed x=(.*) y=(.*)", (IvyClient ic, String[] strings) -> {
                int x = Integer.parseInt(strings[0]);
                int y = Integer.parseInt(strings[1]);
                handleMouseDown(x, y);
            });

        } catch (IvyException ex) {
            Logger.getLogger(IvyMessages.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        try {
            bus.bindMsg("Palette:MouseReleased x=(.*) y=(.*)", (IvyClient ic, String[] strings) -> {
                int x = Integer.parseInt(strings[0]);
                int y = Integer.parseInt(strings[1]);
                handleMouseUp(x, y);
            });

        } catch (IvyException ex) {
            Logger.getLogger(IvyMessages.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        try {
            bus.bindMsg("Palette:MouseDragged x=(.*) y=(.*)", (IvyClient ic, String[] strings) -> {
                int x = Integer.parseInt(strings[0]);
                int y = Integer.parseInt(strings[1]);
                handleMouseDragged(x, y);
            });

        } catch (IvyException ex) {
            Logger.getLogger(IvyMessages.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        try {
            bus.bindMsg("Palette:MouseClicked x=(.*) y=(.*)", (IvyClient ic, String[] strings) -> {
                int x = Integer.parseInt(strings[0]);
                int y = Integer.parseInt(strings[1]);
                handleMouseClicked(x, y);
            });

        } catch (IvyException ex) {
            Logger.getLogger(IvyMessages.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        try {
            bus.bindMsg("Palette:Info nom=(.*) x=(.*) y=(.*) longueur=(.*) hauteur=(.*) couleurFond=(.*) couleurContour=(.*)",
                    (IvyClient ic, String[] strings) -> {
                        String couleur = strings[5];
                        handleCouleur(couleur);
                    });

        } catch (IvyException ex) {
            Logger.getLogger(IvyMessages.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        try {
            bus.bindMsg("Palette:ResultatTesterPoint x=(.*) y=(.*) nom=(.*)", (IvyClient ic, String[] strings) -> {
                String nom = strings[2];
                try {
                    bus.sendMsg("Palette:DemanderInfo nom=" + nom);
                } catch (IvyException ex) {
                    Logger.getLogger(Multimodal.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            });
        } catch (IvyException ex) {
            Logger.getLogger(IvyMessages.class
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
                            // a faire après 
                            break;
                    }
                }
            });

        } catch (IvyException ex) {
            Logger.getLogger(IvyMessages.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
