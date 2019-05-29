package server.rmi;

import server.database.MessageModel;
import server.mtl_server.MtlDBController;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MtlRMIInterfaceImpl extends UnicastRemoteObject implements RMIInterface {

    public MtlRMIInterfaceImpl() throws RemoteException{
        super();
    }

    @Override
    public String processRequest(MessageModel model) throws RemoteException {
        System.out.println(model.getClientID());
        System.out.println(model.getEventType());
        System.out.println(model.getEventID());

        String response = "false";
        MtlDBController controller = MtlDBController.getInstance();
        switch (model.getRequestType()) {
            case ADD_EVENT:
                response = controller.addEvent(model.getEventID(), model.getEventType(), model.getBookingCapacity());
                break;
            case BOOK_EVENT:
                response = controller.bookEvent(model.getClientID(), model.getEventID(), model.getEventType());
                break;
        }


        return response;
    }
}
