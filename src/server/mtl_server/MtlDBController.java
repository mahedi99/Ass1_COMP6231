package server.mtl_server;

import server.Utils;
import server.database.DB;
import server.database.EventDetails;
import server.database.EventType;
import server.database.RequestType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MtlDBController implements DB {

    private static Map<EventType, ConcurrentHashMap<String, EventDetails>> database = new ConcurrentHashMap<>();

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
    public String addEvent(String eventID, EventType eventType, int bookingCapacity) {
        String response = "false";
        switch (eventID.substring(0, 3)){
            case "MTL" :
                EventDetails eventDetails = new EventDetails();
                eventDetails.bookingCapacity = bookingCapacity;

                if (!database.containsKey(eventType)){
                    database.put(eventType, new ConcurrentHashMap<>());
                }
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
            case "TOR" :
                String UDPMsg = RequestType.ADD_EVENT + "|" + eventID + "|" + eventType + "|" + bookingCapacity;
                response = MtlServer.sendMsg(Utils.TOR_SERVER_PORT, UDPMsg);
                break;
            case "OTW" :
                String UDPMsg2 = RequestType.ADD_EVENT + "|" + eventID + "|" + eventType + "|" + bookingCapacity;
                response = MtlServer.sendMsg(Utils.OTW_SERVER_PORT, UDPMsg2);
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
    public String cancelEvent(String customerID, String eventID, EventType eventType) {
        return "";
    }
}
