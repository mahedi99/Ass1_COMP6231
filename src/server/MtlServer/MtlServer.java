package server.MtlServer;

import rmi.RMIInterfaceImpl;
import server.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class MtlServer {
    MtlDBController controller = new MtlDBController();

    public static void main(String[] args) {

        InputStreamReader is = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(is);
        String portNum, registryURL;
        try{
            startRegistry(5000);
            RMIInterfaceImpl exportedObj = new RMIInterfaceImpl();
            registryURL = "rmi://localhost:" + 5000 + "/server";
            Naming.rebind(registryURL, exportedObj);
            System.out.println("Hello Server ready.");
        }
        catch (Exception re) {
            System.out.println("Exception in HelloServer.main: " + re);
        }
    }

    // This method starts a RMI registry on the local host, if it
    // does not already exists at the specified port number.
    private static void startRegistry(int RMIPortNum)
            throws RemoteException{
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

    private void receive(){
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket(Utils.MTL_SERVER_PORT);
            byte[] buffer = new byte[1000];
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);

                String[] data = new String(request.getData()).split(" "); //data = "customerID eventID eventType"

                switch (data[2]){
                    case Utils.ADD_EVENT:
                        //OtwDBController.bookEvent(data[0], data[1]);
                        //controller.addEvent(data[0], data[1], );
                        break;
                    case Utils.CANCEL_EVENT :
                        break;
                }


                //replay back after processing the request
                DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(), request.getPort());
                aSocket.send(reply);
            }

        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
    }
    private static void sendMsg(int serverPort){
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket();
           // byte[] message = "Hello".getBytes();
            InetAddress aHost = InetAddress.getByName("localhost");
            DatagramPacket request = new DatagramPacket("hello".getBytes(), "Hello".length(), aHost, serverPort);
            aSocket.send(request);
            System.out.println("Request message sent from the client to server with port number " + serverPort + " is: "
                    + new String(request.getData()));
            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

            aSocket.receive(reply);
            System.out.println("Reply received from the server with port number " + serverPort + " is: "
                    + new String(reply.getData()));
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
    }
}
