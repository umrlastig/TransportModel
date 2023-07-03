package org.TransportModel.generation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

///////////////////////////////////////////////////////////////////////////////////////////////////
/**  */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class Area
{
    double[][] flowsTC;
    double[][] flowsTI;
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
    public double getFlowTC(String fromID, String toID) {return this.flowsTC[zonesIndex.get(fromID)][zonesIndex.get(toID)];}
    @SuppressWarnings("unused")
    public double getFlowTI(String fromID, String toID) {return this.flowsTI[zonesIndex.get(fromID)][zonesIndex.get(toID)];}
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
    public void addFlowTC(String fromId, String toId, double weighting)
    {
        if(this.flowsTC == null)
            this.flowsTC = new double[zonesIndex.size()+1][zonesIndex.size()+1];
        this.flowsTC[zonesIndex.get(fromId)][zonesIndex.get(toId)] += weighting;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**  */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void addFlowTI(String fromId, String toId, double weighting)
    {
        if(this.flowsTI == null)
            this.flowsTI = new double[zonesIndex.size()+1][zonesIndex.size()+1];
        this.flowsTI[zonesIndex.get(fromId)][zonesIndex.get(toId)] += weighting;
    }
}
