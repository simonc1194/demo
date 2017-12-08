import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class StatusbarView extends JPanel implements IView {
  private DrawingModel model;
  private JLabel strokesNum;

  StatusbarView(DrawingModel model) {
    setLayout(new FlowLayout(FlowLayout.LEADING));
    setPreferredSize(new Dimension(800, 75));
    this.model = model;
    int num = model.getNumOfShape();
    strokesNum = new JLabel(num + " Strokes");
    this.add(strokesNum);
  }

  public void updateView() {
    int num = model.getNumOfShape();
    int index = model.getIndexOfSelectedShape();
    if(index == -1) {
      if(num == 1) {
        strokesNum.setText(num + " Stroke");
      } else {
        strokesNum.setText(num + " Strokes");
      }
    } else {
      Shape shape = model.getShape(index);
      String s = String.format("%.1f", shape.scale);
      String out = num +
      " Stroke" +
      ", Selection (" +
      shape.points.size() +
      " points, scale: " +
      s +
      ", rotation " +
      (int)shape.rotate +
      ")";
      strokesNum.setText(out);
    }
  }
}
