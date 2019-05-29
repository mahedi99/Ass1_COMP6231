package server.otw_server;

import server.database.DB;
import server.database.EventDetails;
import server.database.EventType;

import java.util.HashMap;

public class OtwDBController implements DB {

    private static HashMap<String, HashMap<String, EventDetails>> database = new HashMap<>();

    @Override
    public boolean addEvent(String eventID, EventType eventType, int bookingCapacity) {
        return false;
    }

    @Override
    public boolean removeEvent(String eventID, EventType eventType) {
        return false;
    }

    @Override
    public boolean listEventAvailability(EventType eventType) {
        return false;
    }

    @Override
    public boolean bookEvent(String customerID, String eventID, EventType eventType) {
        return false;
    }

    @Override
    public boolean getBookingSchedule(String customerID) {
        return false;
    }

    @Override
    public boolean cancelEvent(String customerID, String eventID) {
        return false;
    }
}
