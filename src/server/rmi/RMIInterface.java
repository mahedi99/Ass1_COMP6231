package server.rmi;

import server.database.MessageModel;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIInterface extends Remote {

    String processRequest(MessageModel data) throws RemoteException;

}
