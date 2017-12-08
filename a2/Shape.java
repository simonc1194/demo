// This is the file from prof's git repository


/*
*  Shape: See ShapeDemo for an example how to use this class.
*
*/
import java.util.ArrayList;
import java.awt.*;
import java.awt.geom.*;
import javax.vecmath.*;
import java.lang.Math.*;

// simple shape model class
class Shape {

    int order = -1;

    Boolean yellow = false;
    // shape points
    ArrayList<Point2d> points;

    Point2d centre;

    public void clearPoints() {
        points = new ArrayList<Point2d>();
        pointsChanged = true;
    }

    // add a point to end of shape
    public void addPoint(Point2d p) {
        if (points == null) clearPoints();
        points.add(p);
        pointsChanged = true;
    }

    // add a point to end of shape
    public void addPoint(double x, double y) {
        addPoint(new Point2d(x, y));
    }

    public int npoints() {
        return points.size();
    }

    // shape is polyline or polygon
    Boolean isClosed = false;

    public Boolean getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(Boolean isClosed) {
        this.isClosed = isClosed;
    }

    // if polygon is filled or not
    Boolean isFilled = false;

    public Boolean getIsFilled() {
        return isFilled;
    }

    public void setIsFilled(Boolean isFilled) {
        this.isFilled = isFilled;
    }

    // drawing attributes
    Color colour = Color.BLACK;
    float strokeThickness = 2.0f;

    public Color getColour() {
		return colour;
	}

	public void setColour(Color colour) {
		this.colour = colour;
	}

  public void setScale(float scale) {
    this.scale = scale;
  }

  public void setRotate(double rotate) {
    this.rotate = rotate;
  }

  public void setTranslate(double tx, double ty) {
    this.tx_change = tx;
    this.ty_change = ty;
  }

  public void setFinalTranslate() {
    tx += tx_change;
    ty += ty_change;
    tx_change = 0;
    ty_change = 0;
  }

    public float getStrokeThickness() {
		return strokeThickness;
	}

	public void setStrokeThickness(float strokeThickness) {
		this.strokeThickness = strokeThickness;
	}

    // shape's transform

    // quick hack, get and set would be better
    float scale = 1.0f;

    double rotate = 0;

    double tx = 0;
    double ty = 0;

    double tx_change = 0;
    double ty_change = 0;

    // some optimization to cache points for drawing
    Boolean pointsChanged = false; // dirty bit
    int[] xpoints, ypoints;
    int npoints = 0;

    void cachePointsArray() {
        xpoints = new int[points.size()];
        ypoints = new int[points.size()];
        for (int i=0; i < points.size(); i++) {
          xpoints[i] = (int)points.get(i).x;
          ypoints[i] = (int)points.get(i).y;
        }
        npoints = points.size();
        setCentrePoint(xpoints, ypoints, npoints);
        pointsChanged = false;
    }


    // let the shape draw itself
    // (note this isn't good separation of shape View from shape Model)
    public void draw(Graphics2D g2) {

        // don't draw if points are empty (not shape)
        if (points == null) return;

        // see if we need to update the cache
        if (pointsChanged) cachePointsArray();

        // save the current g2 transform matrix
        AffineTransform M = g2.getTransform();

        // multiply in this shape's transform
        double rad = Math.toRadians(rotate);
        g2.translate(centre.getX(), centre.getY());
        g2.translate((int)(tx + tx_change), (int)(ty + ty_change));
        g2.scale(scale, scale);
        g2.rotate(rad);
        g2.translate(-centre.getX(), -centre.getY());

        // call drawing functions
        g2.setColor(colour);
        if (isFilled) {
            g2.fillPolygon(xpoints, ypoints, npoints);
        } else {
            // can adjust stroke size using scale
        	// g2.setStroke(new BasicStroke(strokeThickness / scale));
        	if (isClosed) {
            g2.drawPolygon(xpoints, ypoints, npoints);
          } else {
              if(yellow == true) {
                g2.setStroke(new BasicStroke(6 / scale));
                g2.setColor(Color.YELLOW);
                g2.drawPolyline(xpoints, ypoints, npoints);
              }
              g2.setStroke(new BasicStroke(2 / scale));
              g2.setColor(Color.BLACK);
              g2.drawPolyline(xpoints, ypoints, npoints);
          }
        }

        // reset the transform to what it was before we drew the shape
        g2.setTransform(M);
    }

    public void setCentrePoint(int xpoints[], int ypoints[], int npoints) {
      int boundMinX = Integer.MAX_VALUE;
      int boundMinY = Integer.MAX_VALUE;
      int boundMaxX = Integer.MIN_VALUE;
      int boundMaxY = Integer.MIN_VALUE;

      for(int i = 0; i < npoints; i++) {
        int x = xpoints[i];
        boundMinX = Math.min(boundMinX, x);
        boundMaxX = Math.max(boundMaxX, x);

        int y = ypoints[i];
        boundMinY = Math.min(boundMinY, y);
        boundMaxY = Math.max(boundMaxY, y);
      }

      int x2d = (boundMaxX + boundMinX) / 2;
      int y2d = (boundMaxY + boundMinY) / 2;

      centre = new Point2d(x2d, y2d);
    }

    Point2d closestPoint(Point2d M, Point2d P0, Point2d P1) {
    	Vector2d v = new Vector2d();
    	v.sub(P1,P0); // v = P2 - P1

    	// early out if line is less than 1 pixel long
    	if (v.lengthSquared() < 0.5)
    		return P0;

    	Vector2d u = new Vector2d();
    	u.sub(M,P0); // u = M - P1

    	// scalar of vector projection ...
    	double s = u.dot(v) / v.dot(v);

    	// find point for constrained line segment
    	if (s < 0)
    		return P0;
    	else if (s > 1)
    		return P1;
    	else {
    		Point2d I = P0;
        	Vector2d w = new Vector2d();
        	w.scale(s, v); // w = s * v
    		I.add(w); // I = P1 + w
    		return I;
    	}
    }

    Point2d transformedPoint(int x, int y) {
      int x1 = x - (int)centre.getX();
      int y1 = y - (int)centre.getY();
      int x2 = x1;
      int y2 = y1;
      double rad = Math.toRadians(rotate);
      x1 = (int)(x2 * Math.cos(rad) - y2 * Math.sin(rad));
      y1 = (int)(x2 * Math.sin(rad) + y2 * Math.cos(rad));
      x1 = (int) (x1 * scale);
      y1 = (int) (y1 * scale);
      x1 += tx;
      y1 += ty;
      x1 += centre.getX();
      y1 += centre.getY();
      return new Point2d(x1, y1);
    }

    // let shape handle its own hit testing
    // (x,y) is the point to test against
    // (x,y) needs to be in same coordinate frame as shape, you could add
    // a panel-to-shape transform as an extra parameter to this function
    // (note this isn't good separation of shape Controller from shape Model)
    public boolean hittest(double x, double y)
    {
    	if (points != null) {
        Point2d P = new Point2d(x, y);
          for(int i = 0; i < npoints - 1; i++) {
            Point2d startP = transformedPoint(xpoints[i], ypoints[i]);
            Point2d endP = transformedPoint(xpoints[i+1], ypoints[i+1]);
            Point2d closeP = closestPoint(P, startP, endP);
            double d = P.distance(closeP);
            if(d <= 5) return true;
          }
    	}

    	return false;
    }
}
