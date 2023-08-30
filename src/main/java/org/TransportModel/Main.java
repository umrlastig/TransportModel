package org.TransportModel;

import org.TransportModel.generation.FlowDistributor;
import org.TransportModel.generation.Zone;
import org.TransportModel.generation.io.CommunesReader;
import org.TransportModel.network.Network;
import org.TransportModel.network.io.NetworkReaderBDTOPO;
import org.TransportModel.network.io.NetworkReaderGTFS;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.util.HashMap;


///////////////////////////////////////////////////////////////////////////////////////////////////
/**                                       Main Class                                             */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class Main
{
    static long last = System.currentTimeMillis();
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                        Main Function                                         */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static void main(String[] args)
    {
        //Set Zones
        HashMap<String,Zone> zones = CommunesReader.readShapeFile(); t();
        RealVector population = CommunesReader.readPopulationFile(zones); t();

        RealVector workersInPopulation = CommunesReader.readWorkersInPopulationFile(zones); t();
        RealVector jobsAtWorkplace = CommunesReader.readJobsAtWorkplaceFile(zones); t();

        RealVector studentsInPopulation = CommunesReader.readStudentsInPopulationFile(zones);
        RealVector educationAtStudyPlace = CommunesReader.readEducationAtStudyPlaceFile(zones);

        RealMatrix observedWorkFlows = CommunesReader.readWorkFlowsFile(zones); t();
        RealMatrix observedStudyFlows = CommunesReader.readStudyFlowsFile(zones); t();
        //TI
        Network networkTI = NetworkReaderBDTOPO.readFiles(); t();
        networkTI.removeNotStronglyConnectedNodes(); t();
        networkTI.removeTransitNodes(); t();
        networkTI.linkZones(zones); t();
        //TC
        Network networkTC = NetworkReaderGTFS.readFiles(); t();
        networkTC.removeNotStronglyConnectedNodes(); t();
        networkTC.removeTransitNodes(); t();
        networkTC.linkZones(zones); t();
        //Calculate RealFlows
        RealMatrix minTimes = FlowDistributor.calculateMinTimes(zones,networkTC,networkTI); t();
        RealVector[] weights = FlowDistributor.calculateWeightsFratar(workersInPopulation,jobsAtWorkplace,minTimes); t();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Display the elapsed time since the last call */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static void t()
    {
        System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber()+":"+(System.currentTimeMillis()-last));
        last = System.currentTimeMillis();
    }
}