package org.LUTI;

import javax.swing.*;
import java.awt.*;
///////////////////////////////////////////////////////////////////////////////////////////////////
/**          Affiche un NetworkCanvas et permet de définir ses paramètres                        */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class UserInterface extends JFrame
{
    private final NetworkCanvas networkCanvas;
    private final JPanel graphContainer;
    private final JPanel buttonsContainer;
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                        Constructeur                                          */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public UserInterface()
    {
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.networkCanvas = new NetworkCanvas();
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
        this.networkCanvas.setPreferredSize(new Dimension(900, 900));
        this.graphContainer.add(networkCanvas, BorderLayout.CENTER);
        this.graphContainer.setBackground(Color.black);
        this.getContentPane().add(graphContainer, BorderLayout.CENTER);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**              Initialise le JPanel qui va contenir les boutons d'option                       */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private void setupButtonsContainer()
    {
        this.buttonsContainer.setBackground(Color.red);
        this.buttonsContainer.add(this.createDisplayedTypeChangerButton("Bus",Node.BUS));
        this.buttonsContainer.add(this.createDisplayedTypeChangerButton("Subway",Node.SUBWAY));
        this.buttonsContainer.add(this.createDisplayedTypeChangerButton("Rail",Node.RAIL));
        this.buttonsContainer.add(this.createDisplayedTypeChangerButton("Undefined",Node.UNDEFINED));

        this.getContentPane().add(buttonsContainer, BorderLayout.SOUTH);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**          Créer un bouton pour définir le type de noeuds affichés dans NetworkCanvas          */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private JButton createDisplayedTypeChangerButton(String text, int nodeType)
    {
        JButton button = new JButton(text);
        button.addActionListener(e -> {networkCanvas.setDisplayedNodeType(nodeType);repaint();});
        return button;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                Définit le réseau à afficher                                  */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void displayNetwork(Network network) {this.networkCanvas.setNetwork(network);}
}
