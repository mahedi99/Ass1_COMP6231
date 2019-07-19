package server.corba;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import CORBAApp.CORBAInterface;
import CORBAApp.CORBAInterfaceHelper;
import CORBAApp.CORBAInterfacePOA;
import server.Utils;
import server.database.EventType;
import server.database.MessageModel;
import server.database.RequestType;
import server.tor_server.TorDBController;

public class TorCORBA extends CORBAInterfacePOA {

	private ORB orb = null;

	public static void main(String[] args) {

		new Thread(() -> receive()).start();

		try {
			// CORBA
			// create and initialize the ORB //
			ORB orbObj = ORB.init(args, null);

			// get reference to rootpoa &amp; activate
			POA rootPoa = POAHelper.narrow(orbObj.resolve_initial_references("RootPOA"));
			rootPoa.the_POAManager().activate();

			// create servant and register it with the ORB
			TorCORBA communication = new TorCORBA();
			communication.setOrb(orbObj);
			org.omg.CORBA.Object ref = rootPoa.servant_to_reference(communication);

			// and cast the reference to a CORBA reference
			CORBAInterface montrealCommunication = CORBAInterfaceHelper.narrow(ref);
			// get the root naming context
			// NameService invokes the transient name service
			org.omg.CORBA.Object objRef = orbObj.resolve_initial_references("NameService");
			// Use NamingContextExt, which is part of the
			// Interoperable Naming Service (INS) specification.
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

			// bind the Object Reference in Naming
			NameComponent path[] = ncRef.to_name(Utils.TOR_ORB_SERVER);
			ncRef.rebind(path, montrealCommunication);
			System.out.println("Toronto Server Ready...");
			for (;;) {
				orbObj.run();
			}
		} catch (Exception re) {
			System.out.println("Exception in MtlServer.main: " + re);
		}
	}

	private void setOrb(ORB orb_val) {
		orb = orb_val;
	}

	private static void receive() {
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket(Utils.TOR_SERVER_PORT);
			byte[] buffer = new byte[1000];
			while (true) {
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);

				String[] data = new String(request.getData(), request.getOffset(), request.getLength()).split("\\|");
				String response = "false";
				TorDBController controller = TorDBController.getInstance();

				switch (RequestType.valueOf(data[0].trim())) {
				case ADD_EVENT:
					response = controller.addEvent(data[1], EventType.valueOf(data[2]),
							Integer.parseInt(data[3].trim()));
					break;
				// case REMOVE_EVENT:
				// response = controller.removeEvent(model.getEventID(), model.getEventType());
				// break;
				case LIST_EVENT_AVAILABILITY:
					response = controller.listEventAvailabilityForOthers(EventType.valueOf(data[1].trim()));
					break;
				case BOOK_EVENT:
					response = controller.bookEvent(data[1], data[2], EventType.valueOf(data[3].trim()));
					break;
				case GET_BOOKING_SCHEDULE:
					response = controller.getBookingScheduleForOthers(data[1].trim());
					break;
				case CANCEL_EVENT:
					response = controller.cancelEvent(data[1].trim(), data[2].trim(),
							EventType.valueOf(data[3].trim()));
					break;
				case GET_TOTAL_EVENTS:
                    response = controller.getEventsForOneMonth(data[1].trim(), data[2].trim());
                    break;	
				case CHECK_IS_REGISTERED:
                    response = controller.getIsTheUserRegistered(data[1].trim(), data[2].trim(), EventType.valueOf(data[3].trim()));
                    break;
				case CHECK_CAPACITY:
					response = controller.checkCapacityForTheEvent(data[1].trim(), data[2].trim(), EventType.valueOf(data[3].trim()));
					break;
				}

				// replay back after processing the request
				DatagramPacket reply = new DatagramPacket(response.getBytes(), response.length(), request.getAddress(),
						request.getPort());
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

	public static String sendMsg(int serverPort, String UDPMsg) {
		String response = "false";
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket();
			InetAddress aHost = InetAddress.getByName("localhost");
			DatagramPacket request = new DatagramPacket(UDPMsg.getBytes(), UDPMsg.length(), aHost, serverPort);
			aSocket.send(request);
			System.out.println("Request message sent from the client to server with port number " + serverPort + " is: "
					+ new String(request.getData()));
			byte[] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

			aSocket.receive(reply);
			System.out.println("Reply received from the server with port number " + serverPort + " is : "
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

	@Override
	public void shutdown() {
		orb.shutdown(false);
	}

	@Override
	public String processRequest(String clientID, String eventID, String eventType, int bookingCapacity,
			String requestType, String newEventID, String newEventType) {
		MessageModel messageModel = new MessageModel();
		messageModel.setBookingCapacity(bookingCapacity);
		messageModel.setClientID(clientID);
		if(!eventType.equals("")) {
			messageModel.setEventType(EventType.valueOf(eventType));
		}
		if(!requestType.equals("")) {
			messageModel.setRequestType(RequestType.valueOf(requestType));
		}
		messageModel.setEventID(eventID);
		if(!newEventID.equals("")) {
			messageModel.setNewEventID(newEventID);
			messageModel.setNewEventType(EventType.valueOf(newEventType));
		}
		return this.processRequest(messageModel);
	}

	public String processRequest(MessageModel model) {
		String response = "false";
		TorDBController controller = TorDBController.getInstance();
		switch (model.getRequestType()) {
		case ADD_EVENT:
			response = controller.addEvent(model.getEventID(), model.getEventType(), model.getBookingCapacity());
			break;
		case REMOVE_EVENT:
			response = controller.removeEvent(model.getEventID(), model.getEventType());
			break;
		case LIST_EVENT_AVAILABILITY:
			response = controller.listEventAvailability(model.getEventType());
			break;
		case BOOK_EVENT:
			response = controller.bookEvent(model.getClientID(), model.getEventID(), model.getEventType());
			break;
		case GET_BOOKING_SCHEDULE:
			response = controller.getBookingSchedule(model.getClientID());
			break;
		case CANCEL_EVENT:
			response = controller.cancelEvent(model.getClientID(), model.getEventID(), model.getEventType());
			break;
		case SWAP_EVENT:
            response = controller.swapEvent(model.getClientID(), model.getEventID(), model.getEventType(), model.getNewEventID(), model.getNewEventType());
            break;
		}
		return response;
	}
}
