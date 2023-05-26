package org.LUTI;

import java.util.List;
///////////////////////////////////////////////////////////////////////////////////////////////////
/**                                       Main Class                                             */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class Main
{
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                        Main Function                                         */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static void main(String[] args)
    {
        //Créer un reseau à partir d'un fichier
        String csvPath = "C:\\Documents\\Prog\\Java\\LUTI_Model_Maven\\src\\main\\java\\org\\LUTI\\TC.csv";
        List<String[]> dataLines = NetworkReader.extractDataCSV(csvPath, ';');
        Network network = new Network();
        network.addGraph(NetworkReader.createGlobalGraph(dataLines));

        //Affiche le réseau
        UserInterface networkUserInterface = new UserInterface();
        networkUserInterface.displayNetwork(network);
    }
}