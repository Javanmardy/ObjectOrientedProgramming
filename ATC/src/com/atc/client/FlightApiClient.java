package com.atc.client;
import com.atc.data.FlightDTO;
import java.util.List;

public interface FlightApiClient {
    List<FlightDTO> fetchFlights();
}