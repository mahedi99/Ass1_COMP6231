package server.MtlServer;

import server.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


public class MtlServer {
    MtlDBController controller = new MtlDBController();
    public static void main(String[] args) {

        MtlServer mtlServer = new MtlServer();
        //Establishing connection between servers
        mtlServer.receive();

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
                    case Utils.BOOK_EVENT:
                        //OtwDBController.bookEvent(data[0], data[1]);

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
