package client;

import java.rmi.Naming;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import CORBAApp.CORBAInterface;
import CORBAApp.CORBAInterfaceHelper;
import server.Utils;
import server.database.EventType;
import server.database.MessageModel;
import server.database.RequestType;
import server.rmi.RMIInterface;

public class ClientServerController {

	static CORBAInterface corbaInterface = null;
	static NamingContextExt ncRef = null;

	// -ORBInitialPort 1050 -ORBInitialHost localhost
	org.omg.CORBA.Object objRef;

	private static ClientServerController csc = null;

	private ClientServerController() {

	}

	public static ClientServerController getInstance() {
		if (csc == null)
			csc = new ClientServerController();

		return csc;
	}

	public void makeRmiRequestEventManagerBookEvent(String managerID, String customerID, RequestType request,
			String eventID, EventType eventType) {
		// book event
		String registryURL = "";
		switch (customerID.substring(0, 3)) {
		case "TOR":
			registryURL = Utils.TOR_ORB_SERVER;
			break;
		case "MTL":
			registryURL = Utils.MTL_ORB_SERVER;
			break;
		case "OTW":
			registryURL = Utils.OTW_ORB_SERVER;
			break;
		}

		try {
			String[] args = null;
			ORB orb = ORB.init(args, null);
			objRef = orb.resolve_initial_references("NameService");
			ncRef = NamingContextExtHelper.narrow(objRef);
			corbaInterface = CORBAInterfaceHelper.narrow(ncRef.resolve_str(registryURL));
			String message = corbaInterface.processRequest(customerID, eventID, String.valueOf(eventType), 0,
					String.valueOf(request), "", "");
			System.out.println(message);
		} catch (Exception e) {
			e.getStackTrace();
		}
		LogUtils.writeToFile("Manager_ID_" + managerID + ".txt", " Client_ID_" + customerID + " Request: " + request
				+ '|' + " Event ID: " + eventID + "| Event Type: " + eventType);
	}

	public void makeRmiRequestEventManagerGetBookingSchedule(String managerID, String customerID, RequestType request) {
		// get booking schedule
		String registryURL = "";
		switch (customerID.substring(0, 3)) {
		case "TOR":
			registryURL = Utils.TOR_ORB_SERVER;
			break;
		case "MTL":
			registryURL = Utils.MTL_ORB_SERVER;
			break;
		case "OTW":
			registryURL = Utils.OTW_ORB_SERVER;
			break;
		}

		try {
			String[] args = null;
			ORB orb = ORB.init(args, null);
			objRef = orb.resolve_initial_references("NameService");
			ncRef = NamingContextExtHelper.narrow(objRef);
			corbaInterface = CORBAInterfaceHelper.narrow(ncRef.resolve_str(registryURL));
			String message = corbaInterface.processRequest(customerID, "", "", 0, String.valueOf(request), "", "");
			System.out.print(message);

		} catch (Exception e) {
			e.getStackTrace();
		}

		LogUtils.writeToFile("Manager_ID_" + managerID + ".txt",
				"Client_ID_" + customerID + ".txt" + '|' + " Request: " + request);

	}

	public void makeRmiRequestEventManagerCancelEvent(String managerID, String customerID, RequestType request,
			String eventID, EventType eventType) {
		// cancel event
		String registryURL = "";
		switch (customerID.substring(0, 3)) {
		case "TOR":
			registryURL = Utils.TOR_ORB_SERVER;
			break;
		case "MTL":
			registryURL = Utils.MTL_ORB_SERVER;
			break;
		case "OTW":
			registryURL = Utils.OTW_ORB_SERVER;
			break;
		}

		try {
			String[] args = null;
			ORB orb = ORB.init(args, null);
			objRef = orb.resolve_initial_references("NameService");
			ncRef = NamingContextExtHelper.narrow(objRef);
			corbaInterface = CORBAInterfaceHelper.narrow(ncRef.resolve_str(registryURL));
			String message = corbaInterface.processRequest(customerID, eventID, String.valueOf(eventType), 0, String.valueOf(request), "", "");
			System.out.print(message);

		} catch (Exception e) {
			e.getStackTrace();
		}

		LogUtils.writeToFile("Manager_ID_" + managerID + ".txt", "Customer ID: " + customerID + " | Event ID: "
				+ eventID + '|' + " Request: " + request + '|' + " Event type: " + eventType);

	}

