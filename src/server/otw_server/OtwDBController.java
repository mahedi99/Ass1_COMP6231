package server.otw_server;

import server.LogUtils;
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
    //private static ConcurrentHashMap<String, Integer> eventInOtherCitiesMap = new ConcurrentHashMap<>();

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
    public synchronized String addEvent(String eventID, EventType eventType, int bookingCapacity) {
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
                    response =  "Event "+ eventID + " Added!";
                } else {
                    EventDetails details = database.get(eventType).get(eventID);
                    details.bookingCapacity = bookingCapacity;
                    database.get(eventType).put(eventID, eventDetails);
                    response = "Event already exists, capacity updated!";
                }
                break;
        }
        LogUtils.writeToFile("otw_server.txt", RequestType.ADD_EVENT + " | " + "Event ID : " + eventID + " | " + "Booking Capacity : " + bookingCapacity + "\nResponse : " + response);
        return response;
    }

    @Override
    public synchronized String removeEvent(String eventID, EventType eventType) {
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
        LogUtils.writeToFile("otw_server.txt", RequestType.REMOVE_EVENT + " | " + "Event ID : " + eventID + "\nResponse : " + response);
        return response;
    }

    @Override
    public synchronized String listEventAvailability(EventType eventType) {
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
        LogUtils.writeToFile("otw_server.txt", RequestType.LIST_EVENT_AVAILABILITY + "\nResponse : " + response);
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

    //Also, a customer can book as many events in his/her own
    //city, but only at most 3 events from other cities overall in a month. (yet to implement)***
    @Override
    public synchronized String bookEvent(String customerID, String eventID, EventType eventType) {
        String response = "unsuccessful";
        if (customerID.substring(3,5).contains("C")){

            String month = eventID.substring(6, eventID.length()).trim();
            int totalEventsOutside;
            String UDPMsg1 = RequestType.GET_TOTAL_EVENTS + "|" + customerID + "|" + month;
            String numberOfEvents = OtwServer.sendMsg(Utils.MTL_SERVER_PORT, UDPMsg1);
            totalEventsOutside = Integer.parseInt(numberOfEvents);
            if (totalEventsOutside < 3){
                UDPMsg1 = RequestType.GET_TOTAL_EVENTS + "|" + customerID + "|" + month;
                String numberOfEvents2 = OtwServer.sendMsg(Utils.TOR_SERVER_PORT, UDPMsg1);
                totalEventsOutside += Integer.parseInt(numberOfEvents2);
            }

            switch (eventID.substring(0, 3)){
                case "MTL" :
                    if (totalEventsOutside < 3){
                    //String tmpKey = customerID + eventID.substring(6, eventID.length()).trim(); //tmpKey represents the month+year : 0519
                    //if (!eventInOtherCitiesMap.containsKey(tmpKey) || (eventInOtherCitiesMap.containsKey(tmpKey) && (eventInOtherCitiesMap.get(tmpKey) < 3))){
                        String UDPMsg = RequestType.BOOK_EVENT + "|" + customerID + "|" + eventID + "|" + eventType;
                        response = OtwServer.sendMsg(Utils.MTL_SERVER_PORT, UDPMsg);
//                        if (response.contains("added")){
//
//                            if (eventInOtherCitiesMap.containsKey(tmpKey)){
//                                int i = eventInOtherCitiesMap.get(tmpKey);
//                                eventInOtherCitiesMap.put(tmpKey, i+1);
//                            }
//                            else{
//                                eventInOtherCitiesMap.put(tmpKey, 1);
//                            }
//                        }
                    }
                    else {
                        response = "You can book at-most 3 events from other cities!";
                    }
                    break;
                case "TOR" :
                    if (totalEventsOutside < 3){
                    //String tmpKey2 = customerID + eventID.substring(6, eventID.length()).trim(); //tmpKey represents the month+year : 0519
                    //if (!eventInOtherCitiesMap.containsKey(tmpKey2) || (eventInOtherCitiesMap.containsKey(tmpKey2) && (eventInOtherCitiesMap.get(tmpKey2) < 3))){
                        String UDPMsg = RequestType.BOOK_EVENT + "|" + customerID + "|" + eventID + "|" + eventType;
                        response = OtwServer.sendMsg(Utils.TOR_SERVER_PORT, UDPMsg);
//                        if (response.contains("added")){
//
//                            if (eventInOtherCitiesMap.containsKey(tmpKey2)){
//                                int i = eventInOtherCitiesMap.get(tmpKey2);
//                                eventInOtherCitiesMap.put(tmpKey2, i+1);
//                            }
//                            else{
//                                eventInOtherCitiesMap.put(tmpKey2, 1);
//                            }
//                        }
                    }
                    else {
                        response = "You can book at-most 3 events from other cities!";
                    }
                    break;
                case "OTW" :
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
        LogUtils.writeToFile("otw_server.txt", RequestType.BOOK_EVENT + " | " + "Event ID : " + eventID + " | " + "Customer ID : " + customerID + "\nResponse : " + response);
        return response;
    }


    public String getEventsForOneMonth(String customerID, String month){
        int countEvent = 0;
        Set<EventType> keys = database.keySet();
        for (EventType tmpEventType : keys){
            Set<String> eventKeys = database.get(tmpEventType).keySet();
            for (String tmpEventKey : eventKeys) {
                EventDetails tmpEvent = database.get(tmpEventType).get(tmpEventKey);
                String tmpMonth = tmpEvent.eventID.substring(6, tmpEvent.eventID.length()).trim();
                if (tmpEvent.listCustomers.contains(customerID) && tmpMonth.equals(month)){
                    countEvent ++;
                }
            }
        }
        return "" + countEvent;
    }

    @Override
    public synchronized String getBookingSchedule(String customerID) {
        String response;
        String UDPMsg = RequestType.GET_BOOKING_SCHEDULE + "|" + customerID;
        response = OtwServer.sendMsg(Utils.TOR_SERVER_PORT, UDPMsg);
        response = response + OtwServer.sendMsg(Utils.MTL_SERVER_PORT, UDPMsg);

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
        LogUtils.writeToFile("otw_server.txt", RequestType.GET_BOOKING_SCHEDULE + " | " + "Customer ID : " + customerID + "\nResponse : " + response);
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
        //System.out.println("response : " +response);
        return response;
    }

    @Override
    public synchronized String cancelEvent(String customerID, String eventID, EventType eventType) {
        String response = "false";
        switch (eventID.substring(0, 3)) {
            case "MTL":
                String UDPMsg = RequestType.CANCEL_EVENT + "|" + customerID + "|" + eventID + "|" + eventType;
                response = OtwServer.sendMsg(Utils.MTL_SERVER_PORT, UDPMsg);
                break;
            case "TOR":
                String UDPMsg2 = RequestType.CANCEL_EVENT + "|" + customerID + "|" + eventID + "|" + eventType;
                response = OtwServer.sendMsg(Utils.TOR_SERVER_PORT, UDPMsg2);
                break;
            case "OTW":
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
        LogUtils.writeToFile("otw_server.txt", RequestType.CANCEL_EVENT + " | " + "Event ID : " + eventID + " | " + "Customer ID : " + customerID + "\nResponse : " + response);
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
            return OtwServer.sendMsg(portNum, UDPMsg);
        }
    }
}
