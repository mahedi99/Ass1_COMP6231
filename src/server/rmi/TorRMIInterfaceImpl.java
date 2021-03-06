package server.rmi;

import server.database.MessageModel;
import server.tor_server.TorDBController;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class TorRMIInterfaceImpl extends UnicastRemoteObject implements RMIInterface {

    public TorRMIInterfaceImpl() throws RemoteException{
        super();
    }

    @Override
    public String processRequest(MessageModel model) throws RemoteException {
        String response = "false";
        TorDBController controller = TorDBController.getInstance();
        switch (model.getRequestType()) {
            case ADD_EVENT:
                response = controller.addEvent(model.getEventID(), model.getEventType(), model.getBookingCapacity());
                break;
            case REMOVE_EVENT:
                response = controller.removeEvent(model.getEventID(), model.getEventType());
                break;
            case LIST_EVENT_AVAILABILITY:
                response = controller.listEventAvailability(model.getEventType());
                break;
            case BOOK_EVENT:
                response = controller.bookEvent(model.getClientID(), model.getEventID(), model.getEventType());
                break;
            case GET_BOOKING_SCHEDULE:
                response = controller.getBookingSchedule(model.getClientID());
                break;
            case CANCEL_EVENT:
                response = controller.cancelEvent(model.getClientID(), model.getEventID(), model.getEventType());
                break;
        }
        return response;
    }
}
