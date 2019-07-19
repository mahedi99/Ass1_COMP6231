package client;

import server.database.EventType;
import server.database.RequestType;

import java.util.Scanner;

public class EventManagerClient {

    private static Scanner sc = new Scanner(System.in);

    private RequestType requestType;
    private String managerClientID;
    private String customerClientID;
    private String eventID;
    private EventType eventType;
    private int bookingCapacity;
    private String newEventID;
    private EventType newEventType;
    


    public EventManagerClient() {

    }

    public void eventManagerClientRequest(String id) {

        ClientServerController emccsc = ClientServerController.getInstance();

        managerClientID = id;
        System.out.println("enter your request : ");
        requestType = RequestType.valueOf(sc.next());

        if(requestType == RequestType.ADD_EVENT) {
            System.out.println("enter event id: ");
            eventID = sc.next();
            while(true) {
                System.out.println("enter event type: ");
                eventType = EventType.valueOf(sc.next()) ;
                if(eventType == EventType.CONFERENCE || eventType == EventType.SEMINAR || eventType== EventType.TRADE_SHOW)
                    break;
                else
                    System.out.println("event type is not valid");
            }

            System.out.println("set booking capacity ");
            bookingCapacity = sc.nextInt();
            emccsc.makeRmiRequestEventManagerAddEvent(managerClientID, requestType, eventID, eventType, bookingCapacity);

        }
        else if(requestType == RequestType.REMOVE_EVENT) {
            System.out.println("enter event id: ");
            eventID = sc.next();

            while(true) {
                System.out.println("enter event type: ");
                eventType = EventType.valueOf(sc.next()) ;
                if(eventType == EventType.CONFERENCE || eventType == EventType.SEMINAR || eventType== EventType.TRADE_SHOW)
                    break;
                else
                    System.out.println("event type is not valid");
            }

            emccsc.makeRmiRequestEventManagerRemoveEvent(managerClientID, requestType, eventID, eventType);

        }
        else if(requestType == RequestType.LIST_EVENT_AVAILABILITY) {

            while(true) {
                System.out.println("enter event type: ");
                eventType = EventType.valueOf(sc.next()) ;
                if(eventType == EventType.CONFERENCE || eventType == EventType.SEMINAR || eventType== EventType.TRADE_SHOW)
                    break;
                else
                    System.out.println("event type is not valid");
            }

            emccsc.makeRmiRequestEventManagerListEventAvailability(managerClientID, requestType, eventType);

        }
        else if(requestType == RequestType.BOOK_EVENT){

            System.out.println("enter event id: ");
            eventID = sc.next();
            System.out.println("enter customer id: ");
            customerClientID = sc.next();
            while(true) {
                System.out.println("enter event type: ");
                eventType = EventType.valueOf(sc.next()) ;
                if(eventType == EventType.CONFERENCE || eventType == EventType.SEMINAR || eventType== EventType.TRADE_SHOW)
                    break;
                else
                    System.out.println("event type is not valid");
            }

            emccsc.makeRmiRequestEventManagerBookEvent(managerClientID, customerClientID, requestType, eventID, eventType);

        }
        else if(requestType == RequestType.GET_BOOKING_SCHEDULE) {
            System.out.println("enter customer id: ");
            customerClientID = sc.next();
            emccsc.makeRmiRequestEventManagerGetBookingSchedule(managerClientID,customerClientID, requestType);

        }
        else if(requestType == RequestType.CANCEL_EVENT) {
            System.out.println("enter event id: ");
            eventID = sc.next();
            System.out.println("enter customer id: ");
            customerClientID = sc.next();
            while(true) {
                System.out.println("enter event type: ");
                eventType = EventType.valueOf(sc.next()) ;
                if(eventType == EventType.CONFERENCE || eventType == EventType.SEMINAR || eventType== EventType.TRADE_SHOW)
                    break;
                else
                    System.out.println("event type is not valid");
            }
            emccsc.makeRmiRequestEventManagerCancelEvent(managerClientID, customerClientID, requestType, eventID, eventType);

        }
        else if(requestType == RequestType.SWAP_EVENT){

            System.out.println("NEW enter event id: ");
            newEventID = sc.next();
            
            while(true) {
                System.out.println("NEW event type: ");
                newEventType = EventType.valueOf(sc.next()) ;
                if(newEventType == EventType.CONFERENCE || newEventType == EventType.SEMINAR || newEventType== EventType.TRADE_SHOW)
                    break;
                else
                    System.out.println("event type is not valid");
            }
            
            System.out.println("OLD enter event id: ");
            eventID = sc.next();
            while(true) {
                System.out.println("OLD event type: ");
                eventType = EventType.valueOf(sc.next()) ;
                if(eventType == EventType.CONFERENCE || eventType == EventType.SEMINAR || eventType== EventType.TRADE_SHOW)
                    break;
                else
                    System.out.println("event type is not valid");
            }
            System.out.println("enter customer id: ");
            customerClientID = sc.next();

            emccsc.makeRmiRequestEventManagerSwapEvent(managerClientID, customerClientID, newEventID, newEventType, eventID, eventType, requestType);

        }


    }


}
