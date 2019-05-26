package client;

import rmi.RMIInterface;
import java.rmi.Naming;

public class CustomerClient {

    public static void main(String[] a){
        try {
            String registryURL = "rmi://localhost:" + 5000 + "/server";
            // find the remote object and cast it to an interface object
            RMIInterface h = (RMIInterface) Naming.lookup(registryURL);
            // invoke the remote method
            String message = h.getData("hi");
            System.out.println(message);
        } // end try
        catch (Exception e) {
            System.out.println("Exception in HelloClient: " + e);
        }
    }
}
