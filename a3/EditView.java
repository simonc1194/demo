import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

// the editable view of the terrain and landing pad
public class EditView extends JPanel implements Observer {
    // the model that this view is showing
    private GameModel model;

    boolean startDrag;

    public EditView(GameModel model) {
        this.model = model;
        startDrag = true;

        // want the background to be black
        setBackground(Color.BLACK);

        // prevent editview from stealing focus
        setFocusable(false);

        // add a listener that is responsible for double click
        this.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    Rectangle2D worldBounds = model.getWorldBounds();
                    if(worldBounds.contains(e.getX() - 20, e.getY() - 5, 40, 10)) {
                        model.setLandPad(e.getX(), e.getY());
                    }
                }
            }
        });

        // listener that takes care of in which part mouse pressed
        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                Rectangle r = model.getLandPad();
                // hit-test for landing pad
                if(r.contains(e.getX(), e.getY())) {
                    model.focusPad = true;
                }

                // if cursor is not in landing pad, do hit test for the 20 circles
                if(!model.focusPad) {
                    int [] xp = model.getxPoly();
                    int [] yp = model.getyPoly();
                    for(int i = 0; i < 20; i++) {
                        Point2D cursor = new Point2D.Double(e.getX(), e.getY());
                        Point2D centre = new Point2D.Double((double)xp[i], (double)yp[i]);
                        double dist = cursor.distance(centre);
                        if(dist <= 15) {
                            model.focusPoly = true;
                            model.focusPolyIndex = i;
                            break;
                        }
                    }
                }
            }
        });

        // listener that allows dragging pad and peak
        this.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                Rectangle2D worldBounds = model.getWorldBounds();
                // drag landing pad
                if(model.focusPad) {
                    if (worldBounds.contains(e.getX() - 20, e.getY() - 5, 40, 10)) {
                        if(!startDrag) {
                            model.moveLandPad(e.getX() - 20, e.getY() - 5);
                        } else {
                            startDrag = false;
                            model.setLandPad(e.getX() - 20, e.getY() - 5);
                        }
                    }
                } else {
                    // drag the specific peak
                    if(model.focusPoly) {
                        if(worldBounds.contains(e.getX(), e.getY())) {
                            if(!startDrag) {
                                model.moveY(model.focusPolyIndex, e.getY());
                            } else {
                                startDrag = false;
                                model.modifyY(model.focusPolyIndex, e.getY());
                            }
                        }
                    }
                }
            }
        });

        // after mouse is released, reset all the varaibles related to focus
        this.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if(model.focusPad) {
                    model.focusPad = false;
                    startDrag = true;
                } else {
                    if(model.focusPoly) {
                        model.focusPoly = false;
                        model.focusPolyIndex = -1;
                        startDrag = true;
                    }
                }
            }
        });

        model.generatePoly();
    }

    // paint edit view
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // create a ligth grey world
        g.setColor(Color.lightGray);
        g.fillRect(0, 0, 700, 200);

        g.setColor(Color.darkGray);
        int [] xp = model.getxPoly();
        int [] yp = model.getyPoly();
        Polygon poly = new Polygon(xp, yp, 22);
        g.fillPolygon(poly);
        g.setColor(Color.gray);
        for(int i = 0; i < 20; i++) {
            g.drawArc(xp[i] - 15, yp[i] - 15, 30, 30, 360, 360);
        }

        // paint the red landing pad
        g.setColor(Color.RED);
        Rectangle r = model.getLandPad();
        g.fillRect((int)r.getLocation().getX(), (int)r.getLocation().getY(), 40, 10);
    }

    @Override
    public void update(Observable o, Object arg) { repaint(); }

}
