package server.TorServer;

import server.DB;
import server.EventDetails;

import java.util.HashMap;

public class TorDBController implements DB {

    private static HashMap<String, HashMap<String, EventDetails>> database = new HashMap<>();

    @Override
    public boolean addEvent() {
        return false;
    }

    @Override
    public boolean removeEvent() {
        return false;
    }

    @Override
    public boolean listEventAvailability() {
        return false;
    }

    @Override
    public boolean bookEvent(String clientID, String eventID) {
        return false;
    }

    @Override
    public boolean getBookingSchedule() {
        return false;
    }

    @Override
    public boolean cancelEvent() {
        return false;
    }
}
