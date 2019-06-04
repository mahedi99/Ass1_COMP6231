package server.mtl_server;

import server.Utils;
import server.database.RequestType;
import server.rmi.MtlRMIInterfaceImpl;

import java.io.IOException;
import java.net.*;
import java.rmi.Naming;
import java.rmi.RemoteException;


public class MtlServer {

    public static void main(String[] args) {

        new Thread(() -> {
            receive();
        }).start();

        try{
            Utils.startRegistry(Utils.MTL_SERVER_PORT);
            MtlRMIInterfaceImpl exportedObj = new MtlRMIInterfaceImpl();
            String registryURL = "rmi://localhost:" + Utils.MTL_SERVER_PORT + "/server";
            Naming.rebind(registryURL, exportedObj);
            System.out.println("Montreal Server ready.");
            //listRegistry(registryURL);
        }
        catch (Exception re) {
            System.out.println("Exception in MtlServer.main: " + re);
        }
    }

    private static void listRegistry(String registryURL)
            throws RemoteException, MalformedURLException {
        System.out.println("Registry " + registryURL + " contains: ");
        String [ ] names = Naming.list(registryURL);
        for (int i=0; i < names.length; i++)
            System.out.println(names[i]);
    }

    private static void receive(){
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket(Utils.MTL_SERVER_PORT +1);
            byte[] buffer = new byte[1000];
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);

                String[] data = new String(request.getData(), request.getOffset(), request.getLength()).split("\\|"); //data = "customerID eventID eventType"

                switch (RequestType.valueOf(data[2])){
                    case ADD_EVENT:
                        //OtwDBController.bookEvent(data[0], data[1]);
                        //controller.addEvent(data[0], data[1], );
                        break;
                    case BOOK_EVENT :
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
    public static String sendMsg(int serverPort, String UDPMsg){
        String response = "false";
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket();
           // byte[] message = "Hello".getBytes();
            InetAddress aHost = InetAddress.getByName("localhost");
            DatagramPacket request = new DatagramPacket(UDPMsg.getBytes(), UDPMsg.length(), aHost, serverPort);
            aSocket.send(request);
            System.out.println("Request message sent from the client to server with port number " + serverPort + " is: "
                    + new String(request.getData()));
            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

            aSocket.receive(reply);
            System.out.println("Reply received from the server with port number " + serverPort + " is: "
                    + new String(reply.getData()));
            response = new String(reply.getData()).trim();
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
        return response;
    }
}
