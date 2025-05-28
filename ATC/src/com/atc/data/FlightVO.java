package com.atc.data;
import com.atc.enums.FlightDirection;
import java.time.LocalDateTime;

public class FlightVO {
    private final String flightNumber, origin, destination;
    private final Airplane airplane;
    private final LocalDateTime departure, arrival;
    private final FlightDirection direction;
    public FlightVO(String fn, Airplane airplane, String org, String dest,
                    LocalDateTime dep, LocalDateTime arr, FlightDirection dir) {
        this.flightNumber = fn;
        this.airplane = airplane;
        this.origin = org;
        this.destination = dest;
        this.departure = dep;
        this.arrival = arr;
        this.direction = dir;
    }
    public String flightNumber() { return flightNumber; }
    public String origin() { return origin; }
    public String destination() { return destination; }
    public FlightDirection direction() { return direction; }
}