package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIInterface extends Remote {

    public String getData(String data) throws RemoteException;

}
