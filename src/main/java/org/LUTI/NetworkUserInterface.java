package org.LUTI;

import javax.swing.*;
import java.awt.*;
///////////////////////////////////////////////////////////////////////////////////////////////////
/**          Affiche un NetworkCanvas et permet de définir ses paramètres                        */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class NetworkUserInterface extends JFrame
{
    private final NetworkCanvas networkCanvas;
    private final JPanel graphContainer;
    private final JPanel buttonsContainer;
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                        Constructeur                                          */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public NetworkUserInterface()
    {
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.networkCanvas = new NetworkCanvas();
        this.graphContainer = new JPanel();
        this.buttonsContainer = new JPanel();
        this.setupGraphContainer();
        this.setupButtonsContainer();
        this.setSize(new Dimension(1000,1000));
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                      Initialise le JPanel qui va contenir le Graph                          */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private void setupGraphContainer()
    {
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
        this.buttonsContainer.add(new JButton("Bus"));
        this.buttonsContainer.add(new JButton("Subway"));
        this.buttonsContainer.add(new JButton("Tram"));
        this.buttonsContainer.add(new JButton("Rail"));
        this.getContentPane().add(buttonsContainer, BorderLayout.SOUTH);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                Définit le réseau à afficher                                  */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void displayNetwork(Network network) {this.networkCanvas.setNetwork(network);}
}
