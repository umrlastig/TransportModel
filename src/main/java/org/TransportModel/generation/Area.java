package org.TransportModel.generation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

///////////////////////////////////////////////////////////////////////////////////////////////////
/**  */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class Area
{
    double[][] studentFlows;
    double[][] workFlowsTC;
    double[][] workFlowsTI;
    HashMap<String,Zone> zones;
    HashMap<String,Integer> zonesIndex;
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Constructor */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public Area()
    {
        this.zones = new HashMap<>();
        this.zonesIndex = new HashMap<>();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Getters/Setters */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("unused")
    public double getWorkFlowTC(String fromID, String toID) {return this.workFlowsTC[zonesIndex.get(fromID)][zonesIndex.get(toID)];}
    @SuppressWarnings("unused")
    public double getWorkFlowTI(String fromID, String toID) {return this.workFlowsTI[zonesIndex.get(fromID)][zonesIndex.get(toID)];}
    public List<Zone> getZones(){return new ArrayList<>(this.zones.values());}
    public Zone getZone(String id){return this.zones.get(id);}
    public boolean containsZone(String id){return this.zones.containsKey(id);}
    public void addZone(Zone zone)
    {
        this.zones.put(zone.getId(),zone);
        this.zonesIndex.put(zone.getId(),this.zones.size());
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**  */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void addWorkFlowTC(String fromId, String toId, double weighting)
    {
        if(this.workFlowsTC == null)
            this.workFlowsTC = new double[zonesIndex.size()+1][zonesIndex.size()+1];
        this.workFlowsTC[zonesIndex.get(fromId)][zonesIndex.get(toId)] += weighting;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**  */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void addWorkFlowTI(String fromId, String toId, double weighting)
    {
        if(this.workFlowsTI == null)
            this.workFlowsTI = new double[zonesIndex.size()+1][zonesIndex.size()+1];
        this.workFlowsTI[zonesIndex.get(fromId)][zonesIndex.get(toId)] += weighting;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**  */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void addStudentFlow(String fromId, String toId, double weighting)
    {
        if(this.studentFlows == null)
            this.studentFlows = new double[zonesIndex.size()+1][zonesIndex.size()+1];
        this.studentFlows[zonesIndex.get(fromId)][zonesIndex.get(toId)]+=weighting;
    }
}
