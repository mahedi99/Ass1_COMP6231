package server.rmi;

import server.database.MessageModel;
import server.mtl_server.MtlDBController;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class OtwRMIInterfaceImpl extends UnicastRemoteObject implements RMIInterface {

    public OtwRMIInterfaceImpl() throws RemoteException{
        super();
    }

    @Override
    public String processRequest(MessageModel model) throws RemoteException {
        System.out.println(model.getClientID());
        System.out.println(model.getEventType());
        System.out.println(model.getEventID());

        switch (model.getClientID().substring(0, 2)){
            case "MTL" :
                MtlDBController controller = MtlDBController.getInstance();
                switch (model.getRequestType()) {
                    case ADD_EVENT:
                        controller.addEvent(model.getEventID(), model.getEventType(), model.getBookingCapacity());
                        break;
                    case BOOK_EVENT:
                        controller.bookEvent(model.getEventID(), model.getEventID(), model.getEventType());
                        break;
                }
                break;
            case "TOR" :


                break;
            case "OTW" :
                break;
        }


        return "successful";
    }
}
