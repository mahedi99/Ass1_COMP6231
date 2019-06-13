package server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Utils {
    public static final int MTL_SERVER_PORT = 5000;
    public static final int TOR_SERVER_PORT = 6000;
    public static final int OTW_SERVER_PORT = 7000;

    public static void startRegistry(int RMIPortNum)
            throws RemoteException {
        try {
            Registry registry = LocateRegistry.getRegistry(RMIPortNum);
            registry.list();
        }
        catch (RemoteException e) {
            // No valid registry at that port.
            /**/     System.out.println
/**/        ("RMI registry cannot be located at port "
        /**/        + RMIPortNum);
            Registry registry =
                    LocateRegistry.createRegistry(RMIPortNum);
            /**/        System.out.println(
                    /**/           "RMI registry created at port " + RMIPortNum);
        }
    }
}
