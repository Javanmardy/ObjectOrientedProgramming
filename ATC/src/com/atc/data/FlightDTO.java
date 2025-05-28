package com.atc.data;
import com.atc.enums.FlightDirection;
public record FlightDTO(
    String flightNumber,
    String origin,
    String destination,
    String departureISO,
    String arrivalISO,
    String registration,
    String model,
    FlightDirection direction
) {}