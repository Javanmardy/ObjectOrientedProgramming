package com.atc.model;
import com.atc.data.FlightVO;
import com.atc.enums.FlightDirection;

public class Flight {
    private final FlightVO info;
    private Runway runway;
    public Flight(FlightVO info) { this.info = info; }
    public String flightNumber() { return info.flightNumber(); }
    public String origin() { return info.origin(); }
    public String destination() { return info.destination(); }
    public FlightDirection direction() { return info.direction(); }
    public void assignRunway(Runway r) { this.runway = r; }
}