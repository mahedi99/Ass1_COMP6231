package server.otw_server;

import server.Utils;
import server.rmi.OtwRMIInterfaceImpl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class OtwServer {
    public static void main(String[] args) {

        new Thread(() -> {
            receive();
        }).start();

        try{
            startRegistry(5001);
            OtwRMIInterfaceImpl exportedObj = new OtwRMIInterfaceImpl();
            String registryURL = "rmi://localhost:" + 5001 + "/server";
            Naming.rebind(registryURL, exportedObj);
            System.out.println("Server ready.");
        }
        catch (Exception re) {
            System.out.println("Exception in HelloServer.main: " + re);
        }
    }

    private static void startRegistry(int RMIPortNum)
            throws RemoteException {
        try {
            Registry registry = LocateRegistry.getRegistry(RMIPortNum);
            registry.list();
        }
        catch (RemoteException e) {
            // No valid registry at that port.
            /**/     System.out.println("RMI registry cannot be located at port " + RMIPortNum);
                    LocateRegistry.createRegistry(RMIPortNum);
            /**/        System.out.println("RMI registry created at port " + RMIPortNum);
        }
    }

    private static void receive(){
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket(Utils.MTL_SERVER_PORT);
            byte[] buffer = new byte[1000];
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);

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
