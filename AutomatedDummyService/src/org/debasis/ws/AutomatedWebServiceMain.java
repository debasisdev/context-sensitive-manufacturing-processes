package org.debasis.ws;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(targetNamespace = "http://service.cmp.spi.iaas.uni_stuttgart/", name = "AutomaticService")
public class AutomatedWebServiceMain {
	
	public String automate(@WebParam(name="task") String task) {
    	String store = task.trim().toUpperCase();
    	String temp;
    	switch(store) {
    		case "SEAL":
    			temp = "Sealing is being carried out...";
    			break;
    		case "PREV":
    			temp = "Quality Check and Testing is Completed...";
    			break;
    		case "REPAIR":
    			temp = "Trying to Repair...";
    			break;
    		case "INSTALL":
    			temp = "New Sealing Machine is being Installed...";
    			break;
    		case "MAINTAIN":
    			temp = "Scheduled Maintenance is being done...";
    			break;
    		case "OPTIMIZE":
    			temp = "Optimizing resources in Factory...";
    			break;
    		case "COMPLETE":
    			temp = "Packing and Pallatizing is Finished.";
    			break;
    		case "SHIP":
    			temp = "Shipping will be Begun by the Logisitics Company.";
    			break;
    		default:
    			temp = "Something Broke The Process. Try to Repair!!";
    			break;
    	}
		return temp;
    }
}
