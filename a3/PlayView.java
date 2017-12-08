import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

// the actual game view
public class PlayView extends JPanel implements Observer {
    // the model that this view is showing
    private GameModel model;

    public PlayView(GameModel model) {
        this.model = model;

        // needs to be focusable for keylistener
        setFocusable(true);

        // want the background to be black
        setBackground(Color.BLACK);

        // add listener responds to the key press in order to control the ship
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                if(model.status == "halt") {
                    if(e.getKeyChar() == ' ') {
                        if(model.ship.timer.isRunning()) {
                            model.ship.setPaused(true);
                        } else {
                            model.ship.setPaused(false);
                        }
                    } else if(e.getKeyChar() == 'W' || e.getKeyChar() == 'w') {
                        model.ship.thrustUp();
                    } else if(e.getKeyChar() == 'A' || e.getKeyChar() == 'a') {
                        model.ship.thrustLeft();
                    } else if(e.getKeyChar() == 'S' || e.getKeyChar() == 's') {
                        model.ship.thrustDown();
                    } else if(e.getKeyChar() == 'D' || e.getKeyChar() == 'd') {
                        model.ship.thrustRight();
                    }
                } else if(model.status == "Crashed" || model.status == "Landed") {
                    if(e.getKeyChar() == ' ') {
                        model.gameStart();
                        model.ship.setPaused(false);
                    }
                }
            }
        });
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        model.checkShipStatus();

        // cast graphics to graphics2D for more methods
        Graphics2D g2 = (Graphics2D) g;

        // scale the light grey editview world by 3
        AffineTransform M = g2.getTransform();
        g2.translate(this.getWidth()/2, this.getHeight()/2);
        g2.scale(3, 3);
        g2.translate(-model.ship.getPosition().getX(), -model.ship.getPosition().getY());

        // create a ligth grey world
        g2.setColor(Color.lightGray);
        g2.fillRect(0, 0, 700, 200);

        // create mountain
        g2.setColor(Color.darkGray);
        int [] xp = model.getxPoly();
        int [] yp = model.getyPoly();
        Polygon poly = new Polygon(xp, yp, 22);
        g2.fillPolygon(poly);

        // paint the red landing pad
        g2.setColor(Color.RED);
        Rectangle r = model.getLandPad();
        g2.fillRect((int)r.getLocation().getX(), (int)r.getLocation().getY(), 40, 10);

        // create blue ship
        g2.setColor(Color.blue);
        g2.fill(model.ship.getShape());
        g2.setTransform(M);
}

    @Override
    public void update(Observable o, Object arg) {
        repaint();
    }
}
