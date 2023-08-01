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
    static Thread t = Thread.currentThread();
    static long nbr = 0;
    static long last =  System.currentTimeMillis();
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                        Main Function                                         */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static void main(String[] args)
    {
        /*
        //Set Zones
        HashMap<String,Zone> zones = CommunesReader.readShapeFile(); t();
        RealVector population = CommunesReader.readPopulationFile(zones); t();
        RealVector workersInPopulation = CommunesReader.readWorkersInPopulationFile(zones); t();
        RealVector jobsAtWorkplace = CommunesReader.readJobsAtWorkplaceFile(zones); t();
        RealVector studentsInPopulation = CommunesReader.readStudentsInPopulationFile(zones); t();
        RealVector educationAtStudyPlace = CommunesReader.readEducationAtStudyPlaceFile(zones); t();
        RealMatrix observedStudyFlows = CommunesReader.readStudyFlowsFile(zones); t();
        RealMatrix observedWorkFlows = CommunesReader.readWorkFlowsFile(zones); t();*/
        //Set Networks
        Network networkTC = NetworkReaderGTFS.readFiles(); t();
        Network networkTI = NetworkReaderBDTOPO.readFiles(); t();
        networkTC.removeNotStronglyConnected(); t();
        networkTI.removeNotStronglyConnected(); t();/*
        FlowDistributor.linkZones(networkTC,zones); t();
        FlowDistributor.linkZones(networkTI,zones); t();
        //Calculate RealFlows
        RealMatrix minTimes = FlowDistributor.calculateMinTimes(zones,networkTC,networkTI); t();
        FlowDistributor.calculateWeights(workersInPopulation,jobsAtWorkplace,minTimes); t();
        //FlowDistributor.calculateWeights(studentsInPopulation,educationAtStudyPlace,minTimes);t();*/
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                                                                              */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static void t()
    {
        System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber()+":"+(System.currentTimeMillis()-last));
        last=System.currentTimeMillis();
    }
}