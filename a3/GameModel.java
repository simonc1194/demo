import java.awt.*;
import java.awt.geom.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import javax.swing.undo.*;
import javax.vecmath.*;

public class GameModel extends Observable {
    public GameModel(int fps, int width, int height, int peaks) {
        undoManager = new UndoManager();

        landPad = new Rectangle(330, 100, 40, 10);

        ship = new Ship(60, width/2, 50);
        status = "halt";

        worldBounds = new Rectangle2D.Double(0, 0, width, height);

        // anonymous class to monitor ship updates
        ship.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) { setChangedAndNotify(); }
        });
    }

    // Undo manager
    private UndoManager undoManager;

    // World
    // - - - - - - - - - - -
    public final Rectangle2D getWorldBounds() { return worldBounds; }

    Rectangle2D.Double worldBounds;


    // Ship
    // - - - - - - - - - - -

    public Ship ship;
    public String status;

    public void gameStart() {
        Point2d newPoint = new Point2d(350, 50);
        ship.reset(newPoint);
        ship.setFuel(50);
        status = "halt";
        setChangedAndNotify();
    }

    public void checkShipStatus() {
        if(ship.timer.isRunning()) {
            // check if the ship successfully lands on the landing pad
            if(landPad.intersects(ship.getShape())) {
                if(ship.getSafeLandingSpeed() < ship.getSpeed()) {
                    status = "Crashed";
                } else {
                    status = "Landed";
                }
                ship.setPaused(true);
            }

            // check if the ship hits the mountain or the world boundaries
            Polygon poly = new Polygon(xPoly, yPoly, 22);
            if(!worldBounds.contains(ship.getShape()) || poly.intersects(ship.getShape()) ) {
                ship.setPaused(true);
                status = "Crashed";
            }
        }
        setChangedAndNotify();
    }

    // Observerable
    // - - - - - - - - - - -

    // helper function to do both
    void setChangedAndNotify() {
        setChanged();
        notifyObservers();
    }

    // Landing pad
    Rectangle landPad;

    public boolean focusPad = false;

    public Rectangle getLandPad() { return landPad; }

    public void moveLandPad(int x, int y) {
        landPad.setLocation(x, y);
        setChangedAndNotify();
    }

    public void setLandPad(int x, int y) {
        Rectangle r = new Rectangle(x, y, 40, 10);
        // create undoable edit
        UndoableEdit undoableEdit = new AbstractUndoableEdit() {
            // capture variables for closure
            final Rectangle oldR = landPad;
            final Rectangle newR = r;

            // Method that is called when we must redo the undone action
            public void redo() throws CannotRedoException {
                super.redo();
                landPad = newR;
                setChangedAndNotify();

            }

            public void undo() throws CannotUndoException {
                super.undo();
                landPad = oldR;
                setChangedAndNotify();
            }
        };
        // Add this undoable edit to the undo manager
        undoManager.addEdit(undoableEdit);

        landPad = r;
        setChangedAndNotify();
    }

    // Polygon
    int [] xPoly = new int[22];
    int [] yPoly = new int[22];

    public boolean focusPoly = false;
    public int focusPolyIndex = -1;

    public void generatePoly() {
        Random rand = new Random();
        xPoly[19] = 700;
        yPoly[19] = rand.nextInt(100) + 100;
        xPoly[20] = 700;
        yPoly[20] = 200;
        xPoly[21] = 0;
        yPoly[21] = 200;
        for(int i = 0; i < 19; i++) {
            xPoly[i] = i * (703 / 19);
            yPoly[i] = rand.nextInt(100) + 100;
        }
    }

    public int [] getxPoly() { return xPoly; }

    public int [] getyPoly() { return yPoly; }

    public void moveY(int index, int value) {
        yPoly[index] = value;
        setChangedAndNotify();
    }

    public void modifyY(int index, int value) {
        int [] yP = new int[22];
        System.arraycopy(yPoly, 0, yP, 0, 22);
        yP[index] = value;

        // create undoable edit
        UndoableEdit undoableEdit = new AbstractUndoableEdit() {
            // capture variables for closure
            int [] oldY = yPoly;
            int [] newY = yP;

            // Method that is called when we must redo the undone action
            public void redo() throws CannotRedoException {
                super.redo();
                yPoly = newY;
                setChangedAndNotify();
            }

            public void undo() throws CannotUndoException {
                super.undo();
                yPoly = oldY;
                setChangedAndNotify();
            }

        };

        // Add this undoable edit to the undo manager
        undoManager.addEdit(undoableEdit);

        yPoly = yP;
        setChangedAndNotify();
    }

    // undo and redo methods
    // - - - - - - - - - - - - - -
    public void undo() {
        if (canUndo())
            undoManager.undo();
    }
    public void redo() {
        if (canRedo())
            undoManager.redo();
    }
    public boolean canUndo() {
        return undoManager.canUndo();
    }
    public boolean canRedo() {
        return undoManager.canRedo();
    }
}



