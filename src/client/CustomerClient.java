package client;

import server.rmi.RMIInterface;
import server.database.EventType;
import server.database.RequestType;
import java.util.Scanner;

import java.rmi.Naming;

public class CustomerClient {
    private static Scanner sc = new Scanner(System.in);
    private RequestType requestType;
    private String customerClientID;
    private String eventID;
    private EventType eventType;



    public static void main(String[] a){


        ClientServerController cccsc = ClientServerController.getInstance();
        CustomerClient cc = new CustomerClient();
        System.out.println("enter your id: ");
        cc.customerClientID = sc.next();
        System.out.println("enter your request : ");
        cc.requestType = RequestType.valueOf(sc.next());
        System.out.println("enter event id: ");
        cc.eventID = sc.next();
        System.out.println("enter event type: ");
        cc.eventType = EventType.valueOf(sc.next()) ;
        cccsc.makeRmiRequestCustomer(cc.customerClientID, cc.requestType, cc.eventID, cc.eventType);

        try {
            String registryURL = "rmi://localhost:" + 7000 + "/server";
            // find the remote object and cast it to an interface object
            RMIInterface h = (RMIInterface) Naming.lookup(registryURL);
            // invoke the remote method



//            model.setClientID("OTWC2345");
//            model.setEventID("TORE100519");
//            model.setEventType(EventType.CONFERENCE);
//            model.setRequestType(RequestType.CANCEL_EVENT);
//            model.setBookingCapacity(50);

//            String message = h.processRequest(model);
//            System.out.println(message);
        }
        catch (Exception e) {
            System.out.println("Exception in CustomerClient: " + e);
        }
    }
}
