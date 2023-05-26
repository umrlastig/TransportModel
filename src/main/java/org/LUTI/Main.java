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
        String csvPath = "C:\\Documents\\Prog\\Java\\LUTI_Model_Maven\\src\\main\\java\\org\\LUTI\\TC.csv";

        List<String[]> dataLines = NetworkReader.extractDataCSV(csvPath, ';');
        List<TC_Line> transportLines = NetworkReader.createTCLines(dataLines);
        Network network = new Network();
        for(TC_Line transportLine: transportLines)
            network.addTC_Line(transportLine);
        NetworkUserInterface networkUserInterface = new NetworkUserInterface();
        networkUserInterface.displayNetwork(network);
        networkUserInterface.setVisible(true);
    }
}