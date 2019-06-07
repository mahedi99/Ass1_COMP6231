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



    public void makeRmiRequestEventManagerBookEvent(String managerID, String customerID, RequestType request, String eventID, EventType eventType) {
        //book event
        String registryURL="";
        switch (customerID.substring(0,3)){
            case "TOR" :
                registryURL= "rmi://localhost:"+Utils.TOR_SERVER_PORT+"/server";
                break;
            case "MTL" :
                registryURL= "rmi://localhost:"+Utils.MTL_SERVER_PORT+"/server";
                break;
            case "OTW" :
                registryURL= "rmi://localhost:"+Utils.OTW_SERVER_PORT+"/server";
                break;
        }

        MessageModel messageModel = new MessageModel();
        messageModel.setClientID(customerID);
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

        LogUtils.writeToFile("Manager_ID_"+managerID+".txt", " Client_ID_"+customerID+" Request: "+request+'|'+" Event ID: "+eventID+"| Event Type: "+eventType);

    }

    public void makeRmiRequestEventManagerGetBookingSchedule(String managerID, String customerID, RequestType request) {
        //get booking schedule
        String registryURL="";
        switch (customerID.substring(0,3)){
            case "TOR" :
                registryURL= "rmi://localhost:"+Utils.TOR_SERVER_PORT+"/server";
                break;
            case "MTL" :
                registryURL= "rmi://localhost:"+Utils.MTL_SERVER_PORT+"/server";
                break;
            case "OTW" :
                registryURL= "rmi://localhost:"+Utils.OTW_SERVER_PORT+"/server";
                break;
        }

        MessageModel messageModel = new MessageModel();
        messageModel.setClientID(customerID);
        messageModel.setRequestType(request);

        try{

            RMIInterface rmii = (RMIInterface) Naming.lookup(registryURL);
            String message=rmii.processRequest(messageModel);
            System.out.print(message);

        }catch(Exception e){
            e.getStackTrace();
        }

        LogUtils.writeToFile("Manager_ID_"+managerID+".txt","Client_ID_"+customerID+".txt"+'|'+" Request: "+request);

    }

    public void makeRmiRequestEventManagerCancelEvent(String managerID, String customerID, RequestType request, String eventID, EventType eventType) {
        //cancel event
        String registryURL="";
        switch (customerID.substring(0,3)){
            case "TOR" :
                registryURL= "rmi://localhost:"+Utils.TOR_SERVER_PORT+"/server";
                break;
            case "MTL" :
                registryURL= "rmi://localhost:"+Utils.MTL_SERVER_PORT+"/server";
                break;
            case "OTW" :
                registryURL= "rmi://localhost:"+Utils.OTW_SERVER_PORT+"/server";
                break;
        }

        MessageModel messageModel = new MessageModel();
        messageModel.setClientID(customerID);
        messageModel.setRequestType(request);
        messageModel.setEventID(eventID);
        messageModel.setEventType(eventType);

        try{

            RMIInterface rmi = (RMIInterface) Naming.lookup(registryURL);
            String message=rmi.processRequest(messageModel);
            System.out.print(message);

        }catch(Exception e){
            e.getStackTrace();
        }

        LogUtils.writeToFile("Manager_ID_"+managerID+".txt" ,  "Customer ID: "+customerID+" | Event ID: "+eventID+'|'+" Request: "+request+'|'+" Event type: "+eventType);

    }

    public void makeRmiRequestEventManagerRemoveEvent(String managerID, RequestType request, String eventID, EventType eventType ) {

        String registryURL="";
        switch (managerID.substring(0,3)){
            case "TOR" :
                registryURL= "rmi://localhost:"+Utils.TOR_SERVER_PORT+"/server";
                break;
            case "MTL" :
                registryURL= "rmi://localhost:"+Utils.MTL_SERVER_PORT+"/server";
                break;
            case "OTW" :
                registryURL= "rmi://localhost:"+Utils.OTW_SERVER_PORT+"/server";
                break;
        }


        MessageModel messageModel = new MessageModel();
        messageModel.setClientID(managerID);
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


        LogUtils.writeToFile("Client_ID_"+managerID+".txt", " Request type: "+request+'|'+" Event type: "+eventType+'|'+"Event id: "+eventID);

    }

    public void makeRmiRequestEventManagerListEventAvailability(String managerID, RequestType request, EventType eventType) {

        String registryURL="";
        switch (managerID.substring(0,3)){
            case "TOR" :
                registryURL= "rmi://localhost:"+Utils.TOR_SERVER_PORT+"/server";
                break;
            case "MTL" :
                registryURL= "rmi://localhost:"+Utils.MTL_SERVER_PORT+"/server";
                break;
            case "OTW" :
                registryURL= "rmi://localhost:"+Utils.OTW_SERVER_PORT+"/server";
                break;
        }

        MessageModel messageModel = new MessageModel();
        //messageModel.setClientID(managerID);
        messageModel.setRequestType(request);
        messageModel.setEventType(eventType);


        try{
            RMIInterface rmii = (RMIInterface) Naming.lookup(registryURL);
            String message=rmii.processRequest(messageModel);
            System.out.print(message);

        }catch(Exception e){
            e.getStackTrace();
        }

        LogUtils.writeToFile("Client_ID_"+managerID+".txt", " Request type: "+request+'|'+" Event type: "+eventType);

    }

    public void makeRmiRequestEventManagerAddEvent(String managerID, RequestType request, String eventID, EventType eventType, int bookingCapacity ) {

        String registryURL="";
        switch (managerID.substring(0,3)){
            case "TOR" :
                registryURL= "rmi://localhost:"+Utils.TOR_SERVER_PORT+"/server";
                break;
            case "MTL" :
                registryURL= "rmi://localhost:"+Utils.MTL_SERVER_PORT+"/server";
                break;
            case "OTW" :
                registryURL= "rmi://localhost:"+Utils.OTW_SERVER_PORT+"/server";
                break;
        }

        MessageModel messageModel = new MessageModel();
        messageModel.setClientID(managerID);
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


       LogUtils.writeToFile("Client_ID_"+managerID+".txt", " Request type: "+request+'|'+" Event ID: "+eventID+'|'+" Event type: "+eventType+'|'+" Booking Capacity: "+bookingCapacity);

    }

    public void makeRmiRequestCustomerCancelEvent(String customerID, RequestType request, String eventID, EventType eventType) {

        String registryURL="";
        switch (customerID.substring(0,3)){
            case "TOR" :
                registryURL= "rmi://localhost:"+Utils.TOR_SERVER_PORT+"/server";
                break;
            case "MTL" :
                registryURL= "rmi://localhost:"+Utils.MTL_SERVER_PORT+"/server";
                break;
            case "OTW" :
                registryURL= "rmi://localhost:"+Utils.OTW_SERVER_PORT+"/server";
                break;
        }

            MessageModel messageModel = new MessageModel();
            messageModel.setClientID(customerID);
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

            LogUtils.writeToFile("Client_ID_"+customerID+".txt", " Event ID: "+eventID+" Request type: "+request+'|'+" Event type: "+eventType);

    }

    public void makeRmiRequestCustomerGetBookingSchedule(String customerID, RequestType request) {

        String registryURL="";
        switch (customerID.substring(0,3)){
            case "TOR" :
                registryURL= "rmi://localhost:"+Utils.TOR_SERVER_PORT+"/server";
                break;
            case "MTL" :
                registryURL= "rmi://localhost:"+Utils.MTL_SERVER_PORT+"/server";
                break;
            case "OTW" :
                registryURL= "rmi://localhost:"+Utils.OTW_SERVER_PORT+"/server";
                break;
        }

            MessageModel messageModel = new MessageModel();
            messageModel.setClientID(customerID);
            messageModel.setRequestType(request);

            try{

                RMIInterface rmii = (RMIInterface) Naming.lookup(registryURL);
                String message=rmii.processRequest(messageModel);
                System.out.print(message);

            }catch(Exception e){
                e.getStackTrace();
            }

            LogUtils.writeToFile("Client_ID_"+customerID+".txt"," Request type: "+request );

    }

    public void makeRmiRequestCustomerBookEvent(String customerID, RequestType request, String eventID,  EventType eventType) {

        String registryURL="";
        switch (customerID.substring(0,3)){
            case "TOR" :
                registryURL= "rmi://localhost:"+Utils.TOR_SERVER_PORT+"/server";
                break;
            case "MTL" :
                registryURL= "rmi://localhost:"+Utils.MTL_SERVER_PORT+"/server";
                break;
            case "OTW" :
                registryURL= "rmi://localhost:"+Utils.OTW_SERVER_PORT+"/server";
                break;
        }

            MessageModel messageModel = new MessageModel();
            messageModel.setClientID(customerID);
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

            LogUtils.writeToFile("Client_ID_"+customerID+".txt", " Request type: "+request+" | Event ID: "+eventID+" | Event type: "+eventType);

    }

    public void makeRmiRequestCustomer(String customerID, RequestType request) {

        System.out.println("you are not allowed to perform this operation");
        LogUtils.writeToFile("Client_ID_"+customerID+".txt", " Request Type: "+request);

    }
}
