package org.debasis.ws;

import javax.jws.WebService;

@WebService(targetNamespace = "http://ws.debasis.org/", 
endpointInterface = "org.debasis.ws.sealingMachine", 
portName = "automatedWebServiceMainPort", 
serviceName = "automatedWebServiceMainService")
public class automatedWebServiceMain implements sealingMachine {
	
	public String getVersion() {
        return "1.2";
    }

	public String automate(String task) {
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
