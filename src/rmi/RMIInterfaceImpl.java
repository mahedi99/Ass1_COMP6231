package rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIInterfaceImpl extends UnicastRemoteObject implements RMIInterface {

    public RMIInterfaceImpl() throws RemoteException{
        super();
    }

    @Override
    public String getData(String data) throws RemoteException {
        return "hi";
    }
}
