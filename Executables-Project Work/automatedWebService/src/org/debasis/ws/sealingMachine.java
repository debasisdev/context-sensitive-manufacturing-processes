package org.debasis.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(targetNamespace = "http://ws.debasis.org/")
public interface sealingMachine {

	@WebMethod(action = "getVersion")
	String getVersion();

	@WebMethod(action = "automate")
	String automate(@WebParam(name = "arg0") String task);

}