package server.tor_server;

import com.sun.corba.se.impl.oa.toa.TOA;
import server.Utils;
import server.database.DB;
import server.database.EventDetails;
import server.database.EventType;
import server.database.RequestType;

import java.util.Map;
import java.util.Set;
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
                eventDetails.eventID = eventID;

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
    public String listEventAvailabilityForOthers(EventType eventType){
        String response = "";
        if (database.containsKey(eventType)){
            ConcurrentHashMap<String, EventDetails> allEvents = database.get(eventType);
            Set<String> keys = allEvents.keySet();
            for (String tmpKey : keys){
                EventDetails tmpEvent = allEvents.get(tmpKey);
                response = response + tmpEvent.eventID + " " + tmpEvent.spaceAvailable() + "|";
            }
        }
        return response;
    }
    @Override
    public synchronized String bookEvent(String customerID, String eventID, EventType eventType) {
        String response = "unsuccessful";
        if (customerID.substring(3,5).contains("C")){


            switch (eventID.substring(0, 3)){
                case "MTL" :
                    String UDPMsg = RequestType.BOOK_EVENT + "|" + customerID + "|" + eventID + "|" + eventType;
                    response = TorServer.sendMsg(Utils.MTL_SERVER_PORT, UDPMsg);
                    break;
                case "OTW" :
                    String UDPMsg2 = RequestType.BOOK_EVENT + "|" + customerID + "|" + eventID + "|" + eventType;
                    response = TorServer.sendMsg(Utils.OTW_SERVER_PORT, UDPMsg2);
                    break;
                case "TOR" :
                    if (database.containsKey(eventType)){
                        if(database.get(eventType).containsKey(eventID)){
                            EventDetails event = database.get(eventType).get(eventID);
                            if ( 0 < event.spaceAvailable()){
                                if(event.listCustomers.add(customerID)){
                                    response = customerID + " added to " + eventID + " event.";
                                }
                                else {
                                    response = "Duplicate call for" + eventID + " event";
                                }
                            }
                            else {
                                response = "Capacity full for " + eventID + " event.";
                            }
                        }
                    }

                    break;
            }
        }
        return response;
    }

    @Override
    public synchronized String getBookingSchedule(String customerID) {
        String response;
        String UDPMsg = RequestType.GET_BOOKING_SCHEDULE + "|" + customerID;
        response = TorServer.sendMsg(Utils.OTW_SERVER_PORT, UDPMsg);
        //response = response + TorServer.sendMsg(Utils.MTL_SERVER_PORT, UDPMsg);

        Set<EventType> keys = database.keySet();
        for (EventType tmpEventTypeKey : keys){
            Set<String> eventKeys = database.get(tmpEventTypeKey).keySet();
            for (String tmpEventKey : eventKeys){
                EventDetails tmpEvent = database.get(tmpEventTypeKey).get(tmpEventKey);
                if (tmpEvent.listCustomers.contains(customerID)){
                    response = response + tmpEvent.eventID + "|";
                }
            }
        }
        return "All events  : " + response;
    }
    public String getBookingScheduleForOthers(String customerID){
        String response = "";
        Set<EventType> keys = database.keySet();
        for (EventType tmpEventTypeKey : keys){
            Set<String> eventKeys = database.get(tmpEventTypeKey).keySet();
            for (String tmpEventKey : eventKeys){
                EventDetails tmpEvent = database.get(tmpEventTypeKey).get(tmpEventKey);
                for (String s : tmpEvent.listCustomers){
                    System.out.println("Client : "+s);
                }
                if (tmpEvent.listCustomers.contains(customerID)){
                    response = response + tmpEvent.eventID + "|";
                }
            }
        }
        System.out.println("response : " +response);
        return response;
    }

    @Override
    public String cancelEvent(String customerID, String eventID, EventType eventType) {
        return "";
    }
}
