package server.otw_server;

import server.database.DB;
import server.database.EventDetails;
import server.database.EventType;

import java.util.HashMap;

public class OtwDBController implements DB {

    private static HashMap<String, HashMap<String, EventDetails>> database = new HashMap<>();

    @Override
    public String addEvent(String eventID, EventType eventType, int bookingCapacity) {
        return "";
    }

    @Override
    public String removeEvent(String eventID, EventType eventType) {
        return "";
    }

    @Override
    public String listEventAvailability(EventType eventType) {
        return "";
    }

    @Override
    public String bookEvent(String customerID, String eventID, EventType eventType) {
        return "";
    }

    @Override
    public String getBookingSchedule(String customerID) {
        return "";
    }

    @Override
    public String cancelEvent(String customerID, String eventID) {
        return "";
    }
}
