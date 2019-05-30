package server.tor_server;

import server.database.DB;
import server.database.EventDetails;
import server.database.EventType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TorDBController implements DB {

    private static Map<EventType, ConcurrentHashMap<String, EventDetails>> database = new ConcurrentHashMap<>();

    private static TorDBController instance;
    private TorDBController(){

    }
    public static TorDBController getInstance(){
        if (instance == null){
            instance = new TorDBController();
        }
        return instance;
    }


    @Override
    public String addEvent(String eventID, EventType eventType, int bookingCapacity) {
        String response = "false";
        switch (eventID.substring(0, 3)){
            case "MTL" :
                break;
            case "TOR" :
//                String UDPMsg = RequestType.ADD_EVENT + "|" + eventID + "|" + eventType + "|" + bookingCapacity;
//                isSuccessful = MtlServer.sendMsg(Utils.TOR_SERVER_PORT, UDPMsg);

                EventDetails eventDetails = new EventDetails();
                eventDetails.bookingCapacity = bookingCapacity;

                //ConcurrentHashMap<String, EventDetails> tmpEvent = database.putIfAbsent(eventType, new ConcurrentHashMap<>());
                if (!database.containsKey(eventType)){
                    database.put(eventType, new ConcurrentHashMap<>());
                }
                //System.out.println(tmpEvent.size());
                if(!database.get(eventType).containsKey(eventID)){
                    database.get(eventType).putIfAbsent(eventID, eventDetails);
                    response =  "true";
                }
                else {
                    EventDetails details = database.get(eventType).get(eventID);
                    details.bookingCapacity = bookingCapacity;
                    database.get(eventType).put(eventID, eventDetails);
                    response = "Event already exists, capacity updated";
                }
                break;
            case "OTW" :
                break;
        }
        return response;
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
