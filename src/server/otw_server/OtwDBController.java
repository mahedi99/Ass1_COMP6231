package server.otw_server;

import server.Utils;
import server.database.DB;
import server.database.EventDetails;
import server.database.EventType;
import server.database.RequestType;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

public class OtwDBController implements DB {

    private static Map<EventType, ConcurrentHashMap<String, EventDetails>> database = new ConcurrentHashMap<>();

    private static OtwDBController instance;
    private OtwDBController(){

    }
    public static OtwDBController getInstance(){
        if (instance == null){
            instance = new OtwDBController();
        }
        return instance;
    }

    @Override
    public String addEvent(String eventID, EventType eventType, int bookingCapacity) {
        String response = "false";
        switch (eventID.substring(0, 3)) {
            case "MTL":
                String UDPMsg2 = RequestType.ADD_EVENT + "|" + eventID + "|" + eventType + "|" + bookingCapacity;
                response = OtwServer.sendMsg(Utils.MTL_SERVER_PORT, UDPMsg2);
                break;
            case "TOR":
                String UDPMsg = RequestType.ADD_EVENT + "|" + eventID + "|" + eventType + "|" + bookingCapacity;
                response = OtwServer.sendMsg(Utils.TOR_SERVER_PORT, UDPMsg);
                break;
            case "OTW":
                EventDetails eventDetails = new EventDetails();
                eventDetails.bookingCapacity = bookingCapacity;
                eventDetails.eventID = eventID;

                if (!database.containsKey(eventType)) {
                    database.put(eventType, new ConcurrentHashMap<>());
                }
                if (!database.get(eventType).containsKey(eventID)) {
                    database.get(eventType).putIfAbsent(eventID, eventDetails);
                    response = "true";
                } else {
                    EventDetails details = database.get(eventType).get(eventID);
                    details.bookingCapacity = bookingCapacity;
                    database.get(eventType).put(eventID, eventDetails);
                    response = "Event already exists, capacity updated!";
                }
                break;
        }
        return response;
    }

    @Override
    public String removeEvent(String eventID, EventType eventType) {
        String response = "false";
        switch (eventID.substring(0, 3)) {
            case "OTW":
                if (database.containsKey(eventType)){
                    if(database.get(eventType).containsKey(eventID)){
                        database.get(eventType).remove(eventID);
                        response = "successfully deleted!";
                    }
                }
                break;
        }

        return response;
    }

    @Override
    public String listEventAvailability(EventType eventType) {
//        String[] response  = new String[3];
        String response = "false";
//        Thread mtl = new Thread(() -> {
//            String UDPMsg2 = RequestType.LIST_EVENT_AVAILABILITY + "|" + eventType;
//            response[0] = OtwServer.sendMsg(Utils.MTL_SERVER_PORT, UDPMsg2);
//        }).start();
//        Thread tor = new Thread(() -> {
//            String UDPMsg2 = RequestType.LIST_EVENT_AVAILABILITY + "|" + eventType;
//            response[1] = OtwServer.sendMsg(Utils.TOR_SERVER_PORT, UDPMsg2);
//        }).start();
//
//        try {
//            mtl.join();
//            tor.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        ExecutorService service = Executors.newFixedThreadPool(3);
        //Future<String> mtl = service.submit(new CalculateEvent(Utils.MTL_SERVER_PORT, eventType));
        Future<String> tor = service.submit(new CalculateEvent(Utils.TOR_SERVER_PORT, eventType));
        Future<String> otw = service.submit(() -> {
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
            response  = otw.get() + tor.get();
            response = response.replace("|", ", ").trim();
            response = response.substring(0, response.length() - 1) + ".";
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
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

    //Also, a customer can book as many events in his/her own
    //city, but only at most 3 events from other cities overall in a month. (yet to implement)***
    @Override
    public String bookEvent(String customerID, String eventID, EventType eventType) {
        String response = "unsuccessful";
        if (customerID.substring(3,5).contains("C")){
            switch (eventID.substring(0, 3)){
                case "MTL" :
                    String UDPMsg = RequestType.BOOK_EVENT + "|" + customerID + "|" + eventID + "|" + eventType;
                    response = OtwServer.sendMsg(Utils.MTL_SERVER_PORT, UDPMsg);
                    break;
                case "TOR" :
                    String UDPMsg2 = RequestType.BOOK_EVENT + "|" + customerID + "|" + eventID + "|" + eventType;
                    response = OtwServer.sendMsg(Utils.TOR_SERVER_PORT, UDPMsg2);
                    break;
                case "OTW" :
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
        response = OtwServer.sendMsg(Utils.TOR_SERVER_PORT, UDPMsg);
        //response = response + OtwServer.sendMsg(Utils.MTL_SERVER_PORT, UDPMsg);

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
            return OtwServer.sendMsg(portNum, UDPMsg);
        }
    }
}
