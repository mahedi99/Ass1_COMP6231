package server.OtwServer;

import server.DB;
import server.EventDetails;

import java.util.HashMap;

public class OtwDBController implements DB {

    private static HashMap<String, HashMap<String, EventDetails>> database = new HashMap<>();

    @Override
    public boolean addEvent(String eventID, String eventType, int bookingCapacity) {
        return false;
    }

    @Override
    public boolean removeEvent(String eventID, String eventType) {
        return false;
    }

    @Override
    public boolean listEventAvailability(String eventType) {
        return false;
    }

    @Override
    public boolean bookEvent(String customerID, String eventID, String eventType) {
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
