import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.geom.*;
import java.util.ArrayList;

import javax.vecmath.*;

public class CanvasView extends JPanel implements IView {
  private DrawingModel model;
  private Shape shape;

  CanvasView(DrawingModel model) {
    setPreferredSize(new Dimension(800, 490));
    setBorder(BorderFactory.createLineBorder(Color.gray));

    this.model = model;

    this.addMouseListener(new MouseAdapter(){
        public void mousePressed(MouseEvent e) {
            shape = new Shape();
            model.setMousePoint(e.getX(), e.getY());
            // try setting scale to something other than 1
            shape.scale = 1.0f;
            int index = model.getIndexOfSelectedShape();
            if(index != -1) model.setFinalT(index);

            repaint();
        }
    });

    this.addMouseMotionListener(new MouseAdapter(){
        public void mouseDragged(MouseEvent e) {
          int index = model.getIndexOfSelectedShape();
          if(index == -1) {
            shape.addPoint(e.getX(), e.getY());
          } else {
            Point P = model.getMousePoint();

            model.translate(index, e.getX() - P.x, e.getY() - P.y);
          }
          repaint();
        }
    });

    this.addMouseListener(new MouseAdapter(){
        public void mouseReleased(MouseEvent e) {
          model.setMousePoint(e.getX(), e.getY());
          if(shape.points != null) {
            shape.order = model.getO();
            model.addShape(shape);
            model.incrementNumOfShape();
            model.incrementO();
          }
        }
    });

    this.addMouseMotionListener(new MouseAdapter(){
      public void mouseMoved(MouseEvent e) {
            model.setMousePoint(e.getX(), e.getY());
      }
    });

    this.addMouseListener(new MouseAdapter(){
      public void mouseClicked(MouseEvent e) {
          model.setIndexOfSelectedShape();
          int n = model.getIndexOfSelectedShape();
          if(n != -1) {
            for(Shape myShape : model.getShapes()) {
              if(myShape != null) {
                myShape.yellow = false;
              }
            }
            Shape theShape = model.getShape(n);
            theShape.yellow = true;
            model.addShape(theShape, n);
            model.removeShape(n+1);
            repaint();
          } else {
            for(Shape myShape : model.getShapes()) {
              if(myShape != null) {
                myShape.yellow = false;
              }
            }
            repaint();
          }
      }
    });
  }

  public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D) g; // cast to get 2D drawing methods
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  // antialiasing look nicer
                          RenderingHints.VALUE_ANTIALIAS_ON);
      ArrayList<Shape> shapes = model.getShapes();
      for(Shape ishape : shapes) {
        if(ishape != null) ishape.draw(g2);
      }
      if(shape != null) shape.draw(g2);
  }

  public void updateView() {
    repaint();
  }
}
