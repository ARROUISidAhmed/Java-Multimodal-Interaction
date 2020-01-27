/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivy;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyException;
import fr.dgac.ivy.IvyMessageListener;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author arrouisa
 */
public class IvyMessages extends javax.swing.JFrame {

    /**
     * Creates new form IvyMessages
     */
    public IvyMessages() {
        initComponents();
        initStates();
        initOthers();
    }

    enum State {
        Init,
        Point,
        Draw,

    };

    enum Function {
        Learning,
        Finding,
    };

    private Stroke stroke;
    private Ivy bus;
    private State s;
    private Function f;
    private HashMap<String, ArrayList<Point2D.Double>> historique;

    private void initStates() {
        s = State.Init;
        f = Function.Learning;
        learning.setSelected(true);
        historique = new HashMap<>();
    }

    private double distanceTwoPoints(Point2D.Double p1, Point2D.Double p2) {
        return Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
    }

    private double compareTwoPatterns(ArrayList<Point2D.Double> liste1, ArrayList<Point2D.Double> liste2) {
        double distance = 0;
        for (int i = 0; i < liste1.size(); i++) {
            distance += distanceTwoPoints(liste1.get(i), liste2.get(i));

        }

        return distance / liste1.size();
    }

    private void initOthers() {
        bus = new Ivy("Ivy_Sender", null, null);

        try {
            bus.start("127.255.255.255:2010");
        } catch (IvyException ex) {
            Logger.getLogger(IvyMessages.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            bus.bindMsg("Palette:MousePressed x=(.*) y=(.*)", (IvyClient ic, String[] strings) -> {
                int x1 = Integer.parseInt(strings[0]);
                int y1 = Integer.parseInt(strings[1]);
                try {
                    switch (s) {
                        case Init:
                            stroke = new Stroke();
                            s = State.Point;
                            bus.sendMsg("Palette:CreerEllipse x=" + x1 + " y=" + y1 + " longueur=4 hauteur=4 couleurFond=red couleurContour=red");
                            stroke.addPoint(x1, y1);
                            break;
                        case Point:
                            // "impossible"
                            break;
                        case Draw:
                            //impossible
                            break;
                    }

                } catch (IvyException ex) {
                    Logger.getLogger(IvyMessages.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        } catch (IvyException ex) {
            Logger.getLogger(IvyMessages.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            bus.bindMsg("Palette:MouseDragged x=(.*) y=(.*)", (IvyClient ic, String[] strings) -> {
                int x1 = Integer.parseInt(strings[0]);
                int y1 = Integer.parseInt(strings[1]);
                try {
                    switch (s) {
                        case Init:
                            //impossible
                            break;
                        case Point:
                            s = State.Draw;
                            bus.sendMsg("Palette:CreerEllipse x=" + x1 + " y=" + y1 + " longueur=4 hauteur=4 couleurFond=grey couleurContour=grey");
                            stroke.addPoint(x1, y1);
                            break;
                        case Draw:
                            s = State.Draw;
                            bus.sendMsg("Palette:CreerEllipse x=" + x1 + " y=" + y1 + " longueur=4 hauteur=4 couleurFond=grey couleurContour=grey");
                            stroke.addPoint(x1, y1);
                            break;
                    }
                } catch (IvyException ex) {
                    Logger.getLogger(IvyMessages.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        } catch (IvyException ex) {
            Logger.getLogger(IvyMessages.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            bus.bindMsg("Palette:MouseReleased x=(.*) y=(.*)", (IvyClient ic, String[] strings) -> {
                int x1 = Integer.parseInt(strings[0]);
                int y1 = Integer.parseInt(strings[1]);
                try {
                    switch (s) {
                        case Init:
                            //impossible
                            break;
                        case Point:
                            s = State.Init;
                            bus.sendMsg("Palette:CreerEllipse x=" + x1 + " y=" + y1 + " longueur=4 hauteur=4 couleurFond=green couleurContour=green");
                            stroke.addPoint(x1, y1);
                            break;
                        case Draw:
                            s = State.Init;
                            bus.sendMsg("Palette:CreerEllipse x=" + x1 + " y=" + y1 + " longueur=4 hauteur=4 couleurFond=green couleurContour=green");
                            stroke.addPoint(x1, y1);
                            stroke.normalize();
                            stroke.getPoints().forEach((point) -> {
                                try {
                                    bus.sendMsg("Palette:CreerEllipse x=" + (int) point.x + " y=" + (int) point.y + " longueur=4 hauteur=4 couleurFond=blue couleurContour=blue");

                                } catch (IvyException ex) {
                                    Logger.getLogger(IvyMessages.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            });
                            switch (f) {
                                case Learning:
                                    drawPoints.setListePoint(stroke.getPoints());
                                    break;
                                case Finding:
                                    double distance = Double.MAX_VALUE;
                                    String name = "";
                                    ArrayList<Point2D.Double> listeTrouvee = new ArrayList<>();
                                    for (Map.Entry<String, ArrayList<Point2D.Double>> entry : historique.entrySet()) {
                                        double d2 = compareTwoPatterns(entry.getValue(), stroke.getPoints());
                                        if (d2 < distance) {
                                            distance=d2;
                                            name = entry.getKey();
                                            listeTrouvee=entry.getValue();
                                        }

                                    }
                                    drawPoints.setListePoint(listeTrouvee);
                                    
                                    break;
                            }

                            stroke.getPoints().clear();
                            break;
                    }
                } catch (IvyException ex) {
                    Logger.getLogger(IvyMessages.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        } catch (IvyException ex) {
            Logger.getLogger(IvyMessages.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        choiceArea = new javax.swing.JPanel();
        learning = new javax.swing.JRadioButton();
        finding = new javax.swing.JRadioButton();
        drawArea = new javax.swing.JPanel();
        drawPoints = new ivy.DrawArea();
        inputButton1 = new javax.swing.JButton();
        inputBox1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        learning.setText("Apprentissage");
        learning.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                learningActionPerformed(evt);
            }
        });
        choiceArea.add(learning);

        finding.setText("Reconnaissance");
        finding.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findingActionPerformed(evt);
            }
        });
        choiceArea.add(finding);

        drawArea.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        drawArea.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout drawPointsLayout = new javax.swing.GroupLayout(drawPoints);
        drawPoints.setLayout(drawPointsLayout);
        drawPointsLayout.setHorizontalGroup(
            drawPointsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 886, Short.MAX_VALUE)
        );
        drawPointsLayout.setVerticalGroup(
            drawPointsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 258, Short.MAX_VALUE)
        );

        drawArea.add(drawPoints, java.awt.BorderLayout.CENTER);

        inputButton1.setText("Valider");
        inputButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputButton1ActionPerformed(evt);
            }
        });

        jLabel2.setText("Saisir le nom du geste associé");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(drawArea, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(choiceArea, javax.swing.GroupLayout.PREFERRED_SIZE, 806, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(inputBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 487, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inputButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(choiceArea, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(drawArea, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(inputBox1)
                            .addComponent(inputButton1))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void learningActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_learningActionPerformed
        switch (f) {
            case Learning:
                learning.setSelected(true);
                break;
            case Finding:
                f = Function.Learning;
                finding.setSelected(false);
                learning.setSelected(true);
                inputBox1.setEnabled(true);
                inputButton1.setEnabled(true);
                break;
        }
    }//GEN-LAST:event_learningActionPerformed

    private void findingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findingActionPerformed
        switch (f) {
            case Learning:
                f = Function.Finding;
                learning.setSelected(false);
                finding.setSelected(true);
                inputBox1.setEnabled(false);
                inputButton1.setEnabled(false);
                drawPoints.setListePoint(new ArrayList<>());
                break;
            case Finding:
                finding.setSelected(true);
                break;
        }    }//GEN-LAST:event_findingActionPerformed
    public static boolean isNullOrEmpty(String str) {
        if (str != null && !str.trim().isEmpty()) {
            return false;
        }
        return true;
    }
    private void inputButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputButton1ActionPerformed
        switch (f) {
            case Learning:
                String text = inputBox1.getText();
                text = text.replace(" ", "");
                if (!isNullOrEmpty(text)) {
                    historique.put(text, drawPoints.getListePoint());
                    JOptionPane.showMessageDialog(this, "\"Votre schéma a été sauvegardé avec succès\"");
                    inputBox1.setText("");
                    drawPoints.setListePoint(new ArrayList<>());               }

                break;
            case Finding:
                //impossible
                break;
        }
    }//GEN-LAST:event_inputButton1ActionPerformed

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(IvyMessages.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(IvyMessages.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(IvyMessages.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(IvyMessages.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new IvyMessages().setVisible(true);
            }

        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel choiceArea;
    private javax.swing.JPanel drawArea;
    private ivy.DrawArea drawPoints;
    private javax.swing.JRadioButton finding;
    private javax.swing.JTextField inputBox1;
    private javax.swing.JButton inputButton1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JRadioButton learning;
    // End of variables declaration//GEN-END:variables
}
