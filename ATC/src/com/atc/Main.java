package com.atc;

import com.atc.client.*;
import com.atc.data.FlightDTO;
import com.atc.logic.FlightManager;
import com.atc.mapper.FlightMapper;
import com.atc.model.Flight;
import com.atc.model.Runway;
import com.atc.util.Logger;
import com.atc.util.TimerScheduler;
import java.util.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Logger logger = new Logger();
        List<Runway> runways = List.of(
                new Runway("RW-01", logger),
                new Runway("RW-02", logger),
                new Runway("RW-03", logger),
                new Runway("RW-04", logger)
        );

        FlightManager manager = new FlightManager(runways, logger);
        FlightApiClient api = new StubFlightApiClient();
        List<Flight> flights = api.fetchFlights().stream().map(FlightMapper::toDomain).toList();
        Random rnd = new Random();

        for (Flight f : flights) {
            long delay = 2000L + rnd.nextInt(15000);
            long occupy = 10000L + rnd.nextInt(20000);
            TimerScheduler.schedule(() -> {
                manager.assignRunway(f);
                TimerScheduler.schedule(() -> manager.releaseRunway(f), occupy);
            }, delay);
        }

        Thread.sleep(80000);
    }
}