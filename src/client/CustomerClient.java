package client;

import server.rmi.RMIInterface;
import server.database.EventType;
import server.database.MessageModel;
import server.database.RequestType;

import java.rmi.Naming;

public class CustomerClient {

    public static void main(String[] a){
        try {
            String registryURL = "rmi://localhost:" + 7000 + "/server";
            // find the remote object and cast it to an interface object
            RMIInterface h = (RMIInterface) Naming.lookup(registryURL);
            // invoke the remote method

            MessageModel model = new MessageModel();
            model.setClientID("OTWC2345");
            model.setEventID("TORE100519");
            model.setEventType(EventType.CONFERENCE);
            model.setRequestType(RequestType.CANCEL_EVELT);
            model.setBookingCapacity(50);

            String message = h.processRequest(model);
            System.out.println(message);
        }
        catch (Exception e) {
            System.out.println("Exception in HelloClient: " + e);
        }
    }
}
