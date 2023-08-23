package org.TransportModel.gui;

import javax.swing.*;
import java.awt.*;
///////////////////////////////////////////////////////////////////////////////////////////////////
/**  */
///////////////////////////////////////////////////////////////////////////////////////////////////
public final class UserInterface extends JFrame
{
    private static UserInterface instance;
    private final JPanel imageContainer;
    private final JPanel buttonsContainer;
    private UserInterface()
    {
        this.imageContainer = new JPanel();
        this.buttonsContainer = new JPanel();
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setupImageContainer();
        this.setupButtonsContainer();
        this.setSize(screenSize);
        this.setVisible(true);
    }
    public static UserInterface getInstance()
    {
        if(instance==null)
            instance = new UserInterface();
        return instance;
    }
    public void addButton(JButton button) {this.buttonsContainer.add(button);}
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**  */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private void setupImageContainer()
    {
        this.getContentPane().add(imageContainer, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**  */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private void setupButtonsContainer()
    {
        this.buttonsContainer.setBackground(Color.red);
        this.getContentPane().add(buttonsContainer, BorderLayout.SOUTH);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void display(JComponent component)
    {
        component.setPreferredSize(new Dimension(900, 900));
        this.imageContainer.add(component, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }
}
