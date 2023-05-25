package org.LUTI;

import javax.swing.*;
import java.awt.*;

public class UserInterface extends JFrame {

    private final JPanel panel;

    /** Constructeur **/
    public UserInterface() {
        this.setLayout(null);
        this.setSize(1000, 1000);

        panel = new JPanel(new FlowLayout());
        this.setContentPane(panel);
    }

    public void addJComponent(JComponent component) {
        this.setContentPane(component);
        component.setBounds(0,0,1000,1000);
        this.revalidate();
        this.repaint();
        this.setVisible(true);
    }
}
