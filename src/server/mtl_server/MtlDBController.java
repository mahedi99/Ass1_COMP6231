package server.mtl_server;

import server.LogUtils;
import server.Utils;
import server.database.DB;
import server.database.EventDetails;
import server.database.EventType;
import server.database.RequestType;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

public class MtlDBController implements DB {

    private static Map<EventType, ConcurrentHashMap<String, EventDetails>> database = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Integer> eventInOtherCitiesMap = new ConcurrentHashMap<>();

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
                eventDetails.eventID = eventID;

                if (!database.containsKey(eventType)){
                    database.put(eventType, new ConcurrentHashMap<>());
                }
                if(!database.get(eventType).containsKey(eventID)){
                    database.get(eventType).putIfAbsent(eventID, eventDetails);
                    response =  "Event "+ eventID + " Added!";
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
        LogUtils.writeToFile("mtl_server.txt", RequestType.ADD_EVENT + " | " + "Event ID : " + eventID + " | " + "Booking Capacity : " + bookingCapacity + "\nResponse : " + response);
        return response;
    }

    @Override
    public synchronized String removeEvent(String eventID, EventType eventType) {
        String response = "false";
        switch (eventID.substring(0, 3)) {
            case "MTL":
                if (database.containsKey(eventType)){
                    if(database.get(eventType).containsKey(eventID)){
                        database.get(eventType).remove(eventID);
                        response = "successfully deleted!";
                    }
                }
                break;
        }
        LogUtils.writeToFile("mtl_server.txt", RequestType.REMOVE_EVENT + " | " + "Event ID : " + eventID + "\nResponse : " + response);
        return response;
    }

