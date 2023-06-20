package org.TransportModel.network;

///////////////////////////////////////////////////////////////////////////////////////////////////
/** */
///////////////////////////////////////////////////////////////////////////////////////////////////
public enum ROUTE_TYPE
{
    TRAM_OR_LIGHT_SUBWAY(100), TRAIN(500), BUS(50), FOOT(4),SUBWAY(5),
    UNDEFINED(9), CAR(1);
    private final int capacity;
    ROUTE_TYPE(int capacity) {this.capacity = capacity;}
    public int getCapacity() {return capacity;}
}
