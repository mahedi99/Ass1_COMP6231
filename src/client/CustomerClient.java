package client;

import server.database.EventType;
import server.database.RequestType;
import java.util.Scanner;
import server.rmi.RMIInterface;
import java.rmi.Naming;


public class CustomerClient {
    private static Scanner sc = new Scanner(System.in);
    private RequestType requestType;
    private String customerClientID;
    private String eventID;
    private EventType eventType;



    public void customerClientRequest(String id){

        ClientServerController cccsc = ClientServerController.getInstance();

        customerClientID = id;
        System.out.println("enter your request : ");
        requestType = RequestType.valueOf(sc.next());

        if(requestType == RequestType.BOOK_EVENT) {
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

            cccsc.makeRmiRequestCustomerBookEvent(customerClientID, requestType, eventID, eventType);

        }
        else if(requestType == RequestType.GET_BOOKING_SCHEDULE) {

            cccsc.makeRmiRequestCustomerGetBookingSchedule(customerClientID, requestType);

        }
        else if(requestType == RequestType.CANCEL_EVENT) {
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
           cccsc.makeRmiRequestCustomerCancelEvent(customerClientID, requestType, eventID, eventType);

        }
        else {

            cccsc.makeRmiRequestCustomer(customerClientID, requestType);

        }

    }
}
