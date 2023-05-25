package org.LUTI;

/** MainClasse **/
public class Main
{
    /** MainFonction **/
    public static void main(String[] args)
    {
        Network network = new Network();
        NetworkReader reader = new NetworkReader(network);
        reader.readFile("C:\\Documents\\Prog\\Java\\LUTI_Model_Maven\\src\\main\\java\\org\\LUTI\\TC.csv");
        NetworkCanvas networkCanvas = new NetworkCanvas(network);
        UserInterface userInterface = new UserInterface();
        userInterface.addJComponent(networkCanvas);
    }
}