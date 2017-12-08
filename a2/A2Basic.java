import javax.swing.*;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.*;

public class A2Basic {

  public static void main(String[] args) {
    JFrame frame = new JFrame("A2Basic");

    DrawingModel model = new DrawingModel();

    ToolbarView view1 = new ToolbarView(model);
    model.addView(view1);

    CanvasView view2 = new CanvasView(model);
    model.addView(view2);
    //
    StatusbarView view3 = new StatusbarView(model);
    model.addView(view3);

    JPanel p = new JPanel(new FlowLayout());

    frame.getContentPane().add(p);

    p.add(view1);
    p.add(view2);
    p.add(view3);

    frame.setPreferredSize(new Dimension(800, 600));
    frame.pack();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }
}
