package server.mtl_server;

import server.Utils;
import server.database.DB;
import server.database.EventDetails;
import server.database.EventType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MtlDBController implements DB {

    private static final Map<EventType, ConcurrentHashMap<String, EventDetails>> DATABASE = new ConcurrentHashMap<>();

    private static MtlDBController instance;
    private MtlDBController(){

    }
    public static MtlDBController getInstance(){
        if (instance == null){
            instance = new MtlDBController();
        }
        return instance;
    }

    @Override
    public boolean addEvent(String eventID, EventType eventType, int bookingCapacity) {
        switch (eventID.substring(0, 2)){
            case "MTL" :
                EventDetails eventDetails = new EventDetails();
                eventDetails.bookingCapacity = bookingCapacity;

                ConcurrentHashMap<String, EventDetails> tmpEvent = DATABASE.putIfAbsent(eventType, new ConcurrentHashMap<>());
                if(!tmpEvent.containsKey(eventID)){
                    DATABASE.get(eventType).putIfAbsent(eventID, eventDetails);
                    return true;
                }
                else {
                    EventDetails details = DATABASE.get(eventType).get(eventID);
                    details.bookingCapacity = bookingCapacity;
                    DATABASE.get(eventType).put(eventID, eventDetails);
                }
                break;
            case "TOR" :
                String UDPMsg = eventID + "|" + eventType + "|" + bookingCapacity;
                MtlServer.sendMsg(Utils.TOR_SERVER_PORT, UDPMsg);
                break;
            case "OTW" :
                break;
        }
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
