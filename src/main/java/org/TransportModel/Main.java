package org.TransportModel;

import org.TransportModel.GUI.UserInterface;
import org.TransportModel.network.Network;
import org.TransportModel.network.io.NetworkReader;
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
        //Build Network
        Network network = new Network();
        String nodeFilePath = "src/main/java/org/TransportModel/network/io/stops.txt";
        String linkFilePath = "src/main/java/org/TransportModel/network/io/pathways.txt";
        NetworkReader.readNodeFile(network,nodeFilePath,',');
        NetworkReader.readLinkFile(network,linkFilePath,',');

        //Display Network
        UserInterface networkUserInterface = new UserInterface();
        networkUserInterface.displayNetwork(network);
    }
}