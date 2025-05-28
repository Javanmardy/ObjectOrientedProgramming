package com.atc.mapper;
import com.atc.data.*;
import com.atc.model.Flight;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FlightMapper {
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_DATE_TIME;
    public static Flight toDomain(FlightDTO d) {
        FlightVO vo = new FlightVO(d.flightNumber(), new Airplane(d.registration(), d.model()),
                d.origin(), d.destination(),
                LocalDateTime.parse(d.departureISO(), ISO),
                LocalDateTime.parse(d.arrivalISO(), ISO),
                d.direction());
        return new Flight(vo);
    }
}