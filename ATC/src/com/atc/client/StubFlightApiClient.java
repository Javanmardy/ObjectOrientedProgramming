package com.atc.client;
import com.atc.data.FlightDTO;
import com.atc.enums.FlightDirection;
import java.time.LocalDateTime;
import java.util.*;

public class StubFlightApiClient implements FlightApiClient {
    private static final String HUB = "IKA";
    private static final String[] DESTS = { "LHR", "CDG", "DXB", "FRA", "IST", "DOH", "KUL", "BKK", "AMS", "JFK" };

    public List<FlightDTO> fetchFlights() {
        List<FlightDTO> list = new ArrayList<>();
        Random rnd = new Random();
        for (int i = 0; i < 10; i++) {
            String fn = "IR" + (100 + i);
            boolean dep = rnd.nextBoolean();
            String destCity = DESTS[rnd.nextInt(DESTS.length)];
            String origin = dep ? HUB : destCity;
            String destination = dep ? destCity : HUB;
            list.add(new FlightDTO(fn, origin, destination,
                    LocalDateTime.now().plusMinutes(5 + rnd.nextInt(15)).toString(),
                    LocalDateTime.now().plusMinutes(25 + rnd.nextInt(15)).toString(),
                    "EP-" + (char)('A' + i), "A320",
                    dep ? FlightDirection.DEPARTURE : FlightDirection.ARRIVAL));
        }
        Collections.shuffle(list);
        return list;
    }
}