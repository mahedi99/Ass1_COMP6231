package client;


import server.database.EventType;
import server.database.MessageModel;
import server.database.RequestType;
import server.rmi.RMIInterface;
import server.Utils;

import java.rmi.Naming;

public class ClientServerController {

    private static ClientServerController csc = null;

    private ClientServerController() {

    }

    public static ClientServerController getInstance() {
        if(csc == null)
            csc = new ClientServerController();

        return csc;
    }



    public void makeRmiRequestEventManager(String ID, RequestType request, String eventID, EventType eventType, int bookingCapacity ) {
            System.out.print("client ID: "+ID+" eventID: "+eventID+" request: "+request);
            String registryURL= "rmi://localhost:5000/hi";

            //System.out.print("\nregistry url: "+ registryURL);


        MessageModel messageModel = new MessageModel();
        messageModel.setClientID(ID);
        messageModel.setRequestType(request);
        messageModel.setEventID(eventID);
        messageModel.setEventType(eventType);
        messageModel.setBookingCapacity(bookingCapacity);

       try{
           RMIInterface rmii = (RMIInterface) Naming.lookup(registryURL);
           String message=rmii.processRequest(messageModel);
           System.out.print(message);

       }catch(Exception e){
           e.getStackTrace();
       }
    }

    public void makeRmiRequestCustomer(String ID, RequestType request, String eventID, EventType eventType) {
        System.out.print("client ID: "+ID+" eventID: "+eventID+" request: "+request);
        String registryURL= "rmi://localhost:5000/hi";

        //System.out.print("\nregistry url: "+ registryURL);

        MessageModel messageModel = new MessageModel();
        messageModel.setClientID(ID);
        messageModel.setRequestType(request);
        messageModel.setEventID(eventID);
        messageModel.setEventType(eventType);

        try{

            RMIInterface rmii = (RMIInterface) Naming.lookup(registryURL);
            String message=rmii.processRequest(messageModel);
            System.out.print(message);

        }catch(Exception e){
            e.getStackTrace();
        }
    }
}
