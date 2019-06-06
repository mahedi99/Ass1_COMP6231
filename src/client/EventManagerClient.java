package client;

import server.database.EventType;
import server.database.RequestType;

import java.util.Scanner;

public class EventManagerClient {

    private static Scanner sc = new Scanner(System.in);

    private RequestType requestType;
    private String managerClientID;
    private String eventID;
    private EventType eventType;
    private int bookingCapacity;

//    private int portno;

//    public EventManagerClient(){
//        String managerClientID, String eventID, int portno
//        this.managerClientID=managerClientID;
//        this.portno=portno;
//        EventID=eventID;
//        mcsc = ClientServerController.getInstance();
//    }

//    public String getManagerClientID() {
//        return managerClientID;
//    }

//    public static void requestToController(String request) {
//        mcsc.makeRmiRequest(managerClientID,EventID, request);
//    }

    public static void main(String[] args) {

        ClientServerController emccsc = ClientServerController.getInstance();
        EventManagerClient emc = new EventManagerClient();
        System.out.println("enter your id: ");
        emc.managerClientID = sc.next();
        System.out.println("enter your request : ");
        emc.requestType = RequestType.valueOf(sc.next());
        System.out.println("enter event id: ");
        emc.eventID = sc.next();
        System.out.println("enter event type: ");
        emc.eventType = EventType.valueOf(sc.next()) ;
        System.out.println("set booking capacity ");
        emc.bookingCapacity = sc.nextInt();
        emccsc.makeRmiRequestEventManager(emc.managerClientID, emc.requestType, emc.eventID, emc.eventType, emc.bookingCapacity);

    }


}