	public void makeRmiRequestEventManagerRemoveEvent(String managerID, RequestType request, String eventID,
			EventType eventType) {

		String registryURL = "";
		switch (managerID.substring(0, 3)) {
		case "TOR":
			registryURL = Utils.TOR_ORB_SERVER;
			break;
		case "MTL":
			registryURL = Utils.MTL_ORB_SERVER;
			break;
		case "OTW":
			registryURL = Utils.OTW_ORB_SERVER;
			break;
		}

		try {
			String[] args = null;
			ORB orb = ORB.init(args, null);
			objRef = orb.resolve_initial_references("NameService");
			ncRef = NamingContextExtHelper.narrow(objRef);
			corbaInterface = CORBAInterfaceHelper.narrow(ncRef.resolve_str(registryURL));
			String message = corbaInterface.processRequest(managerID, eventID, String.valueOf(eventType), 0, String.valueOf(request), "", "");
			System.out.print(message);

		} catch (Exception e) {
			e.getStackTrace();
		}

		LogUtils.writeToFile("Client_ID_" + managerID + ".txt",
				" Request type: " + request + '|' + " Event type: " + eventType + '|' + "Event id: " + eventID);

	}

	public void makeRmiRequestEventManagerListEventAvailability(String managerID, RequestType request,
			EventType eventType) {

		String registryURL = "";
		switch (managerID.substring(0, 3)) {
		case "TOR":
			registryURL = Utils.TOR_ORB_SERVER;
			break;
		case "MTL":
			registryURL = Utils.MTL_ORB_SERVER;
			break;
		case "OTW":
			registryURL = Utils.OTW_ORB_SERVER;
			break;
		}

		try {
			String[] args = null;
			ORB orb = ORB.init(args, null);
			objRef = orb.resolve_initial_references("NameService");
			ncRef = NamingContextExtHelper.narrow(objRef);
			corbaInterface = CORBAInterfaceHelper.narrow(ncRef.resolve_str(registryURL));
			String message = corbaInterface.processRequest("", "", String.valueOf(eventType), 0, String.valueOf(request), "", "");
			System.out.print(message);

		} catch (Exception e) {
			e.getStackTrace();
		}

		LogUtils.writeToFile("Client_ID_" + managerID + ".txt",
				" Request type: " + request + '|' + " Event type: " + eventType);

	}

	public void makeRmiRequestEventManagerAddEvent(String managerID, RequestType request, String eventID,
			EventType eventType, int bookingCapacity) {
		if (!managerID.substring(0, 3).equals(eventID.substring(0, 3))) {
			System.out.println("Input Mismatches!");
			return;
		}
		String registryURL = "";
		switch (managerID.substring(0, 3)) {
		case "TOR":
			registryURL = Utils.TOR_ORB_SERVER;
			break;
		case "MTL":
			registryURL = Utils.MTL_ORB_SERVER;
			break;
		case "OTW":
			registryURL = Utils.OTW_ORB_SERVER;
			break;
		}

		try {
			String[] args = null;
			ORB orb = ORB.init(args, null);
			objRef = orb.resolve_initial_references("NameService");
			ncRef = NamingContextExtHelper.narrow(objRef);
			corbaInterface = CORBAInterfaceHelper.narrow(ncRef.resolve_str(registryURL));
			String message = corbaInterface.processRequest(managerID, eventID, String.valueOf(eventType), bookingCapacity, String.valueOf(request), "", "");
			System.out.print(message);

		} catch (Exception e) {
			e.getStackTrace();
		}

		LogUtils.writeToFile("Client_ID_" + managerID + ".txt", " Request type: " + request + '|' + " Event ID: "
				+ eventID + '|' + " Event type: " + eventType + '|' + " Booking Capacity: " + bookingCapacity);

	}
	
	



	public void makeRmiRequestCustomerCancelEvent(String customerID, RequestType request, String eventID,
			EventType eventType) {

		String registryURL = "";
		switch (customerID.substring(0, 3)) {
		case "TOR":
			registryURL = Utils.TOR_ORB_SERVER;
			break;
		case "MTL":
			registryURL = Utils.MTL_ORB_SERVER;
			break;
		case "OTW":
			registryURL = Utils.OTW_ORB_SERVER;
			break;
		}

		try {

			String[] args = null;
			ORB orb = ORB.init(args, null);
			objRef = orb.resolve_initial_references("NameService");
			ncRef = NamingContextExtHelper.narrow(objRef);
			corbaInterface = CORBAInterfaceHelper.narrow(ncRef.resolve_str(registryURL));
			String message = corbaInterface.processRequest(customerID, eventID, String.valueOf(eventType), 0, String.valueOf(request), "", "");
			System.out.print(message);

		} catch (Exception e) {
			e.getStackTrace();
		}

		LogUtils.writeToFile("Client_ID_" + customerID + ".txt",
				" Event ID: " + eventID + " Request type: " + request + '|' + " Event type: " + eventType);

	}

