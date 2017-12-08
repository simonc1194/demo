import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.geom.*;
import javax.vecmath.*;

public class ToolbarView extends JPanel implements IView {
  private DrawingModel model;

  private JButton button;
  private JLabel scaleLabel;
  private JSlider scaleSlider;
  private JLabel scaleLabelInt;
  private JLabel rotateLabel;
  private JSlider rotateSlider;
  private JLabel rotateLabelInt;

  ToolbarView(DrawingModel model) {
    button = new JButton("Delete");
    // button.setHorizontalAlignment( SwingConstants.LEFT );
    setLayout(new FlowLayout(FlowLayout.LEADING));
    setPreferredSize(new Dimension(800, 35));
    this.add(Box.createHorizontalStrut(10));
    this.add(button);

    this.model = model;

    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int index = model.getIndexOfSelectedShape();
        if(index != -1) {
          model.removeShape(index);
          model.decrementNumOfShape();
        }
      }
    });

    this.add(Box.createHorizontalStrut(20));
    scaleLabel = new JLabel("Scale");
    this.add(scaleLabel);

    scaleSlider = new JSlider(50, 200, 100);
    this.add(scaleSlider);
    scaleSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        JSlider s = (JSlider)e.getSource();
        scaleLabelInt.setText(String.format("%.1f", (s.getValue() + 0.0)/100));
        int index = model.getIndexOfSelectedShape();
        if(index != -1) {
          model.scale(index, (float)(s.getValue() + 0.0)/100);
        }
      }
    });

    scaleLabelInt = new JLabel("1.0");
    this.add(scaleLabelInt);

    this.add(Box.createHorizontalStrut(20));
    rotateLabel = new JLabel("Rotate");
    this.add(rotateLabel);

    rotateSlider = new JSlider(-180, 180, 0);
    this.add(rotateSlider);
    rotateSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        JSlider s = (JSlider)e.getSource();
        rotateLabelInt.setText(Integer.toString(s.getValue()));
        int index = model.getIndexOfSelectedShape();
        if(index != -1) {
          model.rotate(index, s.getValue());
        }
      }
    });

    rotateLabelInt = new JLabel("0");
    this.add(rotateLabelInt);

    button.setEnabled(false);
    scaleLabel.setEnabled(false);
    scaleSlider.setEnabled(false);
    scaleLabelInt.setEnabled(false);
    rotateLabel.setEnabled(false);
    rotateSlider.setEnabled(false);
    rotateLabelInt.setEnabled(false);
  }

  public void updateView() {
    int n = model.getIndexOfSelectedShape();
    if(n == -1) {
      scaleSlider.setValue(100);
      scaleLabelInt.setText("1.0");
      rotateSlider.setValue(0);
      rotateLabelInt.setText("0");
      button.setEnabled(false);
      scaleLabel.setEnabled(false);
      scaleSlider.setEnabled(false);
      scaleLabelInt.setEnabled(false);
      rotateLabel.setEnabled(false);
      rotateSlider.setEnabled(false);
      rotateLabelInt.setEnabled(false);

    } else {
      float scale = model.getShape(n).scale;
      double rotate = model.getShape(n).rotate;
      scaleSlider.setValue((int)(scale * 100));
      scaleLabelInt.setText(String.format("%.1f", scale));
      rotateSlider.setValue((int)rotate);
      rotateLabelInt.setText((int)rotate + "");
      button.setEnabled(true);
      scaleLabel.setEnabled(true);
      scaleSlider.setEnabled(true);
      scaleLabelInt.setEnabled(true);
      rotateLabel.setEnabled(true);
      rotateSlider.setEnabled(true);
      rotateLabelInt.setEnabled(true);
    }
  }
}
