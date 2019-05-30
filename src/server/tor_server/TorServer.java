package server.tor_server;

import server.Utils;
import server.database.EventType;
import server.database.RequestType;
import server.rmi.MtlRMIInterfaceImpl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.lang.*;
import java.rmi.Naming;

public class TorServer {

    public static void main(String[] args) {




        //Establishing connection between servers
        new Thread(() -> {
            receive();
        }).start();

        try{
            Utils.startRegistry(Utils.TOR_SERVER_PORT);
            MtlRMIInterfaceImpl exportedObj = new MtlRMIInterfaceImpl();
            String registryURL = "rmi://localhost:" + Utils.TOR_SERVER_PORT + "/server";
            Naming.rebind(registryURL, exportedObj);
            System.out.println("Toronto Server ready.");
            //listRegistry(registryURL);
        }
        catch (Exception re) {
            System.out.println("Exception in TorServer.main: " + re);
        }
    }

    private static void receive(){
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket(Utils.TOR_SERVER_PORT);
            byte[] buffer = new byte[1000];
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);

                String[] data = new String(request.getData()).split("\\|");
                String response = "false";
                TorDBController controller = TorDBController.getInstance();
                switch (RequestType.valueOf(data[0])) {
                    case ADD_EVENT:
                        response = controller.addEvent(data[1], EventType.valueOf(data[2]), Integer.parseInt(data[3].trim()));
                        break;
                    case BOOK_EVENT:
                        response = controller.bookEvent(data[1], data[2], EventType.valueOf(data[3]));
                        break;
                }

                //replay back after processing the request
                DatagramPacket reply = new DatagramPacket(response.getBytes(), response.length(), request.getAddress(), request.getPort());
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