	public void makeRmiRequestCustomerGetBookingSchedule(String customerID, RequestType request) {

		String registryURL = "";
		switch (customerID.substring(0, 3)) {
		case "TOR":
			registryURL = Utils.TOR_ORB_SERVER;
			break;
		case "MTL":
			registryURL = Utils.MTL_ORB_SERVER;
			break;
		case "OTW":
			registryURL = Utils.OTW_ORB_SERVER;
			break;
		}

		try {

			String[] args = null;
			ORB orb = ORB.init(args, null);
			objRef = orb.resolve_initial_references("NameService");
			ncRef = NamingContextExtHelper.narrow(objRef);
			corbaInterface = CORBAInterfaceHelper.narrow(ncRef.resolve_str(registryURL));
			String message = corbaInterface.processRequest(customerID, "", "", 0, String.valueOf(request), "", "");
			System.out.print(message);

		} catch (Exception e) {
			e.getStackTrace();
		}

		LogUtils.writeToFile("Client_ID_" + customerID + ".txt", " Request type: " + request);

	}

	public void makeRmiRequestCustomerBookEvent(String customerID, RequestType request, String eventID,
			EventType eventType) {

		String registryURL = "";
		switch (customerID.substring(0, 3)) {
		case "TOR":
			registryURL = Utils.TOR_ORB_SERVER;
			break;
		case "MTL":
			registryURL = Utils.MTL_ORB_SERVER;
			break;
		case "OTW":
			registryURL = Utils.OTW_ORB_SERVER;
			break;
		}

		try {
			String[] args = null;
			ORB orb = ORB.init(args, null);
			objRef = orb.resolve_initial_references("NameService");
			ncRef = NamingContextExtHelper.narrow(objRef);
			corbaInterface = CORBAInterfaceHelper.narrow(ncRef.resolve_str(registryURL));
			String name = corbaInterface.processRequest(customerID, eventID, String.valueOf(eventType), 0, String.valueOf(request), "", "");
			System.out.println(name);

		} catch (Exception e) {
			e.getStackTrace();
		}

		LogUtils.writeToFile("Client_ID_" + customerID + ".txt",
				" Request type: " + request + " | Event ID: " + eventID + " | Event type: " + eventType);

	}
	
	public void makeRmiRequestCustomerSwapEvent(String customerID, String newEventID, EventType newEventType, String oldEventID, EventType oldEventType, RequestType requestType) {
        String registryURL="";
        switch (customerID.substring(0, 3)) {
		case "TOR":
			registryURL = Utils.TOR_ORB_SERVER;
			break;
		case "MTL":
			registryURL = Utils.MTL_ORB_SERVER;
			break;
		case "OTW":
			registryURL = Utils.OTW_ORB_SERVER;
			break;
		}

        try{
        	String[] args = null;
			ORB orb = ORB.init(args, null);
			objRef = orb.resolve_initial_references("NameService");
			ncRef = NamingContextExtHelper.narrow(objRef);
			corbaInterface = CORBAInterfaceHelper.narrow(ncRef.resolve_str(registryURL));
			String name = corbaInterface.processRequest(customerID, oldEventID, String.valueOf(oldEventType), 0, String.valueOf(requestType), newEventID, String.valueOf(newEventType));
			System.out.println(name);

        }catch(Exception e){
            e.getStackTrace();
        }

    }
	
	public void makeRmiRequestEventManagerSwapEvent(String managerID, String customerID, String newEventID, EventType newEventType, String oldEventID, EventType oldEventType, RequestType requestType) {
        String registryURL="";
        switch (customerID.substring(0, 3)) {
		case "TOR":
			registryURL = Utils.TOR_ORB_SERVER;
			break;
		case "MTL":
			registryURL = Utils.MTL_ORB_SERVER;
			break;
		case "OTW":
			registryURL = Utils.OTW_ORB_SERVER;
			break;
		}

        try{
        	String[] args = null;
			ORB orb = ORB.init(args, null);
			objRef = orb.resolve_initial_references("NameService");
			ncRef = NamingContextExtHelper.narrow(objRef);
			corbaInterface = CORBAInterfaceHelper.narrow(ncRef.resolve_str(registryURL));
			String name = corbaInterface.processRequest(customerID, oldEventID, String.valueOf(oldEventType), 0, String.valueOf(requestType), newEventID, String.valueOf(newEventType));
			System.out.println(name);

        }catch(Exception e){
            e.getStackTrace();
        }

    }

	public void makeRmiRequestCustomer(String customerID, RequestType request) {

		System.out.println("you are not allowed to perform this operation");
		LogUtils.writeToFile("Client_ID_" + customerID + ".txt", " Request Type: " + request);

	}
}
