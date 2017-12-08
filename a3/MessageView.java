import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class MessageView extends JPanel implements Observer {

    // status messages for game
    JLabel fuel = new JLabel("fuel");
    JLabel speed = new JLabel("speed");
    JLabel message = new JLabel("message");

    GameModel model;

    public MessageView(GameModel model) {
        // the model that this view is showing
        this.model = model;

        // want the background to be black
        setBackground(Color.BLACK);

        setLayout(new FlowLayout(FlowLayout.LEFT));

        // display ship's fuel and speed
        speed.setText("Speed: " + String.format("%.2f", model.ship.getSpeed()));
        speed.setForeground(Color.green);
        fuel.setText("Fuel: " + Integer.toString((int)model.ship.getFuel()));
        message.setText("(Paused)");

        add(fuel);
        add(speed);
        add(message);

        for (Component c: this.getComponents()) {
            c.setForeground(Color.WHITE);
            c.setPreferredSize(new Dimension(100, 20));
        }
    }


    @Override
    public void update(Observable o, Object arg) {
        // ship status
        if(model.status == "halt") {
            if(!model.ship.timer.isRunning()) {
                message.setText("(Paused)");
            } else {
                message.setText(" ");
            }
        } else if(model.status == "Landed") {
            message.setText("LANDED!");
        } else if(model.status == "Crashed") {
            message.setText("CRASH");
        }

        // fuel and speed
        fuel.setText("Fuel: " + Integer.toString((int)model.ship.getFuel()));
        speed.setText("Speed: " + String.format("%.2f", model.ship.getSpeed()));

        // speed label color
        if(model.ship.getSpeed() > model.ship.getSafeLandingSpeed()) {
            speed.setForeground(Color.white);
        } else {
            speed.setForeground(Color.green);
        }

        // fuel label color
        if(model.ship.getFuel() >= 10) {
            fuel.setForeground(Color.white);
        } else {
            fuel.setForeground(Color.red);
        }
    }
}