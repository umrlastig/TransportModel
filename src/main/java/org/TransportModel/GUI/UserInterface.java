package org.TransportModel.GUI;

import javax.swing.*;
import java.awt.*;
///////////////////////////////////////////////////////////////////////////////////////////////////
/**          Affiche un NetworkCanvas et permet de définir ses paramètres                        */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class UserInterface extends JFrame
{
    private final JPanel graphContainer;
    private final JPanel buttonsContainer;
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                        Constructeur                                          */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public UserInterface()
    {
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.graphContainer = new JPanel();
        this.buttonsContainer = new JPanel();
        this.setupGraphContainer();
        this.setupButtonsContainer();
        this.setSize(new Dimension(1000,800));
        this.setVisible(true);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                      Initialise le JPanel qui va contenir le Graph                          */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private void setupGraphContainer()
    {

        this.graphContainer.setBackground(Color.black);
        this.getContentPane().add(graphContainer, BorderLayout.CENTER);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**              Initialise le JPanel qui va contenir les boutons d'option                       */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private void setupButtonsContainer()
    {
        this.buttonsContainer.setBackground(Color.red);
        this.getContentPane().add(buttonsContainer, BorderLayout.SOUTH);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**              Initialise le JPanel qui va contenir les boutons d'option                       */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void setComponent(JComponent component)
    {
        component.setPreferredSize(new Dimension(900, 900));
        this.graphContainer.add(component, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }
}
