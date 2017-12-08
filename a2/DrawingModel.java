import java.util.*;
import javax.vecmath.*;
import java.lang.Math.*;
import java.awt.geom.*;
import java.lang.*;
import java.awt.Point;

//View Interface
interface IView {
  public void updateView();
}

public class DrawingModel {
  private ArrayList<IView> views = new ArrayList<IView>();
  private ArrayList<Shape> shapes = new ArrayList<Shape>();
  private Point M = new Point(); // mouse point
  private int indexOfSelectedShape = -1;
  private int numOfShape = 0;
  private int o = 1;

  public void addView(IView view) {
    views.add(view);
    // view.updateView();
  }

  public void incrementO() {
    ++o;
  }

  public int getO() {
    return o;
  }

  public void incrementNumOfShape() {
    ++numOfShape;
    notifyObservers();
  }

  public void decrementNumOfShape() {
    --numOfShape;
    indexOfSelectedShape = -1;
    notifyObservers();
  }

  public int getNumOfShape() {
    return numOfShape;
  }

  public void setMousePoint(int x, int y) {
    M.x = x;
    M.y = y;
  }

  public Point getMousePoint() {
    return M;
  }

  public void addShape(Shape shape) {
    shapes.add(shape);
  }

  public void addShape(Shape shape, int index) {
    shapes.add(index, shape);
  }

  public void addShape_highlight(Shape shape) {
    shapes.add(shape);
  }

  public ArrayList<Shape> getShapes() {
    return shapes;
  }

  public void setIndexOfSelectedShape() {
    Boolean find = false;
    Point2d M2 = new Point2d(M.x, M.y);
    ArrayList<Shape> result = new ArrayList<Shape>();
    for(Shape shape : shapes) {
      if(shape.hittest(M.x, M.y)) {
        result.add(shape);
        find = true;
      }
    }
    if(find == false) {
      indexOfSelectedShape = -1;
    } else {
      int maxOrder = -1;
      int r = -1;
      for(Shape i : result) {
        if(i.order > maxOrder) {
          maxOrder = i.order;
          r = shapes.indexOf(i);
        }
      }
      indexOfSelectedShape = r;
    }
    notifyObservers();
  }

  public int getIndexOfSelectedShape() {
    return indexOfSelectedShape;
  }

  public Shape getShape(int index) {
    return shapes.get(index);
  }

  public void removeShape(int index) {
    shapes.remove(index);
  }

  public void scale(int index, float s) {
    shapes.get(index).setScale(s);
    notifyObservers();
  }

  public void translate(int index, double tx, double ty) {
    shapes.get(index).setTranslate(tx, ty);
    notifyObservers();
  }

  public void setFinalT(int index) {
    shapes.get(index).setFinalTranslate();
  }

  public void rotate(int index, double theta) {
    shapes.get(index).setRotate(theta);
    notifyObservers();
  }

  private void notifyObservers() {
    for(IView view : this.views) {
      view.updateView();
    }
  }
}