    @Override
    public synchronized String listEventAvailability(EventType eventType) {
        String response = "false";

        ExecutorService service = Executors.newFixedThreadPool(3);
        Future<String> otw = service.submit(new CalculateEvent(Utils.OTW_SERVER_PORT, eventType));
        Future<String> tor = service.submit(new CalculateEvent(Utils.TOR_SERVER_PORT, eventType));
        Future<String> mtl = service.submit(() -> {
            String tmpResponse = "";
            if (database.containsKey(eventType)) {
                ConcurrentHashMap<String, EventDetails> allEvents = database.get(eventType);
                Set<String> keys = allEvents.keySet();
                for (String tmpKey : keys) {
                    EventDetails tmpEvent = allEvents.get(tmpKey);
                    tmpResponse = tmpResponse + tmpEvent.eventID + " " + tmpEvent.spaceAvailable() + "|";
                }
            }
            return tmpResponse;
        });

        service.shutdown();

        try {
            service.awaitTermination(5, TimeUnit.SECONDS); //waits at-most 5 seconds
            response  = mtl.get() + otw.get() + tor.get();
            response = response.replace("|", ", ").trim();
            response = response.substring(0, response.length() - 1) + ".";
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.writeToFile("mtl_server.txt", RequestType.LIST_EVENT_AVAILABILITY + "\nResponse : " + response);
        return response;
    }
    public synchronized String listEventAvailabilityForOthers(EventType eventType){
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
                case "TOR" :
                    String tmpKey = customerID + eventID.substring(6, eventID.length()).trim(); //tmpKey represents the month+year : 0519
                    if (!eventInOtherCitiesMap.containsKey(tmpKey) || (eventInOtherCitiesMap.containsKey(tmpKey) && (eventInOtherCitiesMap.get(tmpKey) < 3))){
                        String UDPMsg = RequestType.BOOK_EVENT + "|" + customerID + "|" + eventID + "|" + eventType;
                        response = MtlServer.sendMsg(Utils.TOR_SERVER_PORT, UDPMsg);
                        if (response.contains("added")){

                            if (eventInOtherCitiesMap.containsKey(tmpKey)){
                                int i = eventInOtherCitiesMap.get(tmpKey);
                                eventInOtherCitiesMap.put(tmpKey, i+1);
                            }
                            else{
                                eventInOtherCitiesMap.put(tmpKey, 1);
                            }
                        }
                    }
                    else {
                        response = "You can book at-most 3 events from other cities!";
                    }
                    break;
                case "OTW" :
                    String tmpKey2 = customerID + eventID.substring(6, eventID.length()).trim(); //tmpKey represents the month+year : 0519
                    if (!eventInOtherCitiesMap.containsKey(tmpKey2) || (eventInOtherCitiesMap.containsKey(tmpKey2) && (eventInOtherCitiesMap.get(tmpKey2) < 3))){
                        String UDPMsg = RequestType.BOOK_EVENT + "|" + customerID + "|" + eventID + "|" + eventType;
                        response = MtlServer.sendMsg(Utils.OTW_SERVER_PORT, UDPMsg);
                        if (response.contains("added")){
                            if (eventInOtherCitiesMap.containsKey(tmpKey2)){
                                int i = eventInOtherCitiesMap.get(tmpKey2);
                                eventInOtherCitiesMap.put(tmpKey2, i+1);
                            }
                            else{
                                eventInOtherCitiesMap.put(tmpKey2, 1);
                            }
                        }
                    }
                    else {
                        response = "You can book at-most 3 events from other cities!";
                    }
                    break;
                case "MTL" :
                    if (database.containsKey(eventType)){
                        if(database.get(eventType).containsKey(eventID)){
                            EventDetails event = database.get(eventType).get(eventID);
                            if ( 0 < event.spaceAvailable()){
                                if(event.listCustomers.add(customerID)){
                                    event.totalBooked ++;
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
        LogUtils.writeToFile("mtl_server.txt", RequestType.BOOK_EVENT + " | " + "Event ID : " + eventID + " | " + "Customer ID : " + customerID + "\nResponse : " + response);
        return response;
    }

    @Override
    public synchronized String getBookingSchedule(String customerID) {
        String response;
        String UDPMsg = RequestType.GET_BOOKING_SCHEDULE + "|" + customerID;
        response = MtlServer.sendMsg(Utils.TOR_SERVER_PORT, UDPMsg);
        response = response + MtlServer.sendMsg(Utils.OTW_SERVER_PORT, UDPMsg);

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
        LogUtils.writeToFile("mtl_server.txt", RequestType.GET_BOOKING_SCHEDULE + " | " + "Customer ID : " + customerID + "\nResponse : " + response);
        return "All events  : " + response;
    }
    public synchronized String getBookingScheduleForOthers(String customerID){
        String response = "";
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
        return response;
    }

    @Override
    public synchronized String cancelEvent(String customerID, String eventID, EventType eventType) {
        String response = "false";
        switch (eventID.substring(0, 3)) {
            case "OTW":
                String UDPMsg = RequestType.CANCEL_EVENT + "|" + customerID + "|" + eventID + "|" + eventType;
                response = MtlServer.sendMsg(Utils.OTW_SERVER_PORT, UDPMsg);
                break;
            case "TOR":
                String UDPMsg2 = RequestType.CANCEL_EVENT + "|" + customerID + "|" + eventID + "|" + eventType;
                response = MtlServer.sendMsg(Utils.TOR_SERVER_PORT, UDPMsg2);
                break;
            case "MTL":
                if (database.containsKey(eventType)){
                    ConcurrentHashMap<String, EventDetails> allEvents = database.get(eventType);
                    Set<String> keys = allEvents.keySet();
                    for (String tmpKey : keys){
                        EventDetails tmpEvent = allEvents.get(tmpKey);
                        if (eventID.equals(tmpEvent.eventID)){
                            response = "" + allEvents.get(eventID).listCustomers.remove(customerID);
                            response = (response.contains("true")? "successfully removed!" : "unsuccessful");
                            break;
                        }
                    }
                }

                break;
        }
        LogUtils.writeToFile("mtl_server.txt", RequestType.CANCEL_EVENT + " | " + "Event ID : " + eventID + " | " + "Customer ID : " + customerID + "\nResponse : " + response);
        return response;
    }

    class CalculateEvent implements Callable<String> {

        private final int portNum;
        private final EventType eventType;

        CalculateEvent(int portNum, EventType eventType) {
            this.portNum = portNum;
            this.eventType = eventType;
        }

        @Override
        public String call() {
            String UDPMsg = RequestType.LIST_EVENT_AVAILABILITY + "|" + eventType;
            return MtlServer.sendMsg(portNum, UDPMsg);
        }
    }
}
