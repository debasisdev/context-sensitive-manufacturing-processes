package org.debasis.ws;

/** 
 * Copyright 2016 Debasis Kar
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
**/

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
