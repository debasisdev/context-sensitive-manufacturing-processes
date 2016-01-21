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
 
    @SuppressWarnings("finally")
	public String automate(String task) {
    	String store = task.trim().toUpperCase();
    	String temp;
    	switch(store) {
    		case "SEAL":
    			temp = "Sealing is being carried out...";
    			break;
    		case "PREV":
    			temp = "Quality Check and Testing Is Completed...";
    			break;
    		case "REPAIR":
    			temp = "Trying to Repair...";
    			break;
    		case "INSTALL":
    			temp = "New Sealing Machine is being Installed...";
    			break;
    		case "MAINTAIN":
    			temp = "Schduled Maintance is being done...";
    			break;
    		case "OPTIMIZE":
    			temp = "Optimizing resources in Factory...";
    			break;
    		case "COMPLETE":
    			temp = "Packing and Pallatizing Is Finished.";
    			break;
    		default:
    			temp = "Something Broke The Process. Try to Repair!!";
    			break;
    	}
    	try {
			Thread.sleep(8000);
		} catch (InterruptedException e) {
			temp = "Exception Occurred While Carrying Out The Task.";
		} finally{
			return temp;
		}
    }
}
